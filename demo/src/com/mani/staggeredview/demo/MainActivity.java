package com.mani.staggeredview.demo;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.ByteBuffer;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ListView;
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
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.Volley;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnPullEventListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshBase.State;
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
	
	private final String TAG_REQUEST = "MY_TAG";

	/*
	 * Extends from DisckBasedCache --> Utility from volley toolbox.
	 * Also implements ImageCache, so that we can pass this custom implementation
	 * to ImageLoader. 
	 */
	public  class DiskBitmapCache extends DiskBasedCache implements ImageCache {
		 
	    public DiskBitmapCache(File rootDirectory, int maxCacheSizeInBytes) {
	        super(rootDirectory, maxCacheSizeInBytes);
	    }
	 
	    public DiskBitmapCache(File cacheDir) {
	        super(cacheDir);
	    }
	 
	    public Bitmap getBitmap(String url) {
	        final Entry requestedItem = get(url);
	 
	        if (requestedItem == null)
	            return null;
	 
	        return BitmapFactory.decodeByteArray(requestedItem.data, 0, requestedItem.data.length);
	    }
	 
	    public void putBitmap(String url, Bitmap bitmap) {
	    	final Entry entry = new Entry();
	        entry.data = convertBitmapToBytes(bitmap) ;
	        put(url, entry);
	    }
	}
	
	GsonRequest<FlickrResponsePhotos> gsonObjRequest;
	
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static byte[] convertBitmapToBytes(Bitmap bitmap) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			ByteBuffer buffer = ByteBuffer.allocate(bitmap.getByteCount());
	        bitmap.copyPixelsToBuffer(buffer);
	        return buffer.array();
      } else {
    	  	ByteArrayOutputStream baos = new ByteArrayOutputStream();  
    	  	bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
	        byte[] data = baos.toByteArray();
	        return data;
      }
    }
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_layout);
		
		actionBarSetup();
		
		// Initialise Volley Request Queue. 
		mVolleyQueue = Volley.newRequestQueue(this);

		int max_cache_size = 1000000;
		mImageLoader = new ImageLoader(mVolleyQueue, new DiskBitmapCache(getCacheDir(),max_cache_size));
		
		mStaggeredView = (StaggeredGridView) findViewById(R.id.staggeredview);
		mStaggeredView.init(3);
		mStaggeredView.setMode(Mode.BOTH);
		/*MyGridViewItem item = new MyGridViewItem();
		MyGridViewItem1 item1 = new MyGridViewItem1();
		MyGridViewItem2 item2 = new MyGridViewItem2();
		
		mStaggeredView.addItem(item);

		mStaggeredView.addItem(item2);
		mStaggeredView.addItem(item1);
		
		item = new MyGridViewItem();
		mStaggeredView.addItem(item);
		
		item2 = new MyGridViewItem2();
		mStaggeredView.addItem(item2);

		item2 = new MyGridViewItem2();
		mStaggeredView.addItem(item2);

		item1 = new MyGridViewItem1();
		mStaggeredView.addItem(item1);
		item = new MyGridViewItem();
		mStaggeredView.addItem(item);
		

		item2 = new MyGridViewItem2();
		mStaggeredView.addItem(item2);

		item2 = new MyGridViewItem2();
		mStaggeredView.addItem(item2);

		item1 = new MyGridViewItem1();
		mStaggeredView.addItem(item1);*/

		mStaggeredView.setOnPullEventListener(new OnPullEventListener<ScrollView>() {

			@Override
			public void onPullEvent(PullToRefreshBase<ScrollView> refreshView,
					State state, Mode direction) {

				if(direction == Mode.PULL_FROM_START) {
			        refreshView.getLoadingLayoutProxy().setPullLabel("Pull down to refresh");
				} else if( direction == Mode.PULL_FROM_END) {
					refreshView.getLoadingLayoutProxy().setPullLabel("Pull down to load more data");
				}
				
			}
			
		});
		
		mStaggeredView.setOnRefreshListener(new OnRefreshListener2<ScrollView> () {
			
			public void onPullDownToRefresh(final PullToRefreshBase<ScrollView> refreshView) {
		        // Do work to refresh the list here.
		        new GetDataTask().execute();

			}

			/**
			 * onPullUpToRefresh will be called only when the user has Pulled from
			 * the end, and released.
			 */
			public void onPullUpToRefresh(final PullToRefreshBase<ScrollView> refreshView) {
		        // Do work to refresh the list here.
		        new GetDataTask().execute();

			}
		});
		
		showProgress();
		makeSampleHttpRequest();
	}
	
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void actionBarSetup() {
	  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	    ActionBar ab = getActionBar();
	    ab.setTitle("GSONResponseParsing");
	  }
	}
	
	public void onStop() {
		super.onStop();
		if(mProgress != null)
			mProgress.dismiss();
	}
	  
	private void makeSampleHttpRequest() {
		
		String url = "http://api.flickr.com/services/rest";
		Uri.Builder builder = Uri.parse(url).buildUpon();
		builder.appendQueryParameter("api_key", "5e045abd4baba4bbcd866e1864ca9d7b");
		builder.appendQueryParameter("method", "flickr.interestingness.getList");
		builder.appendQueryParameter("format", "json");
		builder.appendQueryParameter("nojsoncallback", "1");
		
		System.out.println("########## Flickr image request url ########### "+builder.toString());
		
		gsonObjRequest = new GsonRequest<FlickrResponsePhotos>(Request.Method.GET, builder.toString(),
				FlickrResponsePhotos.class, null, new Response.Listener<FlickrResponsePhotos>() {
			@Override
			public void onResponse(FlickrResponsePhotos response) {
				try {
					parseFlickrImageResponse(response);
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
				MyGridViewItem item = new MyGridViewItem(flkrImage);
				mStaggeredView.addItem(item);
				
				
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
