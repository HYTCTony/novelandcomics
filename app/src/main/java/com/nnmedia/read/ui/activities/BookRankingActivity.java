package com.nnmedia.read.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.flyco.tablayout.SlidingScaleTabLayout;
import com.nnmedia.novel.R;
import com.nnmedia.read.contact.Consts;
import com.nnmedia.read.listeners.OnClickEvent;
import com.nnmedia.read.ui.base.BaseActivity;
import com.nnmedia.read.ui.pageradapter.RankPagerAdapter;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

public class BookRankingActivity extends BaseActivity {

    private ImageView btnSearch;
    private SlidingScaleTabLayout tabLayout;
    private ViewPager viewPager;

    private int mType;      //性别

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
        return R.layout.activity_book_ranking;
    }

    @Override
    public void initView(View view) {
        Toolbar toolbar = $(R.id.toolbar_normal);
        initToolBar(toolbar, "");

        btnSearch = $(R.id.iv_asBtn_search);
        tabLayout = $(R.id.slidingTabLayout_ranking_boy_girl);
        viewPager = $(R.id.viewPager_ranking);
        String[] tabTitles = getResources().getStringArray(R.array.tab_ranking);
        viewPager.setOffscreenPageLimit(tabTitles.length);
        viewPager.setAdapter(new RankPagerAdapter(getSupportFragmentManager(), tabTitles));
        tabLayout.setViewPager(viewPager);

    }

    @Override
    public void setListener() {
        btnSearch.setOnClickListener(new OnClickEvent() {
            @Override
            public void singleClick(View v) {
                Intent intent = new Intent(BookRankingActivity.this, SearchBookActivity.class);
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
