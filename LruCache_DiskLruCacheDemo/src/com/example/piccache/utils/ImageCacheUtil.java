package com.example.piccache.utils;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import libcore.io.DiskLruCache;
import libcore.io.DiskLruCache.Snapshot;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.Log;

/**
 * 这个工具类是进行图片下载使用了多级缓存机制。使用了lruCache和diskLruCache相结合的方式，对于从内存中，和从本地缓存中，可以直接获取到图片
 * ，但是对于从网络下载图片的类型，
 * 需要注意在调用的类中实例化一个handler,在handler中根据tag来取出网络下载的图片,默认的tag类型是listView中的position
 * journal文件你打开以后呢，是这个格式；
 * 
 * libcore.io.DiskLruCache 1 1 1
 * 
 * DIRTY c3bac86f2e7a291a1a200b853835b664 CLEAN c3bac86f2e7a291a1a200b853835b664
 * 4698 READ c3bac86f2e7a291a1a200b853835b664 DIRTY
 * c59f9eec4b616dc6682c7fa8bd1e061f CLEAN c59f9eec4b616dc6682c7fa8bd1e061f 4698
 * READ c59f9eec4b616dc6682c7fa8bd1e061f DIRTY be8bdac81c12a08e15988555d85dfd2b
 * CLEAN be8bdac81c12a08e15988555d85dfd2b 99 READ
 * be8bdac81c12a08e15988555d85dfd2b DIRTY 536788f4dbdffeecfbb8f350a941eea3
 * REMOVE 536788f4dbdffeecfbb8f350a941eea3
 * 
 * 17 首先看前五行：
 * 
 * 第一行固定字符串libcore.io.DiskLruCache 第二行DiskLruCache的版本号，源码中为常量1
 * 第三行为你的app的版本号，当然这个是你自己传入指定的 第四行指每个key对应几个文件，一般为1 第五行，空行
 * ok，以上5行可以称为该文件的文件头，DiskLruCache初始化的时候，如果该文件存在需要校验该文件头。
 * 
 * 接下来的行，可以认为是操作记录。
 * 
 * DIRTY
 * 表示一个entry正在被写入（其实就是把文件的OutputStream交给你了）。那么写入分两种情况，如果成功会紧接着写入一行CLEAN的记录；
 * 如果失败，会增加一行REMOVE记录。 REMOVE除了上述的情况呢，当你自己手动调用remove(key)方法的时候也会写入一条REMOVE记录。
 * READ就是说明有一次读取的记录。 每个CLEAN的后面还记录了文件的长度，注意可能会一个key对应多个文件，那么就会有多个数字（参照文件头第四行）。
 * 从这里看出，只有CLEAN且没有REMOVE的记录，才是真正可用的Cache Entry记录。
 * 
 * 分析完journal文件，首先看看DiskLruCache的创建的代码。
 * 
 * @author liuzhiyuan
 * 
 */
public class ImageCacheUtil {
	private static final String tag = "ImageCacheUtil";

	public static final int SUCCESS = 100;
	public static final int FAIL = 101;
	private LruCache<String, Bitmap> lruCache;
	private ExecutorService newFixedThreadPool;

	private Handler handler;

	private DiskLruCache mDiskLruCache;

	private Snapshot snapShot;

	private FileInputStream fileInputStream;

	private FileDescriptor fileDescriptor;

	/**
	 * 调用这个工具类，需要传递两个参数
	 * 
	 * @param context
	 *            上下文
	 * @param handler
	 */
	public ImageCacheUtil(Context context, Handler handler) {
		// 获取当前内存的大小，为自己的程序定义一个最大使用内存
		int maxSize = (int) (Runtime.getRuntime().maxMemory() / 8);
		// 定义一个LruCache 这个类中维护了一套算法，可以将最近最不长用的数据进行清除
		lruCache = new LruCache<String, Bitmap>(maxSize) {
			// 在构造方法中重写以下sizeOf方法，在lruCache中默认值是1
			@Override
			protected int sizeOf(String key, Bitmap value) {
				// getRowBytes()一行上面对应像素点占用的大小*value.getHeight()行号
				return value.getRowBytes() * value.getHeight();
			}
		};
		try {
			// 获取图片缓存路径
			File cacheDir = getDiskCacheDir(context, "ImageCache");
			// 如果缓存路径不存在
			if (!cacheDir.exists()) {
				// 创建出这个路径来
				cacheDir.mkdirs();
			}
			// 创建出这样一个DiskLruCache对象来，并且规定本应用图片的最大缓存大小为10兆
			mDiskLruCache = DiskLruCache.open(cacheDir, getAppVersion(context),
					1, 10 * 1024 * 1024);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// 2*cup核数+1 创建一个线程池，一般这个线程池的线程个数是手机核数*2+1
		newFixedThreadPool = Executors.newFixedThreadPool(5);
		this.handler = handler;
	}

	public Bitmap getBitmap(String imgUrl) {
		// position为确认给那个imageview控件使用的tag
		Bitmap bitmap = null;
		// 1,先去内存中去取，这样的效率最高
		bitmap = lruCache.get(imgUrl);
		if (bitmap != null) {
			Log.i(tag, "从内存中获取到的图片");
			return bitmap;
		}
		// 2,如果内存中没有数据，再到文件中去取，效率次于内存
		bitmap = getBitmapFromLocal(imgUrl);
		if (bitmap != null) {
			Log.i(tag, "从文件中获取到的图片");
			return bitmap;
		}
		// 3,如果文件中没有数据，去网络去下载,效率最低
		getBitmapFromNet(imgUrl);
		Log.i(tag, "网络获取到的图片.......");
		return null;
	}

	/**
	 * 创建一个线程
	 * 
	 * @author Administrator
	 * 
	 */
	class RunnableTask implements Runnable {
		private String imageUrl;

		public RunnableTask(String imageUrl) {
			// 想要使用该线程，需要将图片的路径传递过来
			this.imageUrl = imageUrl;
		}

		@Override
		public void run() {
			// 访问网络,下载图片
			try {
				URL url = new URL(imageUrl);
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				// 读取超时时间()
				connection.setReadTimeout(5000);
				// 链接超时时间()
				connection.setConnectTimeout(5000);
				connection.setRequestMethod("GET");
				InputStream inputStream = connection.getInputStream();
				Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

				// 消息机制()
				Message msg = new Message();
				msg.what = SUCCESS;
				msg.obj = bitmap;
				Bundle bundle = new Bundle();
				bundle.putString("tag", imageUrl);
				// 这里是为了将下载图片时传来tag通过handler发给主线程，在主线程中更新界面不会出现图片错乱的现象
				msg.setData(bundle);
				handler.sendMessage(msg);

				// 内存缓存
				lruCache.put(imageUrl, bitmap);
				// 写入文件
				writeToLocal(imageUrl, bitmap);

				return;
			} catch (Exception e) {
				e.printStackTrace();
			}

			Message msg = new Message();
			msg.what = FAIL;
			handler.sendMessage(msg);
		}
	}

	private void writeToLocal(String imageUrl, Bitmap bitmap) {
		try {
			// 将imageUrl转化成Md5格式的字符串
			String key = MD5Encoder.encode(imageUrl);
			// 通过这个字符串获取到的就是图片存储的文件名称
			DiskLruCache.Editor editor = mDiskLruCache.edit(key);
			if (editor != null) {
				// 当写入到文件时，通过bitmap的compress方法，将图片写入的输出流中
				OutputStream outputStream = editor.newOutputStream(0);
				bitmap.compress(CompressFormat.JPEG, 100, outputStream);
				// 不要忘记还需要commit一下
				editor.commit();
			} else {
				editor.abort();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getBitmapFromNet(String url) {
		newFixedThreadPool.execute(new RunnableTask(url));
	}

	/**
	 * 从本地缓存中获取图片
	 * 
	 * @param imgUrl
	 *            图片路径
	 * @return 图片
	 */
	private Bitmap getBitmapFromLocal(String imgUrl) {

		try {
			// 生成图片URL对应的key
			String key = MD5Encoder.encode(imgUrl);
			// 查找key对应的缓存
			snapShot = mDiskLruCache.get(key);
			if (snapShot == null) {
				return null;
			}
			if (snapShot != null) {
				// 根据索引值获取一个文件流
				fileInputStream = (FileInputStream) snapShot.getInputStream(0);
				// 通过文件描述符
				fileDescriptor = fileInputStream.getFD();
			}
			Bitmap bitmap = null;
			if (fileDescriptor != null) {
				// 根据文件描述符，解析一个文件流，获取bitmap
				bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
			}
			if (bitmap != null) {
				// 将Bitmap对象添加到内存缓存当中
				lruCache.put(imgUrl, bitmap);
			}
			return bitmap;
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			// 在最后，扫尾的工作
			if (fileDescriptor == null && fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (IOException e) {
				}
			}
		}
		return null;
	}

	/**
	 * 根据传入的uniqueName获取硬盘缓存的路径地址。 如果sd卡是挂载状态，就将缓存存储到sd卡上，否则，将缓存存到当前程序的包名下的缓存目录中
	 */
	public File getDiskCacheDir(Context context, String uniqueName) {
		String cachePath;
		// 判断sd卡是否挂载
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			// 获取缓存目录
			cachePath = context.getExternalCacheDir().getPath();
		} else {
			cachePath = context.getCacheDir().getPath();
		}
		// 拼接缓存文件路径
		return new File(cachePath + File.separator + uniqueName);
	}

	/**
	 * 获取当前应用程序的版本号。
	 */
	public int getAppVersion(Context context) {
		try {
			// 获取包管理者，然后拿到程序的版本号
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return info.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 1;
	}

	/**
	 * 将缓存记录同步到journal文件中。供Activity在onPause的时候调用
	 */
	public void fluchCache() {
		if (mDiskLruCache != null) {
			try {
				mDiskLruCache.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
