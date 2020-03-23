package com.huli.foxread.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.huli.foxread.R;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.listeners.OnClickEvent;
import com.huli.foxread.ui.activities.SearchBookActivity;
import com.huli.foxread.ui.base.BaseFragment;
import com.huli.foxread.ui.pageradapter.CPagerAdapter;
import com.huli.foxread.utils.StatusBarUtils;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

public class MainBookstoreFragment extends BaseFragment implements OnTabSelectListener {

    public SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;
    private ArrayList<Fragment> fragments = new ArrayList<>();

    private ImageView btnSearch;

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
        btnSearch = $(view, R.id.iv_asBtn_search_bs);
    }

    @Override
    public void setListener() {
        slidingTabLayout.setOnTabSelectListener(this);

        btnSearch.setOnClickListener(new OnClickEvent() {
            @Override
            public void singleClick(View v) {
                int currentTab = slidingTabLayout.getCurrentTab();
                Intent intent = new Intent(mActivity, SearchBookActivity.class);
                intent.putExtra(Consts.TYPE, currentTab == 0 ? 4 : currentTab);
                startActivity(intent);
            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {
        String[] tabTitles = getResources().getStringArray(R.array.tab_book_store);
        fragments.add(new SelectionBookFragment());
        fragments.add(BookStoreBoyFragment.newInstance(Consts.TYPE_BOY));
        fragments.add(BookStoreBoyFragment.newInstance(Consts.TYPE_GIRL));
        fragments.add(BookStoreBoyFragment.newInstance(Consts.TYPE_LIBRARY));
        viewPager.setOffscreenPageLimit(fragments.size());
        viewPager.setAdapter(new CPagerAdapter(getChildFragmentManager(), fragments, tabTitles));
        slidingTabLayout.setViewPager(viewPager);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            StatusBarUtils.setStatusBarTextDark(mActivity, true);
        }
    }


    @Override
    public void onTabSelect(int position) {

    }

    @Override
    public void onTabReselect(int position) {

    }
}
