package com.nnmedia.read.ui.imgshowpickerview;

import android.content.Context;


public class SizeUtils {
    private static SizeUtils sizeUtils = new SizeUtils();


    public static SizeUtils getSizeUtils() {
        return sizeUtils;
    }

    /**
     * dp转px
     *
     * @param context 上下文
     * @param dpValue dp值
     * @return px值
     */
    public int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
