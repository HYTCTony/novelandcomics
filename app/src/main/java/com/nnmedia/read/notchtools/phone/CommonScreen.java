package com.nnmedia.read.notchtools.phone;

import android.app.Activity;
import android.view.Window;

import com.nnmedia.read.notchtools.core.AbsNotchScreenSupport;
import com.nnmedia.read.notchtools.core.OnNotchCallBack;

public class CommonScreen extends AbsNotchScreenSupport {

    @Override
    public boolean isNotchScreen(Window window) {
        return false;
    }

    @Override
    public int getNotchHeight(Window window) {
        return 0;
    }

    @Override
    public void fullScreenDontUseStatus(Activity activity, OnNotchCallBack notchCallBack) {
        super.fullScreenDontUseStatus(activity, notchCallBack);
    }

    @Override
    public void fullScreenUseStatus(Activity activity, OnNotchCallBack notchCallBack) {
        super.fullScreenUseStatus(activity, notchCallBack);
    }

}
