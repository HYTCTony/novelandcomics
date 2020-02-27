package com.huli.foxread.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.huli.foxread.R;
import com.huli.foxread.ui.base.LazyLoadFragment;
import com.huli.foxread.ui.widget.NoScrollViewPager;
import com.huli.foxread.ui.widget.verticaltablayout.ITabView;
import com.huli.foxread.ui.widget.verticaltablayout.TabAdapter;
import com.huli.foxread.ui.widget.verticaltablayout.TabView;
import com.huli.foxread.ui.widget.verticaltablayout.VerticalTabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class RankingBoyGirlFragment extends LazyLoadFragment {

    private VerticalTabLayout tabLayout;
    private NoScrollViewPager viewPager;

    private int mType;

    public static RankingBoyGirlFragment newInstance(int type) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        RankingBoyGirlFragment frag = new RankingBoyGirlFragment();
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public int bindLayout() {
        return R.layout.fragment_ranking_boy_girl;
    }

    @Override
    public void setStatusBar(View view) {

    }

    @Override
    public void initView(View view) {
        tabLayout = $(view, R.id.verticaltablayout_ranking_type);
        viewPager = $(view, R.id.viewPager_ranking_type);
    }

    @Override
    public void setListener() {

    }

    @Override
    public void doBusiness(Context mContext) {
        viewPager.setNoScroll(true);
        viewPager.setAdapter(new MyPagerAdapter(getChildFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();
    }


    private class MyPagerAdapter extends FragmentPagerAdapter implements TabAdapter {

        private String[] titles;
        int textSelectCol = ContextCompat.getColor(getContext(), R.color.txt_red);
        int textUnSelectCol = ContextCompat.getColor(getContext(), R.color.txt_black);

        public MyPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            titles = getResources().getStringArray(R.array.tab_sub_ranking);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return RankingSubFeagment.newInstance(position);
        }

        @Override
        public int getCount() {
            return titles == null ? 0 : titles.length;
        }

        @Override
        public TabView.TabIcon getIcon(int position) {
            return null;
        }

        @Override
        public TabView.TabTitle getTitle(int position) {
            return new ITabView.TabTitle.Builder().setTextColor(textSelectCol, textUnSelectCol).setContent(titles[position]).build();
        }

        @Override
        public int getBackground(int position) {
            return 0;
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
}
