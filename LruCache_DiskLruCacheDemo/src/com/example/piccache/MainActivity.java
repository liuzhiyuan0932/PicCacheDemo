package com.example.piccache;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Adapter;
import android.widget.GridView;

import com.example.piccache.adapter.MyPicAdapter;
import com.example.piccache.utils.Images;
import com.example.piccachedemo.R;

/**
 * 使用GridView展示图片
 * 
 * @author liuzhiyuan
 */
public class MainActivity extends Activity {

	/**
	 * GridView的适配器
	 */
	private MyPicAdapter mAdapter;

	private GridView gridView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		gridView = (GridView) findViewById(R.id.gridView);
		// 获取图片地址
		String[] imageUrls = Images.imageUrls;
		//实例化适配器
		mAdapter = new MyPicAdapter(this, imageUrls, gridView);
		// 设置适配器
		gridView.setAdapter(mAdapter);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 在onPause时执行
		mAdapter.flush();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}