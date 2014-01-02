package com.mani.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/**
 * Cannot change items and call refresh or notifydatasetChanged.
 * @author maniselvaraj
 *
 */
public class StaggeredGridView extends ScrollView{
	
	private Context mContext;
	private int mColumnCount;
	
	/** A ScrollView has only one child. TopLayout is that **/
	private LinearLayout mTopLayout;
	
	/** Holds the list of LinearLayouts based on the grid view's column size **/
	private ArrayList<LinearLayout> mGridLayouts;
	
	private ArrayList<Integer> mItemsHeight;
	
	/** Holds the list of grid items **/
	private List<StaggeredGridViewItem> mGridViewItems = new ArrayList<StaggeredGridViewItem>();
	
	private final int MAX_COLUMNS_SUPPORTED = 4;
	private int mColumnIndexToAdd = 0;
	private int showedItemCount = 0;
	
	private static Handler mHandler = new Handler();
	
	private final int SCROLL_OFFSET=3;
	
	public interface OnScrollListener {
		public void onTop();
		public void onScroll();
		public void onBottom();
	}
	
	private OnScrollListener mScrollListener;

	public StaggeredGridView(Context context) {
		super(context);
		mContext = context;
	}

	public StaggeredGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	public void init(int columncount) {
		
		if ( columncount > MAX_COLUMNS_SUPPORTED ) {
			Log.e("StaggeredGridView", "Number of columns supplied exceeds the maximum supported column..");
			throw new IllegalArgumentException("Column count cannot be more than maximum supported in init() ");
		}

		mColumnCount = columncount;
		mTopLayout = new LinearLayout(mContext);
		mGridLayouts = new ArrayList<LinearLayout>();
		mItemsHeight = new ArrayList<Integer>();

		for (int i = 0; i < mColumnCount; i++) {
			LinearLayout itemLayout = new LinearLayout(mContext);
			LinearLayout.LayoutParams itemParam = new LinearLayout.LayoutParams(
					0, LayoutParams.WRAP_CONTENT, 1.0f/mColumnCount);
			itemLayout.setPadding(5, 10, 5, 100);
			itemLayout.setOrientation(LinearLayout.VERTICAL);
			itemLayout.setClickable(true);
			ViewTreeObserver vto = itemLayout.getViewTreeObserver();

		    if(vto.isAlive()){
		        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
		            @Override
		            public void onGlobalLayout() {
			            for(LinearLayout parent: mGridLayouts) {
			            	System.out.println("######### Height of layout  ########## "+parent+ ":"+parent.getMeasuredHeight());
			            	
			            }
		            } 
		        });
		    }

		    itemLayout.setLayoutParams(itemParam);
			
			mGridLayouts.add(itemLayout);
			mTopLayout.addView(itemLayout);
			mItemsHeight.add(Integer.valueOf(i));
		}
		
		this.addView(mTopLayout);
	}
	
	public void clear() {
		mGridLayouts.clear();
		mColumnIndexToAdd = 0;
		mItemsHeight.clear();

		for (int i = 0; i < mColumnCount; i++) {
			LinearLayout layout = mGridLayouts.get(i);
			layout.removeAllViews();
			mItemsHeight.add(Integer.valueOf(0));
		}

		this.smoothScrollTo(0,0);
	}

	public OnScrollListener getScrollListener() {
		return mScrollListener;
	}

	public void setOnScrollListener(OnScrollListener mOnEndScrollListener) {
		this.mScrollListener = mOnEndScrollListener;
	}

	@Override
	protected void onScrollChanged(int x, int y, int oldX, int oldY) {
		super.onScrollChanged(x, y, oldX, oldY);
		//System.out.println("####### onScrollChanged y oldY  ######### "+y+":"+oldY+" GetHeight() "+getHeight()+" : "+mTopLayout.getMeasuredHeight());

		if (Math.abs(y - oldY) < 2 || 
				y+getHeight() >= mTopLayout.getMeasuredHeight() ||
				y <= SCROLL_OFFSET+1) {
			if (mScrollListener != null) {
				if (y <= SCROLL_OFFSET+1) {
					System.out.println("####### onScrollChanged onTop ######### ");
					mScrollListener.onTop();

				} else if (y+getHeight() >= mTopLayout.getMeasuredHeight()) {
					System.out.println("####### onScrollChanged onBottom ######### ");
					mScrollListener.onBottom();
				}
			}
		} else {
			mScrollListener.onScroll();
		}
	}
	
	int itemsBefore;
	
	public void setItemsBeforeToIntimate(int items) {
		itemsBefore = items;
	}
	
	public void addItem(StaggeredGridViewItem item) {
		mGridViewItems.add(item);
		datasetChanged();
	}
	
	public void addItemsList(List<StaggeredGridViewItem> itemlist) {
		if( itemlist != null && itemlist.size() > 0 ) {
			for(StaggeredGridViewItem item: itemlist) {
				mGridViewItems.add(item);
			}
			datasetChanged();
		}
	}
	
	private void recalculatePositions() {
		
		//Find biggest height among linearlayouts.
		
		//
		
	}
	
	private void datasetChanged() {
	
		int count = mGridViewItems.size();
		
		if (count > showedItemCount) {
			for (int i = showedItemCount; i < count; i++) {
				mColumnIndexToAdd = 0;
				int minHeight = mItemsHeight.get(0);
				for (int j = 1; j < mColumnCount; j++) {
					if (minHeight > mItemsHeight.get(j)) {
						minHeight = mItemsHeight.get(j);
						mColumnIndexToAdd = j;
					}
				}

				addToLayout(mGridViewItems.get(i), mColumnIndexToAdd);
			}
			showedItemCount = count;	
		}
	}
	
	private void addToLayout(StaggeredGridViewItem item, int columnIndex) {
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout parent = mGridLayouts.get(columnIndex);
		
		final View itemView = item.getView(inflater,parent);
		itemView.setClickable(true);
		itemView.setTag(Integer.valueOf(parent.getChildCount()+1));
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.topMargin = 10;
		parent.addView(itemView,params);

		// calculate height of item
		int itemHeight = item.getViewHeight(inflater, parent);
		Integer currentHeight = mItemsHeight.get(columnIndex);
		currentHeight = currentHeight + itemHeight;
		mItemsHeight.set(columnIndex, currentHeight);

	}
	
}
