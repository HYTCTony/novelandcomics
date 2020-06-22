package com.huli.page.ui.activity;

import android.content.Context;
import android.content.Intent;

import com.huli.foxread.R;
import com.huli.page.ui.adapter.ViewPagerAdapter;
import com.huli.page.ui.base.BaseActivity;

import androidx.viewpager2.widget.ViewPager2;

public class ReadTestActivity extends BaseActivity {

    public static void start(Context context) {
        Intent starter = new Intent(context, ReadTestActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void initView() {
        ViewPager2 viewPager2 = findViewById(R.id.viewpager2);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter();
        viewPager2.setAdapter(viewPagerAdapter);
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_read_test;
    }
}
