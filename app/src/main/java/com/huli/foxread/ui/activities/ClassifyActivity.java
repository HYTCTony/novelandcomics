package com.huli.foxread.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.huli.foxread.R;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.ui.fragments.ClassifyFragment;
import com.huli.foxread.ui.widget.verticaltablayout.ITabView;
import com.huli.foxread.ui.widget.verticaltablayout.QTabView;
import com.huli.foxread.ui.widget.verticaltablayout.TabView;
import com.huli.foxread.ui.widget.verticaltablayout.VerticalTabLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


public class ClassifyActivity extends BaseActivity implements VerticalTabLayout.OnTabSelectedListener {

    private VerticalTabLayout tabLayout;
    private FragmentManager fragmentManager;
    private ClassifyFragment boyFragment, girlFragment, mFragment;

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_classify;
    }

    @Override
    public void initView(View view) {
        Toolbar toolbar = $(R.id.toolbar_normal);
        initToolBar(toolbar, R.string.txt_classify);

        tabLayout = $(R.id.verticaltablayout_classify);

        int textSelectCol = ContextCompat.getColor(this, R.color.txt_red);
        int textUnSelectCol = ContextCompat.getColor(this, R.color.txt_black);
        String[] tabTitles = getResources().getStringArray(R.array.tab_v_classify);
        for (int i = 0; i < tabTitles.length; i++) {
            tabLayout.addTab(new QTabView(this).setCurBgColor(R.color.white, R.color.color_f2)
                    .setTitle(new ITabView.TabTitle.Builder().setTextColor(textSelectCol, textUnSelectCol).setContent(tabTitles[i]).build()));
        }

        fragmentManager = getSupportFragmentManager();
    }

    @Override
    public void setListener() {
        tabLayout.addOnTabSelectedListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {
        tabLayout.setTabSelected(0);
        changeFragment(0);
    }

    @Override
    public void onTabSelected(TabView tab, int position) {
        changeFragment(position);
    }

    @Override
    public void onTabReselected(TabView tab, int position) {

    }

    /**
     * 切换Fragment
     */
    private void changeFragment(int position) {
        // 开启一个Fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        switch (position) {
            case 0:
                if (boyFragment == null) {
                    boyFragment = ClassifyFragment.newInstance(1);
                    transaction.add(R.id.frameLayout_content_classify, boyFragment);
                } else {
                    transaction.show(boyFragment);
                }
                break;

            case 1:
                if (girlFragment == null) {
                    girlFragment = ClassifyFragment.newInstance(1);
                    transaction.add(R.id.frameLayout_content_classify, girlFragment);
                } else {
                    transaction.show(girlFragment);
                }
                break;

            case 2:
                if (mFragment == null) {
                    mFragment = ClassifyFragment.newInstance(1);
                    transaction.add(R.id.frameLayout_content_classify, mFragment);
                } else {
                    transaction.show(mFragment);
                }
                break;

            default:
                break;
        }
        transaction.commit();   //记得提交事务
    }


    /**
     * 将所有Fragment设置为隐藏
     */
    private void hideFragments(FragmentTransaction transaction) {
        if (boyFragment != null) {
            transaction.hide(boyFragment);
        }
        if (girlFragment != null) {
            transaction.hide(girlFragment);
        }
        if (mFragment != null) {
            transaction.hide(mFragment);
        }

    }

}
