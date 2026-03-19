package com.nnmedia.read.ui.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

public class HanSerifTextView extends AppCompatTextView {

    public HanSerifTextView(Context context) {
        super(context);
    }

    public HanSerifTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HanSerifTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setTypeface(@Nullable Typeface tf, int style) {
        super.setTypeface(Typeface.SANS_SERIF);
    }
}
