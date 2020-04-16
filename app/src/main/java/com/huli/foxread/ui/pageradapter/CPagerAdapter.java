package com.huli.foxread.ui.pageradapter;


import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class CPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> mList;
    private String[] titles;

    public CPagerAdapter(FragmentManager fm, List<Fragment> mList, String[] titles) {
        super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);  //实现懒加载
        this.mList = mList;
        this.titles = titles;
    }

//    public CPagerAdapter(FragmentManager fm, List<Fragment> mList, String[] titles) {
//        super(fm);
//        this.mList = mList;
//        this.titles = titles;
//    }

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
        if (titles != null && titles.length > position) {
            return titles[position];
        }
        return "";
    }

}
