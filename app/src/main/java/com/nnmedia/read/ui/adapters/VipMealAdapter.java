package com.nnmedia.read.ui.adapters;

import android.view.View;

import com.nnmedia.read.contact.Consts;
import com.nnmedia.read.ui.fragments.VipMealFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

public class VipMealAdapter extends FragmentPagerAdapter {

    private String[] titles;

    public VipMealAdapter(FragmentManager fm, String[] titles) {
        super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);  //实现懒加载
        this.titles = titles;
    }

    @NonNull
    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return VipMealFragment.newInstance(Consts.TYPE_CASH, i);
//            case 1:
//                return VipMealFragment.newInstance(Consts.TYPE_H_POINT, i);
            case 1:
                return VipMealFragment.newInstance(Consts.TYPE_GOLD, i);
            case 2:
                return VipMealFragment.newInstance(Consts.TYPE_POINT, i);
            default:
                return VipMealFragment.newInstance(Consts.TYPE_CASH, i);
        }
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