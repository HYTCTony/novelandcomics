package com.huli.foxread.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.flyco.tablayout.SlidingTabLayout;
import com.huli.foxread.R;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.listeners.OnClickEvent;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.ui.fragments.RankingBoyGirlFragment;
import com.huli.foxread.ui.pageradapter.CPagerAdapter;

import java.util.ArrayList;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

public class BookRankingActivity extends BaseActivity {

    private ImageView btnSearch;
    private SlidingTabLayout tabLayout;
    private ViewPager viewPager;
    private ArrayList<Fragment> fragments = new ArrayList<>();

    private int mType;      //性别

    @Override
    public void initParms(Bundle parms) {
        mType = parms.getInt(Consts.TYPE);
    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_book_ranking;
    }

    @Override
    public void initView(View view) {
        Toolbar toolbar = $(R.id.toolbar_normal);
        initToolBar(toolbar, "");

        btnSearch = $(R.id.iv_asBtn_search);
        tabLayout = $(R.id.slidingTabLayout_ranking_boy_girl);
        viewPager = $(R.id.viewPager_ranking);
        String[] tabTitles = getResources().getStringArray(R.array.tab_ranking);
        for (int i = 0; i < tabTitles.length; i++) {
            fragments.add(RankingBoyGirlFragment.newInstance(i + 1));
        }
        viewPager.setOffscreenPageLimit(fragments.size());
        viewPager.setAdapter(new CPagerAdapter(getSupportFragmentManager(), fragments, tabTitles));
        tabLayout.setViewPager(viewPager);

    }

    @Override
    public void setListener() {
        btnSearch.setOnClickListener(new OnClickEvent() {
            @Override
            public void singleClick(View v) {
                Intent intent = new Intent(BookRankingActivity.this, SearchBookActivity.class);
                intent.putExtra(Consts.TYPE, Consts.TYPE_SELECTION);
                startActivity(intent);
            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {
        if (mType == Consts.TYPE_BOY) {
            tabLayout.setCurrentTab(0);
        } else if (mType == Consts.TYPE_GIRL) {
            tabLayout.setCurrentTab(1);
        } else {
            tabLayout.setCurrentTab(0);
        }
    }
}
