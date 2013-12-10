package com.mani.staggeredview.demo.griditems;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.ClientError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.mani.staggeredview.demo.R;
import com.mani.staggeredview.demo.app.StaggeredDemoApplication;
import com.mani.staggeredview.demo.model.FlickrImage;
import com.mani.staggeredview.demo.model.FlickrResponsePhotos;
import com.mani.staggeredview.demo.volley.GsonRequest;
import com.mani.view.StaggeredGridViewItem;

/**
 * Item with user uploaded
 * @author maniselvaraj
 *
 */
public class FlickrGridItem1 extends StaggeredGridViewItem{

	private ImageLoader mImageLoader;
	private FlickrImage mImage;
	private View mView;
	private int mHeight;
	
	public FlickrGridItem1(FlickrImage image) {
		mImage=image;
		mImageLoader=StaggeredDemoApplication.getImageLoader();
	}
	
	@Override
	public View getView(LayoutInflater inflater, ViewGroup parent) {
		// TODO Auto-generated method stub
		mView = inflater.inflate(R.layout.grid_item1, null);
		ImageView image = (ImageView) mView.findViewById(R.id.image);
        mImageLoader.get(mImage.getImageUrl(), 
				ImageLoader.getImageListener(image,R.drawable.ic_launcher, android.R.drawable.ic_dialog_alert),parent.getWidth(),0);
		
		mView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.out.println("######## GridView Item onclick########### ");
			}
		});
		return mView;
	}

	@Override
	public int getViewHeight(LayoutInflater inflater, ViewGroup parent) {
		FrameLayout item_containerFrameLayout = (FrameLayout)mView.findViewById(R.id.container);
		item_containerFrameLayout.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
		mHeight = item_containerFrameLayout.getMeasuredHeight();
		System.out.println("########## Height ######## "+mHeight);
		return mHeight;
	}
}
