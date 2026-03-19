package com.nnmedia.read.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FontHelper {

    /**
     * 用来加载所有的基于TextView的文本组件（TextView、Button、RadioButton、ToggleButton等等），而无需考虑界面的布局层级如何。
     *
     * @param context
     * @param root
     * @param fontName
     */
    public static void applyFont(final Context context, final View root, final String fontName) {
        try {
            if (root instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) root;
                for (int i = 0; i < viewGroup.getChildCount(); i++)
                    applyFont(context, viewGroup.getChildAt(i), fontName);
            } else if (root instanceof TextView)
                ((TextView) root).setTypeface(Typeface.createFromAsset(context.getAssets(), fontName));
            ((TextView) root).setIncludeFontPadding(false);
        } catch (Exception e) {
//            Log.e(TAG, String.format("Error occured when trying to apply %s font for %s view", fontName, root));
            e.printStackTrace();
        }
    }
}
