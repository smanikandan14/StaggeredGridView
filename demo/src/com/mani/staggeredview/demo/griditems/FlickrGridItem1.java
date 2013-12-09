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

	private RequestQueue mVolleyQueue;
	private ImageLoader mImageLoader;
	private FlickrImage mImage;
	private String mUserId;
	private View mView;
	private int mHeight;
	
	public FlickrGridItem1(FlickrImage image) {
		mImage=image;
		mUserId=image.getOwner();
		mVolleyQueue=StaggeredDemoApplication.getRequestQueue();
		mImageLoader=StaggeredDemoApplication.getImageLoader();
	}
	
	private void flickerGetUserRequest() {
		
		String url = "http://api.flickr.com/services/rest";
		Uri.Builder builder = Uri.parse(url).buildUpon();
		builder.appendQueryParameter("api_key", "5e045abd4baba4bbcd866e1864ca9d7b");
		builder.appendQueryParameter("method", "flickr.people.getInfo");
		builder.appendQueryParameter("format", "json");
		builder.appendQueryParameter("nojsoncallback", "1");
		builder.appendQueryParameter("user_id", mUserId);
		
		System.out.println("########## Flickr image request url ########### "+builder.toString());
		
		GsonRequest<FlickrResponsePhotos> gsonObjRequest = new GsonRequest<FlickrResponsePhotos>(Request.Method.GET, builder.toString(),
				FlickrResponsePhotos.class, null, new Response.Listener<FlickrResponsePhotos>() {
			@Override
			public void onResponse(FlickrResponsePhotos response) {
				try { 
					if(response != null) {
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				// Handle your error types accordingly.For Timeout & No connection error, you can show 'retry' button.
				// For AuthFailure, you can re login with user credentials.
				// For ClientError, 400 & 401, Errors happening on client side when sending api request.
				// In this case you can check how client is forming the api and debug accordingly.
				// For ServerError 5xx, you can do retry or handle accordingly.
				if( error instanceof NetworkError) {
				} else if( error instanceof ClientError) { 
				} else if( error instanceof ServerError) {
				} else if( error instanceof AuthFailureError) {
				} else if( error instanceof ParseError) {
				} else if( error instanceof NoConnectionError) {
				} else if( error instanceof TimeoutError) {
				}
			}
		});
		mVolleyQueue.add(gsonObjRequest);
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
		//flickerGetUserRequest();
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
