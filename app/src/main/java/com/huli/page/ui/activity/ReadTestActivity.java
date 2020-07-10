package com.huli.page.ui.activity;

import android.content.Context;
import android.content.Intent;

import com.huli.foxread.R;
import com.huli.page.ui.base.BaseActivity;

public class ReadTestActivity extends BaseActivity {

    public static void start(Context context) {
        Intent starter = new Intent(context, ReadTestActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void initView() {
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_read_test;
    }

}
