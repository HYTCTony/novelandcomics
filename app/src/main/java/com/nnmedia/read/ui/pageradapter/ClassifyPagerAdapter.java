package com.nnmedia.read.ui.pageradapter;

import android.view.View;

import com.nnmedia.read.ui.fragments.ClassifyVpFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

/**
 * 分类Pager适配器
 */
public class ClassifyPagerAdapter extends FragmentPagerAdapter {

    private String[] titles;

    public ClassifyPagerAdapter(FragmentManager fm, String[] titles) {
        super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);  //实现懒加载
        this.titles = titles;
    }


    @NonNull
    @Override
    public Fragment getItem(int i) {
        return ClassifyVpFragment.newInstance(i);
    }

    @Override
    public int getCount() {
        return titles == null ? 0 : titles.length;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (titles != null && titles.length > position) {
            return titles[position];
        }
        return "";
    }

    //SlidingScaleTabLayout
    //请务必重写PagerAdapter.getItemPosition()方法，根据object返回正确的位置信息，因为需要通过此方法找到对应位置的SlidingTab，进行文字样式切换
    @Override
    public int getItemPosition(@NonNull Object object) {
        // PagerAdapter的默认实现，请返回正确的位置信息
        if (object instanceof View) {
            return (int) ((View) object).getTag();
        }
        return PagerAdapter.POSITION_UNCHANGED;
    }
}
