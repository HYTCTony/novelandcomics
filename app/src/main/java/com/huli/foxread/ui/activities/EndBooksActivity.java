package com.huli.foxread.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.flyco.tablayout.SlidingScaleTabLayout;
import com.huli.foxread.R;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.listeners.OnClickEvent;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.ui.pageradapter.EbPagerAdapter;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

/**
 * 完结
 */
public class EndBooksActivity extends BaseActivity {

    private ImageView btnSearch;
    private SlidingScaleTabLayout tabLayout;
    private ViewPager viewPager;

    private int mType;

    @Override
    public void initParms(Bundle parms) {
        mType = parms.getInt(Consts.TYPE);
    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_end_books;
    }

    @Override
    public void initView(View view) {
        Toolbar toolbar = $(R.id.toolbar_normal);
        initToolBar(toolbar, "");

        btnSearch = $(R.id.iv_asBtn_search);
        tabLayout = $(R.id.slidingTabLayout_end_books);
        viewPager = $(R.id.recyclerView_end_book);
        String[] tabTitles = getResources().getStringArray(R.array.tab_end_book);
        viewPager.setOffscreenPageLimit(tabTitles.length);
        viewPager.setAdapter(new EbPagerAdapter(getSupportFragmentManager(), tabTitles));
        tabLayout.setViewPager(viewPager);
    }

    @Override
    public void setListener() {
        btnSearch.setOnClickListener(new OnClickEvent() {
            @Override
            public void singleClick(View v) {
                Intent intent = new Intent(EndBooksActivity.this, SearchBookActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {
        if (mType == Consts.TYPE_BOY) {
            tabLayout.setCurrentTab(0);
        } else if (mType == Consts.TYPE_GIRL) {
            tabLayout.setCurrentTab(1);
        } else {
            tabLayout.setCurrentTab(0);
        }
    }

}
