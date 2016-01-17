package com.example.piccache.adapter;

import java.io.BufferedOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.piccache.utils.ImageCacheUtil;
import com.example.piccachedemo.R;

/**
 * GridView的适配器，通过图片多级缓存，展示图片到界面上
 * 
 * @author liuzhiyuan
 */
public class MyPicAdapter extends BaseAdapter {

	protected static final String TAG = "MyPicAdapter";
	/**
	 * 图片多级缓存的工具类，本代码的重点
	 */
	private ImageCacheUtil imageCacheUtil;

	private BufferedOutputStream mDiskLruCache;

	private GridView gridView;

	private String[] imageUrls;

	private Context context;

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (ImageCacheUtil.SUCCESS == msg.what) {
				Log.i(TAG, "success");
				// 获取handler传递过来的tag值
				Bundle peekData = msg.peekData();
				String tag = peekData.getString("tag");
				Bitmap bitmap = (Bitmap) msg.obj;
				// 根据这个tag，来获取到之前存了该tag的imageView
				ImageView imageView = (ImageView) gridView.findViewWithTag(tag);
				if (imageView != null && bitmap != null) {
					imageView.setImageBitmap(bitmap);
				}
			}
		};
	};

	public MyPicAdapter(Context context, String[] imageUrls, GridView gridView) {
		this.context = context;
		this.imageUrls = imageUrls;
		this.gridView = gridView;
		imageCacheUtil = new ImageCacheUtil(context, handler);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = View.inflate(context, R.layout.photo_layout, null);
			convertView.setTag(viewHolder);
			viewHolder.imageView = (ImageView) convertView
					.findViewById(R.id.photo);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		// 给ImageView设置一个Tag，保证异步加载图片时不会乱序
		viewHolder.imageView.setTag(imageUrls[position]);
		// 暂时放一张空的图片
		viewHolder.imageView.setImageResource(R.drawable.empty_photo);
		Bitmap bitmap = imageCacheUtil.getBitmap(imageUrls[position]);
		// 如果bitmap！=null,说明图片是从内存或者是从本地缓存中读取到了
		if (bitmap != null) {
			viewHolder.imageView.setImageBitmap(bitmap);
		}
		return convertView;
	}

	/**
	 * @author liuzhiyuan ListView的代码优化类
	 */
	class ViewHolder {
		ImageView imageView;
	}

	@Override
	public int getCount() {
		return imageUrls.length;
	}

	@Override
	public Object getItem(int position) {
		return imageUrls[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * 刷新
	 */
	public void flush() {
		imageCacheUtil.fluchCache();

	}

}