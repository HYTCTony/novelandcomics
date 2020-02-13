package com.huli.foxread.ui.pageradapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class CPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> mList;
    private String[] titles;

    public CPagerAdapter(FragmentManager fm, List<Fragment> mList, String[] titles) {
        super(fm);
        this.mList = mList;
        this.titles = titles;
    }

    @Override
    public Fragment getItem(int i) {
        return mList.get(i);
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

}
