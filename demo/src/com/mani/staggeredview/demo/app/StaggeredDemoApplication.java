package com.mani.staggeredview.demo.app;

import android.app.Application;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.mani.staggeredview.demo.volley.BitmapLruCache;

public class StaggeredDemoApplication extends Application {

	private static Context applicationContext;
	private static RequestQueue mRequestQueue;
	private static ImageLoader mImageLoader;
	private static BitmapLruCache mBitmapCache;

	public static boolean INIT_FLAG = true;

	public void onCreate() {
		super.onCreate();

		applicationContext = this.getApplicationContext();
		
		mRequestQueue = Volley.newRequestQueue(applicationContext);
		long size = Runtime.getRuntime().maxMemory()/4;
		mBitmapCache = new BitmapLruCache(50);//(int)size);
		mImageLoader = new ImageLoader(mRequestQueue, mBitmapCache);
	}
	
	public static RequestQueue getRequestQueue() { 
		if (mRequestQueue != null) {
			return mRequestQueue;
		} else {
			throw new IllegalStateException("RequestQueue not initialized");
		}
	}

	public static ImageLoader getImageLoader() {
		if (mImageLoader != null) {
			return mImageLoader;
		} else {
			throw new IllegalStateException("ImageLoader not initialized");
		}
	}

}
