package com.huli.page.ui.activity;

import android.content.Context;
import android.content.Intent;

import com.huli.foxread.R;
import com.huli.page.ui.base.BaseActivity;
import com.huli.page.widget.view.HProgressBar;

public class ReadTestActivity extends BaseActivity {

    HProgressBar mHProgressBar;

    public static void start(Context context) {
        Intent starter = new Intent(context, ReadTestActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void initView() {
        mHProgressBar = (HProgressBar) findViewById(R.id.hprogress_bar_register);
        mHProgressBar.setProgress(60);
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_read_test;
    }

}
