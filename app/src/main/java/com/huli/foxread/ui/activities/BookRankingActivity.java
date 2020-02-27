package com.huli.foxread.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.flyco.tablayout.SlidingTabLayout;
import com.huli.foxread.R;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.ui.fragments.RankingBoyGirlFragment;
import com.huli.foxread.ui.pageradapter.CPagerAdapter;

import java.util.ArrayList;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

public class BookRankingActivity extends BaseActivity {

    private SlidingTabLayout tabLayout;
    private ViewPager viewPager;
    private ArrayList<Fragment> fragments = new ArrayList<>();

    @Override
    public void initParms(Bundle parms) {

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

        tabLayout = $(R.id.slidingTabLayout_ranking_boy_girl);
        viewPager = $(R.id.viewPager_ranking);
        String[] tabTitles = getResources().getStringArray(R.array.tab_ranking);
        for (int i = 0; i < tabTitles.length; i++) {
            fragments.add(RankingBoyGirlFragment.newInstance(i));
        }
        viewPager.setOffscreenPageLimit(fragments.size());
        viewPager.setAdapter(new CPagerAdapter(getSupportFragmentManager(), fragments, tabTitles));
        tabLayout.setViewPager(viewPager);
    }

    @Override
    public void setListener() {

    }

    @Override
    public void doBusiness(Context mContext) {

    }
}
