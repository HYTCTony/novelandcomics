package com.nnmedia.read.ui.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import com.nnmedia.novel.R;

import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


/**
 * 分割线
 */

public class SimpleDividerDecoration extends RecyclerView.ItemDecoration {

    private int dividerHeight;
    private int padding = 0;
    private Paint dividerPaint;

    public SimpleDividerDecoration(Context context) {
        dividerPaint = new Paint();
        dividerPaint.setColor(ContextCompat.getColor(context, R.color.off_gray));
        dividerHeight = 3;
    }

    public SimpleDividerDecoration(Context context, @DimenRes int dividerHeight) {
        dividerPaint = new Paint();
        dividerPaint.setColor(ContextCompat.getColor(context, R.color.off_gray));
        this.dividerHeight = context.getResources().getDimensionPixelSize(dividerHeight);
    }


    public SimpleDividerDecoration(Context context, @DimenRes int dividerHeight, @ColorRes int colorRes) {
        dividerPaint = new Paint();
        dividerPaint.setColor(ContextCompat.getColor(context, colorRes));
        this.dividerHeight = context.getResources().getDimensionPixelSize(dividerHeight);
    }

    public SimpleDividerDecoration(Context context, @DimenRes int dividerHeight, @DimenRes int padding, @ColorRes int colorRes) {
        dividerPaint = new Paint();
        dividerPaint.setColor(ContextCompat.getColor(context, colorRes));
        this.dividerHeight = context.getResources().getDimensionPixelSize(dividerHeight);
        this.padding = context.getResources().getDimensionPixelSize(padding);
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = dividerHeight;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getChildCount();
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        for (int i = 0; i < childCount - 1; i++) {
            View view = parent.getChildAt(i);
            float top = view.getBottom();
            float bottom = view.getBottom() + dividerHeight;
            c.drawRect(left + padding, top, right - padding, bottom, dividerPaint);
        }
    }
}