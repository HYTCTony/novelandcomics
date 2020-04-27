package com.huli.foxread.ui.fragments;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;

import com.flyco.tablayout.SlidingScaleTabLayout;
import com.huli.foxread.R;
import com.huli.foxread.listeners.OnClickEvent;
import com.huli.foxread.ui.activities.SearchBookActivity;
import com.huli.foxread.ui.base.BaseFragment;
import com.huli.foxread.ui.pageradapter.BsPagerAdapter;
import com.huli.foxread.utils.StatusBarUtils;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.vectordrawable.graphics.drawable.ArgbEvaluator;
import androidx.viewpager.widget.ViewPager;

public class MainBookstoreFragment extends BaseFragment implements ViewPager.OnPageChangeListener {

    public SlidingScaleTabLayout slidingTabLayout;
    private ViewPager viewPager;

    private ConstraintLayout ctlTabLayout;
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
        ctlTabLayout = $(view, R.id.ctl_tabLayout);
        ctlTabLayout.setBackgroundResource(R.color.colorPrimaryDark);
        slidingTabLayout = $(view, R.id.slidingTabLayout_book_store);
        viewPager = $(view, R.id.viewPager_book_store);
        btnSearch = $(view, R.id.iv_asBtn_search_bs);
    }

    @Override
    public void setListener() {
        btnSearch.setOnClickListener(new OnClickEvent() {
            @Override
            public void singleClick(View v) {
                Intent intent = new Intent(mActivity, SearchBookActivity.class);
                startActivity(intent);
            }
        });
        viewPager.addOnPageChangeListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {
        String[] tabTitles = getResources().getStringArray(R.array.tab_book_store);
        viewPager.setOffscreenPageLimit(tabTitles.length);
        viewPager.setAdapter(new BsPagerAdapter(getChildFragmentManager(), tabTitles));
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
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (!childForbid && isBleach && position == 0) {
            ctlTabRestore();
        } else {
            if (!isBleach) {
                ctlTabBleach();
            }
        }
    }


    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private boolean childForbid;       //禁止恢复颜色
    public boolean isBleach;           //（现在）是白色

    public void childCtrlTabRestore(){
        ctlTabRestore();
        childForbid = false;
    }
    public void childCtrlTabBleach(){
        ctlTabBleach();
        childForbid = true;
    }

    /**
     * 颜色复原
     */
    private void ctlTabRestore() {
        changeColorAmin(ctlTabLayout, Color.WHITE, ContextCompat.getColor(mActivity, R.color.colorPrimaryDark));
        isBleach = false;
    }

    /**
     * 变白
     */
    private void ctlTabBleach() {
        changeColorAmin(ctlTabLayout, ContextCompat.getColor(mActivity, R.color.colorPrimaryDark), Color.WHITE);
        isBleach = true;
    }


    /**
     * @param view
     * @param colStar
     * @param colEnd
     */
    private void changeColorAmin(View view, int colStar, int colEnd) {
        ValueAnimator colorAnim = ObjectAnimator.ofInt(view, "backgroundColor", colStar, colEnd);
        colorAnim.setDuration(350);
        colorAnim.setEvaluator(new ArgbEvaluator());
//        colorAnim.setRepeatCount(ValueAnimator.INFINITE);
//        colorAnim.setRepeatMode(ValueAnimator.REVERSE);
        colorAnim.start();
    }
}
