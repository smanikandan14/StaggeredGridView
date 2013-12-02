package com.mani.view.demo;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnPullEventListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshBase.State;
import com.mani.view.StaggeredGridView;
import com.mani.view.StaggeredGridViewItem;



public class TestActivity extends Activity {

	private StaggeredGridView mStaggeredView;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_layout);
		mStaggeredView = (StaggeredGridView) findViewById(R.id.waterfall);
		mStaggeredView.init(3);
		mStaggeredView.setMode(Mode.BOTH);
		MyGridViewItem item = new MyGridViewItem();
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
		mStaggeredView.addItem(item1);

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
		@Override
		public View getView(LayoutInflater inflater, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view = inflater.inflate(R.layout.grid_item, null);
			FrameLayout item_containerFrameLayout = (FrameLayout)view.findViewById(R.id.container);
			item_containerFrameLayout.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
			mHeight = item_containerFrameLayout.getMeasuredHeight();
			System.out.println("########## Height ######## "+mHeight);
			view.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					System.out.println("######## GridView Item onclick########### ");
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
