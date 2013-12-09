package com.mani.staggeredview.demo;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

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
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnPullEventListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshBase.State;
import com.mani.staggeredview.demo.app.StaggeredDemoApplication;
import com.mani.staggeredview.demo.griditems.FlickrGridItem1;
import com.mani.staggeredview.demo.griditems.FlickrGridItem2;
import com.mani.staggeredview.demo.model.FlickrImage;
import com.mani.staggeredview.demo.model.FlickrResponse;
import com.mani.staggeredview.demo.model.FlickrResponsePhotos;
import com.mani.staggeredview.demo.volley.GsonRequest;
import com.mani.view.StaggeredGridView;
import com.mani.view.StaggeredGridViewItem;

public class MainActivity extends Activity {

	private StaggeredGridView mStaggeredView;
	private RequestQueue mVolleyQueue;
	private ProgressDialog mProgress;
	private ImageLoader mImageLoader;
	private int currPage=1;
	GsonRequest<FlickrResponsePhotos> gsonObjRequest;
	
	private final String TAG_REQUEST = "MY_TAG";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_layout);
		
		actionBarSetup();
		
		// Initialise Volley Request Queue. 
		mVolleyQueue = StaggeredDemoApplication.getRequestQueue();
		mImageLoader = StaggeredDemoApplication.getImageLoader();
		
		mStaggeredView = (StaggeredGridView) findViewById(R.id.staggeredview);
		mStaggeredView.init(2);
		mStaggeredView.setMode(Mode.BOTH);

		mStaggeredView.setOnPullEventListener(new OnPullEventListener<ScrollView>() {

			@Override
			public void onPullEvent(PullToRefreshBase<ScrollView> refreshView,
					State state, Mode direction) {

				if(direction == Mode.PULL_FROM_START) {
					System.out.println("########## PULL FROM START ######### ");
			        refreshView.getLoadingLayoutProxy().setPullLabel("Pull down to refresh");
				} else if( direction == Mode.PULL_FROM_END) {
					System.out.println("########## PULL FROM END ######### ");
					refreshView.getLoadingLayoutProxy().setPullLabel("Pull down to load more data");
				}
				
			}
			
		});
		
		mStaggeredView.setOnRefreshListener(new OnRefreshListener2<ScrollView> () {
			
			public void onPullDownToRefresh(final PullToRefreshBase<ScrollView> refreshView) {
		        new GetDataTask().execute();

			}

			public void onPullUpToRefresh(final PullToRefreshBase<ScrollView> refreshView) {
		        //new GetDataTask().execute();
				flickerGetImagesRequest();
			}
		});
		
		showProgress();
		flickerGetImagesRequest();
	}
	
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void actionBarSetup() {
	  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	    ActionBar ab = getActionBar();
	    ab.setTitle("StaggeredGridView Demo");
	  }
	}
	
	public void onStop() {
		super.onStop();
		if(mProgress != null)
			mProgress.dismiss();
	}
	  
	private void flickerGetImagesRequest() {
		
		String url = "http://api.flickr.com/services/rest";
		Uri.Builder builder = Uri.parse(url).buildUpon();
		builder.appendQueryParameter("api_key", "5e045abd4baba4bbcd866e1864ca9d7b");
		builder.appendQueryParameter("method", "flickr.interestingness.getList");
		builder.appendQueryParameter("format", "json");
		builder.appendQueryParameter("nojsoncallback", "1");
		builder.appendQueryParameter("per_page", "20");
		builder.appendQueryParameter("page", Integer.toString(currPage));
		
		System.out.println("########## Flickr image request url ########### "+builder.toString());
		
		gsonObjRequest = new GsonRequest<FlickrResponsePhotos>(Request.Method.GET, builder.toString(),
				FlickrResponsePhotos.class, null, new Response.Listener<FlickrResponsePhotos>() {
			@Override
			public void onResponse(FlickrResponsePhotos response) {
				try { 
					if(response != null) {
						mStaggeredView.onRefreshComplete();
						parseFlickrImageResponse(response);
						currPage++;
					}
				} catch (Exception e) {
					e.printStackTrace();
					showToast("JSON parse error");
				}
				stopProgress();
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
				mStaggeredView.onRefreshComplete();
				stopProgress();
				showToast(error.getMessage());
			}
		});
		gsonObjRequest.setTag(TAG_REQUEST);	
		mVolleyQueue.add(gsonObjRequest);
	}
	
	
	private void showProgress() {
		mProgress = ProgressDialog.show(this, "", "Loading...");
	}
	
	private void stopProgress() {
		mProgress.cancel();
	}
	
	private void showToast(String msg) {
		Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
	}
	
	private void parseFlickrImageResponse(FlickrResponsePhotos response) {
		
			FlickrResponse photos = response.getPhotos();
			for(int index = 0 ; index < photos.getPhotos().size(); index++) {
			
				FlickrImage flkrImage = photos.getPhotos().get(index);
				StaggeredGridViewItem item = null;
				if( index%2==0) {
					item = new FlickrGridItem1(flkrImage);
					mStaggeredView.addItem(item);
				} else {
					item = new FlickrGridItem2(flkrImage);
					mStaggeredView.addItem(item);
					
				}
				
				
				/*DataModel model = new DataModel();
				model.setImageUrl(imageUrl);
				model.setTitle(flkrImage.getTitle());
				mDataList.add(model); */

			}
	}
	
	private class GetDataTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... params) {
                // Simulates a background job.
                try {
                        Thread.sleep(4000);
                } catch (InterruptedException e) {
                }
                return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
                // Call onRefreshComplete when the list has been refreshed.
                mStaggeredView.onRefreshComplete();

                super.onPostExecute(result);
        }
}

	public class MyGridViewItem extends StaggeredGridViewItem {

		private int mHeight = 0;
		private View mView = null;
		private FlickrImage mImage = null;
		
		public MyGridViewItem(FlickrImage flkrImage) {
			mImage = flkrImage;
		}
		
		@Override
		public View getView(LayoutInflater inflater, ViewGroup parent) {
			// TODO Auto-generated method stub
			mView = inflater.inflate(R.layout.grid_item3, null);
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
	
	public class MyGridViewItem1 extends StaggeredGridViewItem {

		private int mHeight = 0;
		@Override
		public View getView(LayoutInflater inflater, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view = inflater.inflate(R.layout.grid_item1, null);
			FrameLayout item_containerFrameLayout = (FrameLayout)view.findViewById(R.id.container);
			item_containerFrameLayout.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
			mHeight = item_containerFrameLayout.getMeasuredHeight();
			System.out.println("########## Height ######## "+mHeight);
			
			view.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					System.out.println("######## GridView Item 1 onclick########### ");
				}
			});
			return view;
		}

		@Override
		public int getViewHeight(LayoutInflater inflater, ViewGroup parent) {
			// TODO Auto-generated method stub
			return mHeight;
		}
		
	}

	
	public class MyGridViewItem2 extends StaggeredGridViewItem {

		private int mHeight = 0;
		@Override
		public View getView(LayoutInflater inflater, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view = inflater.inflate(R.layout.grid_item2, null);
			FrameLayout item_containerFrameLayout = (FrameLayout)view.findViewById(R.id.container);
			item_containerFrameLayout.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
			mHeight = item_containerFrameLayout.getMeasuredHeight();
			System.out.println("########## Height ######## "+mHeight);
			view.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					System.out.println("######## GridView Item 2 onclick########### ");
				}
			});
			return view;
		}

		@Override
		public int getViewHeight(LayoutInflater inflater, ViewGroup parent) {
			// TODO Auto-generated method stub
			return mHeight;
		}
		
	}

}
