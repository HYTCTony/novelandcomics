package com.huli.foxread.ui.base;

import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;


/**
 * 备注：全屏（无状态栏）继承这个
 */

public abstract class FullScreenBaseActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        intiStatusBar();
        super.onCreate(savedInstanceState);

    }

    /**
     * 初始化状态栏
     */
    private void intiStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //设置全屏。状态栏上滑隐藏（可以拉下）
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

            //状态背景栏透明
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
}
