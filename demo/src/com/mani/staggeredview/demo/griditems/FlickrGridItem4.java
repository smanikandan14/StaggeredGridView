package com.mani.staggeredview.demo.griditems;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.mani.staggeredview.demo.PictureActivity;
import com.mani.staggeredview.demo.R;
import com.mani.staggeredview.demo.app.StaggeredDemoApplication;
import com.mani.staggeredview.demo.model.FlickrComment;
import com.mani.staggeredview.demo.model.FlickrGetCommentsResponse;
import com.mani.staggeredview.demo.model.FlickrImage;
import com.mani.staggeredview.demo.model.FlickrProfileResponse;
import com.mani.staggeredview.demo.volley.GsonRequest;
import com.mani.view.StaggeredGridViewItem;

public class FlickrGridItem4 extends StaggeredGridViewItem{

	private RequestQueue mVolleyQueue;
	private ImageLoader mImageLoader;
	private FlickrImage mImage;
	private String mUserId;
	private View mView;
	private int mHeight;
	private TextView mComment1;
	private TextView mComment2;
	private TextView mComment3;
	private TextView mComment4;
	private  Context mContext;
	
	public FlickrGridItem4(Context context, FlickrImage image) {
		mContext = context;
		mImage=image;
		mUserId=image.getOwner();
		mVolleyQueue=StaggeredDemoApplication.getRequestQueue();
		mImageLoader=StaggeredDemoApplication.getImageLoader();
	}
	
	private void flickrGetCommentsRequest() {
		
		String url = "http://api.flickr.com/services/rest";
		Uri.Builder builder = Uri.parse(url).buildUpon();
		builder.appendQueryParameter("api_key", "5e045abd4baba4bbcd866e1864ca9d7b");
		builder.appendQueryParameter("method", "flickr.photos.comments.getList");
		builder.appendQueryParameter("format", "json");
		builder.appendQueryParameter("nojsoncallback", "1");
		builder.appendQueryParameter("photo_id", mImage.getId());
		
		GsonRequest<FlickrGetCommentsResponse> gsonObjRequest = new GsonRequest<FlickrGetCommentsResponse>(Request.Method.GET, builder.toString(),
				FlickrGetCommentsResponse.class, null, new Response.Listener<FlickrGetCommentsResponse>() {
			@Override
			public void onResponse(FlickrGetCommentsResponse response) {
				try { 
					System.out.println("########## FlickrGetCommentsResponse not null ########## "+response);
					if(response != null) {
						int count=0;
						for(FlickrComment comment: response.getComments().getComments()) {
							if(count == 0) {
								mComment1.setText(comment.get_content());
							} else if(count == 1) {
								mComment2.setText(comment.get_content());
							} else if(count == 2) {
								mComment3.setText(comment.get_content());
							} else if(count == 3) {
								mComment4.setText(comment.get_content());
							}
							count++;
						}
						
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
				System.out.println("########## onError ########## "+error.getMessage());
			}
		});
		mVolleyQueue.add(gsonObjRequest);
	}
	
	@Override
	public View getView(LayoutInflater inflater, ViewGroup parent) {
		// TODO Auto-generated method stub
		mView = inflater.inflate(R.layout.grid_item4, null);
		ImageView image = (ImageView) mView.findViewById(R.id.image);
		mComment1 = (TextView) mView.findViewById(R.id.comment1);
		mComment2 = (TextView) mView.findViewById(R.id.comment2);
		mComment3 = (TextView) mView.findViewById(R.id.comment3);
		mComment4 = (TextView) mView.findViewById(R.id.comment4);
        
		mImageLoader.get(mImage.getImageUrl(), 
				ImageLoader.getImageListener(image,R.drawable.bg_no_image, android.R.drawable.ic_dialog_alert),parent.getWidth(),0);
		
		image.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, PictureActivity.class);
				intent.putExtra(PictureActivity.IMAGE_URL, mImage.getImageUrl());
				mContext.startActivity(intent);
			}
		});

		flickrGetCommentsRequest();
		return mView;
	}

	@Override
	public int getViewHeight(LayoutInflater inflater, ViewGroup parent) {
		RelativeLayout item_containerFrameLayout = (RelativeLayout)mView.findViewById(R.id.container);
		item_containerFrameLayout.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
		mHeight = item_containerFrameLayout.getMeasuredHeight();
		return mHeight;
	}


}

