package com.huli.foxread.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baoyachi.stepview.HorizontalStepView;
import com.baoyachi.stepview.bean.StepBean;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.huli.foxread.R;
import com.huli.foxread.engines.GlideImageLoader2;
import com.huli.foxread.entity.BannerADEntity;
import com.huli.foxread.ui.activities.InviteFriendsActivity;
import com.huli.foxread.ui.adapters.ReadingMissionAdapter;
import com.huli.foxread.ui.base.BaseFragment;
import com.huli.foxread.utils.GlideUtil;
import com.huli.foxread.utils.StatusBarUtils;
import com.huli.foxread.utils.Tos;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainWelfareFragment extends BaseFragment implements OnBannerListener, View.OnClickListener {

    private RecyclerView recyclerView;
    private ReadingMissionAdapter mAdapter;

    private Banner mBanner;

    @Override
    public int bindLayout() {
        return R.layout.fragment_main_welfare;
    }

    @Override
    public void setStatusBar(View view) {
        StatusBarUtils.setStatusBarTextDark(mActivity, false);
        StatusBarUtils.offsetView(mActivity, $(view, R.id.tv_title_bar_welfare));
    }

    @Override
    public void initView(View view) {

        recyclerView = $(view, R.id.recyclerView_reading_task);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mAdapter = new ReadingMissionAdapter();
        recyclerView.setAdapter(mAdapter);

        LayoutInflater inflater = LayoutInflater.from(mActivity);
        View headViewTop = inflater.inflate(R.layout.layout_rv_head_welfare_top, recyclerView, false);
        View headViewNewbie = inflater.inflate(R.layout.layout_rv_head_newbie_mission, recyclerView, false);
        View headViewDaily = inflater.inflate(R.layout.layout_rv_head_daily_mission, recyclerView, false);

        mAdapter.addHeaderView(headViewTop, 0);
        mAdapter.addHeaderView(headViewNewbie, 1);
        mAdapter.addHeaderView(headViewDaily, 2);

        initTopLayout(headViewTop);

        initNewbieMission(headViewNewbie);

        initDailyLayout(headViewDaily);
    }

    @Override
    public void setListener() {
        mAdapter.addChildClickViewIds(R.id.btn_reading_mission_action);
        mAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.btn_reading_mission_action) {
                    Tos.showShort(mActivity, "去阅读");
                }
            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {

        List<String> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add("sssssssssss");
        }
        mAdapter.setNewData(list);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            StatusBarUtils.setStatusBarTextDark(mActivity, false);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_go2_invite_friends:
                startActivity(new Intent(mActivity, InviteFriendsActivity.class));
                break;
            default:
                break;
        }
    }


    @Override
    public void OnBannerClick(int position) {

    }

    //如果你需要考虑更好的体验，可以这么操作
    @Override
    public void onStart() {
        super.onStart();
        //开始轮播
        if (mBanner != null) {
            mBanner.start();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        //结束轮播
        if (mBanner != null) {
            mBanner.stopAutoPlay();
        }
    }

    private void initDailyLayout(View rootView) {
        TextView btnGo2Invite = $(rootView, R.id.btn_go2_invite_friends);
        btnGo2Invite.setText(R.string.txt_go2_invite_friends);
        btnGo2Invite.setOnClickListener(this);
    }

    private void initNewbieMission(View rootView) {
        HorizontalStepView setpview = $(rootView, R.id.step_view);
        List<StepBean> stepsBeanList = new ArrayList<>();
        StepBean stepBean0 = new StepBean("第1天", 1);
        StepBean stepBean1 = new StepBean("第2天", 1);
        StepBean stepBean2 = new StepBean("第3天", 1);
        StepBean stepBean3 = new StepBean("第4天", 0);
        StepBean stepBean4 = new StepBean("第5天", -1);
        StepBean stepBean5 = new StepBean("第6天", -1);
        StepBean stepBean6 = new StepBean("第7天", -1);
        stepsBeanList.add(stepBean0);
        stepsBeanList.add(stepBean1);
        stepsBeanList.add(stepBean2);
        stepsBeanList.add(stepBean3);
        stepsBeanList.add(stepBean4);
        stepsBeanList.add(stepBean5);
        stepsBeanList.add(stepBean6);

        setpview.setStepViewTexts(stepsBeanList)//总步骤
                .setTextSize(10)//set textSize
                .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(mActivity, R.color.col_red))//设置StepsViewIndicator完成线的颜色
                .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(mActivity, R.color.txt_gray))//设置StepsViewIndicator未完成线的颜色
                .setStepViewComplectedTextColor(ContextCompat.getColor(mActivity, R.color.txt_black))//设置StepsView text完成线的颜色
                .setStepViewUnComplectedTextColor(ContextCompat.getColor(mActivity, R.color.txt_gray))//设置StepsView text未完成线的颜色
                .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(mActivity, R.drawable.ic_stepview_complted_red_packet))//设置StepsViewIndicator CompleteIcon
                .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(mActivity, R.drawable.ic_stepview_default_red_packet))//设置StepsViewIndicator DefaultIcon
                .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(mActivity, R.drawable.ic_stepview_default_red_packet));//设置StepsViewIndicator AttentionIcon
    }


    private void initTopLayout(View rootView) {
        ImageView imageView = $(rootView, R.id.iv_welfare_login_icon);
        TextView tvGoldCoinCount = $(rootView, R.id.tv_gold_coin_count_today_sign_in);
        TextView tvSignInCount = $(rootView, R.id.tv_continuous_sign_in_count);
        TextView btnSignInNow = $(rootView, R.id.tv_asBtn_sign_in_now);

        GlideUtil.loadRoundSquare(mActivity, imageView, "url", 0);

        initBannerView(rootView);
    }

    private void initBannerView(View rootView) {
        mBanner = $(rootView, R.id.banner_welfare);
        //设置banner样式
//        mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE);
        //设置图片加载器
        mBanner.setImageLoader(new GlideImageLoader2());
        //设置banner动画效果
        mBanner.setBannerAnimation(Transformer.Default);
        //设置自动轮播，默认为true
        mBanner.isAutoPlay(true);
        //设置轮播时间
        mBanner.setDelayTime(3500);
        //设置指示器位置（当banner模式中有指示器时）
        mBanner.setIndicatorGravity(BannerConfig.CENTER);
        mBanner.setOnBannerListener(this);

        List<BannerADEntity> bannerADs = getBannerADs();
        //设置图片集合
        mBanner.setImages(bannerADs);
        //banner设置方法全部调用完毕时最后调用

        mBanner.start();
    }

    private List<BannerADEntity> getBannerADs() {
        List<BannerADEntity> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            BannerADEntity ad = new BannerADEntity();
            ad.setTitle("AD标题-----" + i);
            ad.setType(1);
            ad.setImgUrl("https://p9-tt.byteimg.com/large/pgc-image/5489f6a4f7ac41e18a9164b650a4ba9b");
            list.add(ad);
        }
        return list;
    }

}
