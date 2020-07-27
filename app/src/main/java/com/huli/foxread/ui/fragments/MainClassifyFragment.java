package com.huli.foxread.ui.fragments;

import android.content.Context;
import android.view.View;

import com.flyco.tablayout.SlidingScaleTabLayout;
import com.huli.foxread.R;
import com.huli.foxread.cache.UserInfoCache;
import com.huli.foxread.ui.base.BaseFragment;
import com.huli.foxread.ui.pageradapter.ClassifyPagerAdapter;
import com.huli.foxread.utils.StatusBarUtils;

import androidx.viewpager.widget.ViewPager;

/**
 * 分类
 */
public class MainClassifyFragment extends BaseFragment {

    private SlidingScaleTabLayout tabLayout;
    private ViewPager mViewPager;

    private int gender;

    @Override
    public int bindLayout() {
        return R.layout.fragment_main_classify;
    }

    @Override
    public void setStatusBar(View view) {
        StatusBarUtils.offsetView(mActivity, $(view, R.id.slidingTabLayout_book_classify));
        StatusBarUtils.setAndroidNativeLightStatusBar(mActivity, true);
    }

    @Override
    public void initView(View view) {
        tabLayout = $(view, R.id.slidingTabLayout_book_classify);
        mViewPager = $(view, R.id.viewPager_classify_boy_girl);

        String[] tabTitles = getResources().getStringArray(R.array.tab_classify);
        mViewPager.setOffscreenPageLimit(tabTitles.length);
        mViewPager.setAdapter(new ClassifyPagerAdapter(getChildFragmentManager(), tabTitles));
        tabLayout.setViewPager(mViewPager);
    }

    @Override
    public void setListener() {

    }

    @Override
    public void doBusiness(Context mContext) {
        gender = UserInfoCache.getGender(mContext);
        if (gender == 2) {
            //女生
            mViewPager.setCurrentItem(1);
        } else {
            //男生
            mViewPager.setCurrentItem(0);
        }
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            StatusBarUtils.setStatusBarTextDark(mActivity, true);
        }
    }

}
