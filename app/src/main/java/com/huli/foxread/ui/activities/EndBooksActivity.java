package com.huli.foxread.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.flyco.tablayout.SlidingTabLayout;
import com.huli.foxread.R;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.ui.fragments.EndBooksFragment;
import com.huli.foxread.ui.pageradapter.CPagerAdapter;

import java.util.ArrayList;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

public class EndBooksActivity extends BaseActivity {

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
        return R.layout.activity_end_books;
    }

    @Override
    public void initView(View view) {
        Toolbar toolbar = $(R.id.toolbar_normal);
        initToolBar(toolbar, "");

        tabLayout = $(R.id.slidingTabLayout_end_books);
        viewPager = $(R.id.recyclerView_end_book);
        String[] tabTitles = getResources().getStringArray(R.array.tab_end_book);
        for (int i = 0; i < tabTitles.length; i++) {
            fragments.add(EndBooksFragment.newInstance(i));
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
