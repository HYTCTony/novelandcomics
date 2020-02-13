package com.huli.foxread.ui.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.huli.foxread.R;
import com.huli.foxread.ui.base.BaseFragment;
import com.huli.foxread.ui.pageradapter.CPagerAdapter;
import com.huli.foxread.utils.StatusBarUtils;

import java.util.ArrayList;

public class MainBookstoreFragment extends BaseFragment implements OnTabSelectListener {

    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;
    private ArrayList<Fragment> fragments = new ArrayList<>();

    @Override
    public int bindLayout() {
        return R.layout.fragment_main_book_store;
    }

    @Override
    public void setStatusBar(View view) {
        StatusBarUtils.offsetView(mActivity, $(view, R.id.slidingTabLayout_book_store));
        StatusBarUtils.setAndroidNativeLightStatusBar(mActivity, true);
    }

    @Override
    public void initView(View view) {
        slidingTabLayout = $(view, R.id.slidingTabLayout_book_store);
        viewPager = $(view, R.id.viewPager_book_store);
    }

    @Override
    public void setListener() {
        slidingTabLayout.setOnTabSelectListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {
        String[] tabTitles = getResources().getStringArray(R.array.tab_book_store);
        fragments.add(new SelectionBookFragment());
        fragments.add(new BookStoreBoyFragment());
        fragments.add(new BookStoreGirlFragment());
        fragments.add(new BooksBsFragment());
        viewPager.setAdapter(new CPagerAdapter(getChildFragmentManager(), fragments, tabTitles));
        slidingTabLayout.setViewPager(viewPager);
    }


    @Override
    public void onTabSelect(int position) {

    }

    @Override
    public void onTabReselect(int position) {

    }
}
