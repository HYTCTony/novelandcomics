package com.nnmedia.read.ui.decoration;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 重写RecyclerView.ItemDecoration方法
 * 一个动态设置item个数,间距的工具类
 */
public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

    private int spanCount;           //列数
    private int spacing;             //间隔
    private boolean includeEdge;     //是否包含边缘
    private int headCount = 0;

    public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge, int headCount) {
        this.spanCount = spanCount;
        this.spacing = spacing;
        this.includeEdge = includeEdge;
        this.headCount = headCount;
    }

    public GridSpacingItemDecoration(int spanCount, int spacing, int headCount) {
        this.spanCount = spanCount;
        this.spacing = spacing;
        this.headCount = headCount;
    }

    public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
        this.spanCount = spanCount;
        this.spacing = spacing;
        this.includeEdge = includeEdge;
    }

    public GridSpacingItemDecoration(int spanCount, int spacing) {
        this.spanCount = spanCount;
        this.spacing = spacing;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        int position = parent.getChildAdapterPosition(view); // item position

        if (position < headCount) {
            return;
        }

        //这里是关键，需要根据你有几列来判断
        int pos = position - headCount;
        int column = pos % spanCount; // item column

        if (includeEdge) {
            outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
            outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

            if (pos < spanCount) { // top edge
                outRect.top = spacing;
            }
            outRect.bottom = spacing; // item bottom
        } else {
            outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
            outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
            if (pos >= spanCount) {
                outRect.top = spacing; // item top
            }
        }
    }
}
