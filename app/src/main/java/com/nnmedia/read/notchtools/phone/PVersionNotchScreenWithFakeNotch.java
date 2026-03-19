package com.nnmedia.read.notchtools.phone;

import android.app.Activity;
import android.view.DisplayCutout;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;

import com.nnmedia.read.notchtools.core.AbsNotchScreenSupport;
import com.nnmedia.read.notchtools.core.OnNotchCallBack;
import com.nnmedia.read.notchtools.helper.NotchStatusBarUtils;

import androidx.annotation.RequiresApi;


public class PVersionNotchScreenWithFakeNotch extends AbsNotchScreenSupport {

    @RequiresApi(api = 28)
    @Override
    public boolean isNotchScreen(Window window) {
        WindowInsets windowInsets = window.getDecorView().getRootWindowInsets();
        if (windowInsets == null) {
            return false;
        }

        DisplayCutout displayCutout = windowInsets.getDisplayCutout();
        if(displayCutout == null || displayCutout.getBoundingRects() == null){
            return false;
        }

        return true;
    }

    @RequiresApi(api = 28)
    @Override
    public int getNotchHeight(Window window) {
        int notchHeight = 0;
        WindowInsets windowInsets = window.getDecorView().getRootWindowInsets();
        if (windowInsets == null) {
            return 0;
        }

        DisplayCutout displayCutout = windowInsets.getDisplayCutout();
        if(displayCutout == null || displayCutout.getBoundingRects() == null){
            return 0;
        }

        notchHeight = displayCutout.getSafeInsetTop();

        return notchHeight;
    }

    @RequiresApi(api = 28)
    @Override
    public void fullScreenDontUseStatus(final Activity activity, OnNotchCallBack notchCallBack) {
        super.fullScreenDontUseStatus(activity, notchCallBack);
        if (!isNotchScreen(activity.getWindow())) {
            return;
        }
        WindowManager.LayoutParams attributes = activity.getWindow().getAttributes();
        attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        activity.getWindow().setAttributes(attributes);
        NotchStatusBarUtils.setFakeNotchView(activity.getWindow());
    }

    @RequiresApi(api = 28)
    @Override
    public void fullScreenUseStatus(Activity activity, OnNotchCallBack notchCallBack) {
        super.fullScreenUseStatus(activity, notchCallBack);
        WindowManager.LayoutParams attributes = activity.getWindow().getAttributes();
        attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        activity.getWindow().setAttributes(attributes);
    }
}
