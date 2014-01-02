package com.mani.staggeredview.demo;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.mani.staggeredview.demo.app.StaggeredDemoApplication;

public class PictureActivity extends Activity {

	private ImageLoader mImageLoader;
	private ZoomImageView mPicture;
	private ImageView mClose;
	public static final String IMAGE_URL = "image_url";
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.picture_layout);
		mImageLoader = StaggeredDemoApplication.getImageLoader();
		mPicture = (ZoomImageView) findViewById(R.id.image);
		mClose = (ImageView) findViewById(R.id.close);
		mPicture.setAdjustViewBounds(false);

		String imageUrl = null;
		Bundle b = getIntent().getExtras();
		if( b != null) {
			imageUrl = b.getString(IMAGE_URL);
		}

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int mScreenWidth = metrics.widthPixels;
		int mScreenHeight = metrics.heightPixels;

		try {
			mImageLoader.get(imageUrl, 
					ImageLoader.getImageListener(mPicture, 
							R.drawable.bg_no_image, // default image resId 
							R.drawable.bg_no_image), // error image resId
							mScreenWidth, mScreenHeight - 50);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			mPicture.setImageResource(R.drawable.bg_no_image);
		}

		mClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}
