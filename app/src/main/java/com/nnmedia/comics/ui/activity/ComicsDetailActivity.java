package com.nnmedia.comics.ui.activity;

import android.content.Context;
import android.content.Intent;

import com.nnmedia.novel.R;
import com.nnmedia.page.ui.base.BaseActivity;

public class ComicsDetailActivity extends BaseActivity {

    public static void start(Context context) {
        Intent starter = new Intent(context, ComicsDetailActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_comics_detail;
    }
}
