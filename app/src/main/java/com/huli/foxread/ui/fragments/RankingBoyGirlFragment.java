package com.huli.foxread.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.huli.foxread.R;
import com.huli.foxread.contact.Consts;
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

    public static RankingBoyGirlFragment newInstance(int type, int index) {
        Bundle bundle = new Bundle();
        bundle.putInt(Consts.RANK_FORM_BG, type);
        bundle.putInt("index", index);
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
        Bundle bundle = getArguments();
        int index = bundle.getInt("index");
        // 这个设置tag要与FragmentPagerAdapter中的获取方法getItemPosition方法要对应上
        view.setTag(index);

        tabLayout = $(view, R.id.verticaltablayout_ranking_type);
        viewPager = $(view, R.id.viewPager_ranking_type);
    }

    @Override
    public void setListener() {

    }

    @Override
    public void doBusiness(Context mContext) {
        mType = getArguments().getInt(Consts.RANK_FORM_BG);

    }

    @Override
    protected void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();
        viewPager.setNoScroll(true);
        viewPager.setAdapter(new MyPagerAdapter(getChildFragmentManager(), mType));
        tabLayout.setupWithViewPager(viewPager);
    }


    private class MyPagerAdapter extends FragmentPagerAdapter implements TabAdapter {

        private String[] titles;
        int textSelectCol = ContextCompat.getColor(getContext(), R.color.txt_red);
        int textUnSelectCol = ContextCompat.getColor(getContext(), R.color.txt_black);

        private int mType;

        public MyPagerAdapter(@NonNull FragmentManager fm, int mType) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            titles = getResources().getStringArray(R.array.tab_sub_ranking);
            this.mType = mType;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return RankingSubFragment.newInstance(mType, position + 1);
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
            return new ITabView.TabTitle.Builder().setTextSize(16).setTextColor(textSelectCol, textUnSelectCol).setContent(titles[position]).build();
        }

        @Override
        public int getBackground(int position) {
            return R.drawable.selector_vtab_bg_on_white;
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
