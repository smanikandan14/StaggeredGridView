package com.mani.view;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
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
 * Simple StaggeredGridView implementation using scrollview.
 * 
 * Supports different type of items.
 * 
 * Supports two mode of item arrangement in gridview.
 * 
 * FIXED - If the item's height are known(fixed) Use this mode. Items are arranged in the order in which it is added to the grid view.
 * 
 * DYNAMIC - Order is not maintained. If the item content changes dynamically for example, a image is downloaded 
 * 		  and the size is unknown. Use this mode, so that item positions are re-calculated
 * 		  when a change in height for an item is detected and height between columns are maintained close to equal. 
 * 
 * @author Mani Selvaraju
 *
 */
public class StaggeredGridView extends ScrollView{
	
	/** A ScrollView can have only one child. This is the top layout which holds the column layouts. **/
	private LinearLayout mTopLayout;
	
	/** Holds the list of LinearLayouts based on the grid view's column size **/
	private ArrayList<LinearLayout> mGridLayouts;
	
	private ArrayList<Integer> mItemsHeight;
	
	/** Holds the list of grid items **/
	private List<StaggeredGridViewItem> mGridViewItems = new ArrayList<StaggeredGridViewItem>();
	
	private OnGlobalLayoutListener mOnGlobalLayoutChangeListener = null;
	
	private final int MAX_COLUMNS_SUPPORTED = 4;
	private final int SCROLL_OFFSET = 3;

	private Context mContext;
	private Handler mHandler = new Handler();
	
	private int mColumns;
	private int mColumnIndexToAdd = 0;
	private int showedItemCount = 0;
	private int mPreviousColumnItemAdded = 0;
	private boolean isInitialized = false;
	
	/**
	 * Callbacks for different events of scrolling. 
	 *
	 */
	public interface OnScrollListener {
		public void onTop();
		public void onScroll();
		public void onBottom();
	}
	
	public enum Mode {
		FIXED("fixed"),
		DYNAMIC("dynamic");
		
		private String name;
		
		Mode(String name ) {
			this.name = name;
		}
		
		public String toString() {
			return this.name;
		}
	};
	
	private OnScrollListener mScrollListener;
	private Mode mMode = Mode.FIXED;
	
	public StaggeredGridView(Context context) {
		super(context);
		mContext = context;
	}

	public StaggeredGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mContext = context;
		int count = 2;
		//Default to FIXED mode.
		Mode mode = Mode.FIXED;
		String modeattr = null;
		
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.StaggeredGridView);
		final int indexCount = typedArray.getIndexCount();

		for(int i = 0; i < indexCount; i++){
			int attr = typedArray.getIndex(i);
			switch(attr){
				case R.styleable.StaggeredGridView_columnCount:
					count = typedArray.getInteger(attr, -1);
					break;
				case R.styleable.StaggeredGridView_mode:
					modeattr = typedArray.getString(R.styleable.StaggeredGridView_mode);
					break;
			}
		}
        
        typedArray.recycle();
        
        if( modeattr != null ) {
        	if( modeattr.equals(Mode.FIXED.toString())) {
        		mode = Mode.FIXED;
        	} else if( modeattr.equals(Mode.DYNAMIC.toString())) {
        		mode = Mode.DYNAMIC;
        	}
        }
        
        initialize(count, mode);
	}

	/**
	 * Supply number of columns the grid view to show and the mode.
	 * @param columncount
	 * @param mode
	 */
	public void initialize(int columncount, Mode mode) {
		
		if( isInitialized ) {
			throw new UnsupportedOperationException("Cannot call initialize twice. Check have you initialized from xml. ");
		}
		
		if( columncount > MAX_COLUMNS_SUPPORTED ) {
			Log.e("StaggeredGridView", "Number of columns supplied exceeds the maximum supported column..");
			throw new IllegalArgumentException("Column count cannot be more than maximum supported in init() ");
		}

		mColumns = columncount;
		mMode = mode;
		mTopLayout = new LinearLayout(mContext);
		mGridLayouts = new ArrayList<LinearLayout>();
		mItemsHeight = new ArrayList<Integer>();

		for( int i = 0; i < mColumns; i++ ) {
			LinearLayout itemLayout = new LinearLayout(mContext);
			LinearLayout.LayoutParams itemParam = new LinearLayout.LayoutParams(
					0, LayoutParams.WRAP_CONTENT, 1.0f/mColumns);
			itemLayout.setPadding(5, 10, 5, 100);
			itemLayout.setOrientation(LinearLayout.VERTICAL);
			itemLayout.setClickable(true);
		    itemLayout.setLayoutParams(itemParam);
			
			mGridLayouts.add(itemLayout);
			mTopLayout.addView(itemLayout);
			mItemsHeight.add(Integer.valueOf(i));
		}
		
		mOnGlobalLayoutChangeListener = new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				if( mGridLayouts.get(0).getChildCount() > 1 || 
						mGridLayouts.get(1).getChildCount() > 1 ) {
					removeListeners();
					reCalculatePositions();
				}
			}
		};

		if( mMode == Mode.DYNAMIC ) {
			registerLayoutChangeListener();
		}
		
		this.addView(mTopLayout);
		isInitialized = true;
	}
	
	public void clear() {
		mGridLayouts.clear();
		mColumnIndexToAdd = 0;
		mItemsHeight.clear();

		for( int i = 0; i < mColumns; i++ ) {
			LinearLayout layout = mGridLayouts.get(i);
			layout.removeAllViews();
			mItemsHeight.add(Integer.valueOf(0));
		}

		this.smoothScrollTo(0,0);
	}

	public int size() {
		return mGridViewItems.size();
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

		if ( Math.abs(y - oldY) < 2 || 
				y + getHeight() >= mTopLayout.getMeasuredHeight() ||
				y <= SCROLL_OFFSET+1 ) {
			if( mScrollListener != null ) {
				if( y <= SCROLL_OFFSET+1 ) {
					mScrollListener.onTop();
				} else if( y + getHeight() >= mTopLayout.getMeasuredHeight() ) {
					mScrollListener.onBottom();
				}
			}
		} else {
			mScrollListener.onScroll();
		}
	}

	/**
	 * When height of a grid item changes dynamically,  
	 * 1 - Find the layout which has smallest height and largest height among the columns.
	 * 2 - Remove the last element of the largest height layout and add to the smallest height layout.
	 * 3 - Register for the global layout change listener again.
	 */
	private void reCalculatePositions() {

		LinearLayout smallHeightLayout = mGridLayouts.get(0);
		LinearLayout largerHeightLayout = mGridLayouts.get(0);
		
		for(LinearLayout layout: mGridLayouts) {
			int height = layout.getMeasuredHeight();
			int smallHeight = smallHeightLayout.getMeasuredHeight();
			int largerHeight = largerHeightLayout.getMeasuredHeight();
			
			if( height < smallHeight ) {
				smallHeightLayout = layout;
			}  
			
			if( height > largerHeight) {
				largerHeightLayout = layout;
			}
		}
		
		View v = largerHeightLayout.getChildAt(smallHeightLayout.getChildCount()-1);
		if( v != null) {
			largerHeightLayout.removeView(v);
			smallHeightLayout.addView(v);
		}
		
		// Post the register layout change listener on the main thread, so that moving the positions
		// ( addView, removeView ) doesn't trigger onGlobalLayoutChange event.
		mHandler.post(new Runnable() {
			public void run() {
				registerLayoutChangeListener();	
			}
		});
   		
	}
	
	private void registerLayoutChangeListener() {
	    ViewTreeObserver vto = mTopLayout.getViewTreeObserver();
	    if(vto.isAlive()){
	    	vto.addOnGlobalLayoutListener(mOnGlobalLayoutChangeListener);
	    } 
	}
	
	private void removeListeners() {
		removeOnGlobalLayoutListener ( mTopLayout, mOnGlobalLayoutChangeListener);
	}
	
	@TargetApi(16)
	public static void removeOnGlobalLayoutListener(View v, ViewTreeObserver.OnGlobalLayoutListener listener){
	    if (Build.VERSION.SDK_INT < 16) {
	    	v.getViewTreeObserver().removeGlobalOnLayoutListener(listener);    
	    } else {
	        v.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
	    } 
	    
	}
	
	public void addItem(StaggeredGridViewItem item) {
		mGridViewItems.add(item);
		addItems(item);
	}
	
	public void addItemsList(List<StaggeredGridViewItem> itemlist) {
		if( itemlist != null && itemlist.size() > 0 ) {
			for(StaggeredGridViewItem item: itemlist) {
				mGridViewItems.add(item);
				addItems(item);
			}
			
		}
	}

	private void addItems(StaggeredGridViewItem item) {
		if( mMode == Mode.FIXED ) {
			addItemInFixedMode(item);
		} else if( mMode == Mode.DYNAMIC ) {
			addItemInDynamicMode(item);
		}
	}
	
	/**
	 * Adds the item in the order in which it is added to gridview.
	 */
	private void addItemInFixedMode(StaggeredGridViewItem item) {
		if( (mPreviousColumnItemAdded + 1) > (mColumns) ) {
			mPreviousColumnItemAdded = 0;
		}
		mColumnIndexToAdd = mPreviousColumnItemAdded;
		mPreviousColumnItemAdded ++;
		addToLayout(item, mColumnIndexToAdd);
	}
	
	/**
	 * Finds the column to which item has to be added by comparing the heights of the layout.
	 */
	private void addItemInDynamicMode(StaggeredGridViewItem item) {
	
		int count = mGridViewItems.size();
		
		if (count > showedItemCount) {
			for (int i = showedItemCount; i < count; i++) {
				mColumnIndexToAdd = 0;
				int minHeight = mItemsHeight.get(0);
				for (int j = 1; j < mColumns; j++) {
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
