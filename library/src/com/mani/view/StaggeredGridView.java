package com.mani.view;

import java.util.ArrayList;
import java.util.List;

import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Cannot change items and call refresh or notifydatasetChanged.
 * @author maniselvaraj
 *
 */
public class StaggeredGridView extends PullToRefreshScrollView{
	
	private Context mContext;
	private int mColumnCount;
	
	private View mFirstChild;
	private LinearLayout mTopLayout;
	private ArrayList<LinearLayout> mGridLayouts;
	private ArrayList<Integer> mItemsHeight;
	private List<StaggeredGridViewItem> mGridViewItems = new ArrayList<StaggeredGridViewItem>();
	private int mColumnIndexToAdd = 0;
	private int showedItemCount = 0;
	
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

			itemLayout.setLayoutParams(itemParam);
			mGridLayouts.add(itemLayout);
			mTopLayout.addView(itemLayout);
			mItemsHeight.add(Integer.valueOf(i));
		}
		this.addView(mTopLayout);
		mFirstChild = getChildAt(0);
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

		this.smoothScrollTo(0);
	}

	public OnScrollListener getOnEndScrollListener() {
		return mScrollListener;
	}

	public void setOnEndScrollListener(OnScrollListener mOnEndScrollListener) {
		this.mScrollListener = mOnEndScrollListener;
	}

	@Override
	protected void onScrollChanged(int x, int y, int oldX, int oldY) {
		super.onScrollChanged(x, y, oldX, oldY);
		if (Math.abs(y - oldY) < 2 || y+getHeight() >= mFirstChild.getMeasuredHeight() || y <= SCROLL_OFFSET+1) {
			if (mScrollListener != null) {
				if (y <= SCROLL_OFFSET+1) {
					mScrollListener.onTop();

				} else if (y+getHeight() >= mFirstChild.getMeasuredHeight()) {
					mScrollListener.onBottom();
				}
			}
		} else {
			mScrollListener.onScroll();
		}
	}

	public void addItem(StaggeredGridViewItem item) {
		mGridViewItems.add(item);
		datasetChanged();
	}
	
	public void datasetChanged() {
	
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
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.topMargin = 10;
		mGridLayouts.get(columnIndex).addView(itemView,params);

		// calculate height of item
		int itemHeight = item.getViewHeight(inflater, parent);
		Integer currentHeight = mItemsHeight.get(columnIndex);
		currentHeight = currentHeight + itemHeight;
		mItemsHeight.set(columnIndex, currentHeight);

	}
	
}
