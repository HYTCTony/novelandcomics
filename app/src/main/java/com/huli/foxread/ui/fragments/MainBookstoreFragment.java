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
import com.huli.foxread.cache.UserInfoCache;
import com.huli.foxread.listeners.OnClickEvent;
import com.huli.foxread.ui.activities.SearchBookActivity;
import com.huli.foxread.ui.base.BaseFragment;
import com.huli.foxread.ui.pageradapter.BsPagerAdapter;
import com.huli.foxread.utils.StatusBarUtils;

import java.lang.reflect.Field;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.vectordrawable.graphics.drawable.ArgbEvaluator;
import androidx.viewpager.widget.ViewPager;

public class MainBookstoreFragment extends BaseFragment implements ViewPager.OnPageChangeListener {

    private SlidingScaleTabLayout slidingTabLayout;
    private ViewPager viewPager;
    private BsPagerAdapter mAdapter;

    private ConstraintLayout ctlTabLayout;
    private ImageView btnSearch;

    /**
     * 获取当前显示的Fragment
     * @return
     */
    public Fragment getCurrentFragment(){
       return mAdapter.getCurrentFragment();
    }


    /*控制ctlTabLayout变色*/
    private boolean childForbid;        //child控制禁止恢复颜色
    public boolean isWhite;             //（现在）是白色


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
        slidingTabLayout = $(view, R.id.slidingTabLayout_book_store);
        viewPager = $(view, R.id.viewPager_book_store);
        btnSearch = $(view, R.id.iv_asBtn_search_bs);


        String[] tabTitles = getResources().getStringArray(R.array.tab_book_store);
        viewPager.setOffscreenPageLimit(tabTitles.length);
        mAdapter = new BsPagerAdapter(getChildFragmentManager(), tabTitles);
        viewPager.setAdapter(mAdapter);
//        setDefaultItem(1);
        slidingTabLayout.setViewPager(viewPager);

        int gender = UserInfoCache.getGender(mActivity);
        if (gender == 1) {
            isWhite = true;
            viewPager.setCurrentItem(1);
            ctlTabLayout.setBackgroundResource(R.color.white);
        } else if (gender == 2) {
            isWhite = true;
            viewPager.setCurrentItem(2);
            ctlTabLayout.setBackgroundResource(R.color.white);
        } else {
            isWhite = false;
            viewPager.setCurrentItem(0);
            ctlTabLayout.setBackgroundResource(R.color.colorPrimaryDark);
        }
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

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            StatusBarUtils.setStatusBarTextDark(mActivity, true);
        }
    }


    /**
     * 利用反射，设置默认选中item
     *
     * @param position
     */
    private void setDefaultItem(int position) {
        //我这里mViewpager是viewpager子类的实例。如果你是viewpager的实例，也可以这么干。
        try {
            Class c = Class.forName("android.support.v4.view.ViewPager");
            Field field = c.getDeclaredField("mCurItem");
            field.setAccessible(true);
            field.setInt(viewPager, position);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mAdapter.notifyDataSetChanged();

        viewPager.setCurrentItem(position);
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (!childForbid && isWhite && position == 0) {
            ctlTab2Yellow();
        } else {
            if (!isWhite) {
                ctlTab2White();
            }
        }
    }


    @Override
    public void onPageScrollStateChanged(int state) {

    }


    public void childCtrlTab2Yellow() {
        ctlTab2Yellow();
        childForbid = false;
    }

    public void childCtrlTab2White() {
        ctlTab2White();
        childForbid = true;
    }

    /**
     * 颜色变黄
     */
    private void ctlTab2Yellow() {
        changeColorAmin(ctlTabLayout, Color.WHITE, ContextCompat.getColor(mActivity, R.color.colorPrimaryDark));
        isWhite = false;
    }

    /**
     * 变白
     */
    private void ctlTab2White() {
        changeColorAmin(ctlTabLayout, ContextCompat.getColor(mActivity, R.color.colorPrimaryDark), Color.WHITE);
        isWhite = true;
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
