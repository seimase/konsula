package com.konsula.app.ui.custom;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;

/**
 * Created by hiantohendry on 5/21/16.
 */

public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {
    public static String TAG = EndlessRecyclerOnScrollListener.class.getSimpleName();

    private int previousTotal = 0; // The total number of items in the dataset after the last load
    private boolean loading = true; // True if we are still waiting for the last set of data to load.
    private int visibleThreshold = 6; // The minimum amount of items to have below your current scroll position before loading more.
    int firstVisibleItem, visibleItemCount, totalItemCount;

    private int current_page = 1;
    private int start_page=1;

    GridLayoutManager mStaggeredGridLayoutManager;

    public EndlessRecyclerOnScrollListener(GridLayoutManager layoutManager) {
        this.mStaggeredGridLayoutManager = layoutManager;
        //visibleThreshold = visibleThreshold * mStaggeredGridLayoutManager.getSpanCount();
    }

    public int getLastVisibleItem(int[] lastVisibleItemPositions) {
        int maxSize = 0;
        for (int i = 0; i < lastVisibleItemPositions.length; i++) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i];
            }
            else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i];
            }
        }
        return maxSize;
    }

    public void reset() {
        this.previousTotal = 0;
        this.current_page =1;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if(dy < 0) return;

        //int[] lastVisibleItemPositions =
        int lastVisibleItemPosition = mStaggeredGridLayoutManager.findLastVisibleItemPosition();
         visibleItemCount = recyclerView.getChildCount();
         totalItemCount = mStaggeredGridLayoutManager.getItemCount();

        if (loading) {

            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }

        synchronized (this) {
           // Log.e("HH","lastVisibleItemPosition="+lastVisibleItemPosition+", totalItemCount="+totalItemCount);
            if (!loading && (lastVisibleItemPosition + visibleThreshold) > totalItemCount) {
                current_page++;
                onLoadMore(current_page);
                loading = true;
            }
        }
    }

    public abstract void onLoadMore(int current_page);
}
