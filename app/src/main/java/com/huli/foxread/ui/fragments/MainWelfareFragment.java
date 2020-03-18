package com.huli.foxread.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baoyachi.stepview.HorizontalStepView;
import com.baoyachi.stepview.bean.StepBean;
import com.huli.foxread.R;
import com.huli.foxread.cache.UserInfoCache;
import com.huli.foxread.callbacks.ookkggoo.LtbCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.engines.GlideImageLoader2;
import com.huli.foxread.entity.BannerADEntity;
import com.huli.foxread.entity.NewBieSignInTaskEntity;
import com.huli.foxread.entity.NormalSignInTaskEntity;
import com.huli.foxread.entity.ReadSubTaskBean;
import com.huli.foxread.entity.WelfareIndexEntity;
import com.huli.foxread.entity.WelfareNewBieTaskEntity;
import com.huli.foxread.entity.WelfareReadTaskEntity;
import com.huli.foxread.entity.WelfareTaskEntity;
import com.huli.foxread.listeners.OnClickEvent;
import com.huli.foxread.ui.activities.InvitationCodeActivity;
import com.huli.foxread.ui.activities.InviteFriendsActivity;
import com.huli.foxread.ui.activities.LoginActivity;
import com.huli.foxread.ui.activities.MainActivity;
import com.huli.foxread.ui.activities.MyGoldCoinActivity;
import com.huli.foxread.ui.adapters.WelfareMissionAdapter;
import com.huli.foxread.ui.adapters.WelfareReadMissionAdapter;
import com.huli.foxread.ui.base.BaseFragment;
import com.huli.foxread.utils.StatusBarUtils;
import com.huli.foxread.utils.Tos;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainWelfareFragment extends BaseFragment implements OnBannerListener, View.OnClickListener {

    private SmartRefreshLayout mRefreshLayout;
    private TextView btnClick2Login;
    private LinearLayout btnGoldCoinUsable;
    private TextView tvGoldCoin;

    private RecyclerView recyclerView;
    private WelfareReadMissionAdapter mAdapter;

    private View headViewTop;
    private Banner mBanner;
    private List<BannerADEntity> bannerDatas;
    private TextView tvGoldCoinCount, tvSignInCount;
    private TextView btnSignInNow;

    private View headViewNewBie;
    private RecyclerView rvNewbie;

    private View headViewDaily;
    private RecyclerView rvDaily;


//    private boolean completeInit;       //是否完成初始化，防止网络状态异常导致没有初始化

    @Override
    public int bindLayout() {
        return R.layout.fragment_main_welfare;
    }

    @Override
    public void setStatusBar(View view) {
        StatusBarUtils.setStatusBarTextDark(mActivity, false);
        StatusBarUtils.offsetView(mActivity, $(view, R.id.toolbar_title_bar_welfare));
    }

    @Override
    public void initView(View view) {

        mRefreshLayout = $(view, R.id.smartRefreshLayout_welfare);
        mRefreshLayout.setEnableScrollContentWhenRefreshed(false);
        btnClick2Login = $(view, R.id.btn_click_2_login);
        btnGoldCoinUsable = $(view, R.id.ll_asBtn_gold_coin_usable);
        tvGoldCoin = $(view, R.id.tv_gold_coin_usable);

        ImageView imageView = $(view, R.id.iv_welfare_login_icon);
        tvGoldCoinCount = $(view, R.id.tv_gold_coin_count_today_sign_in);
        tvSignInCount = $(view, R.id.tv_continuous_sign_in_count);
        btnSignInNow = $(view, R.id.tv_asBtn_sign_in_now);

//        GlideUtil.loadRoundSquare(mActivity, imageView, "url", 0);

        recyclerView = $(view, R.id.recyclerView_reading_task);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mAdapter = new WelfareReadMissionAdapter();
        recyclerView.setAdapter(mAdapter);
        initTopLayout();

//        LayoutInflater inflater = LayoutInflater.from(mActivity);
//        View headViewTop = inflater.inflate(R.layout.layout_rv_head_welfare_top, recyclerView, false);
//        View headViewNewbie = inflater.inflate(R.layout.layout_rv_head_newbie_mission, recyclerView, false);
//        View headViewDaily = inflater.inflate(R.layout.layout_rv_head_daily_mission, recyclerView, false);

//        mAdapter.addHeaderView(headViewTop, 0);
//        mAdapter.addHeaderView(headViewNewbie, 1);
//        mAdapter.addHeaderView(headViewDaily, 2);

//        initTopLayout(headViewTop);

//        initNewbieMission(headViewNewbie);

//        initDailyLayout(headViewDaily);
    }

    @Override
    public void setListener() {
        btnSignInNow.setOnClickListener(this);

        mRefreshLayout.setOnRefreshListener(refreshLayout -> {
            reqTopBannerData();

            reqGetWerfareTasks(false);
        });
        mAdapter.addChildClickViewIds(R.id.btn_welfare_mission_action);
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.btn_welfare_mission_action) {
                WelfareReadTaskEntity data = mAdapter.getData().get(position);
                if (data.getComplete_task() == 0) {
                    ((MainActivity) mActivity).switch2Bookstore();
                } else {
                    Tos.showShort(mActivity, "领取奖励");
                }
            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {
        boolean isVisitor = UserInfoCache.getIsVisitor(mContext);
        if (isVisitor) {
            btnClick2Login.setVisibility(View.VISIBLE);
            btnGoldCoinUsable.setVisibility(View.GONE);
            btnClick2Login.setOnClickListener(new OnClickEvent() {
                @Override
                public void singleClick(View v) {
                    startActivity(new Intent(mActivity, LoginActivity.class));
                }
            });
        } else {
            btnClick2Login.setVisibility(View.GONE);
            btnGoldCoinUsable.setVisibility(View.VISIBLE);
            btnGoldCoinUsable.setOnClickListener(new OnClickEvent() {
                @Override
                public void singleClick(View v) {
                    startActivity(new Intent(mActivity, MyGoldCoinActivity.class));
                }
            });
        }


        reqTopBannerData();

        reqGetWerfareTasks(true);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            StatusBarUtils.setStatusBarTextDark(mActivity, false);
        }
    }

    private int signInFlag = -1;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_asBtn_sign_in_now:
                if (signInFlag == 0) {
                    Tos.showShort(mActivity, "签到");
                } else if (signInFlag == 1) {
                    Tos.showShort(mActivity, "已经签到");
                } else {

                }
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

    private void initDailyLayout(List<WelfareTaskEntity> datas) {
//        TextView btnGo2Invite = $(rootView, R.id.btn_go2_invite_friends);
//        btnGo2Invite.setText(R.string.txt_go2_invite_friends);
//        btnGo2Invite.setOnClickListener(this);
        if (headViewDaily == null) {
            headViewDaily = LayoutInflater.from(mActivity).inflate(R.layout.layout_rv_head_daily_mission, recyclerView, false);
            mAdapter.addHeaderView(headViewDaily);
            rvDaily = $(headViewDaily, R.id.recyclerView_daily_mission_welfare);
            rvDaily.setLayoutManager(new LinearLayoutManager(mActivity));
        }
        WelfareMissionAdapter dailyAdapter = new WelfareMissionAdapter();
        rvDaily.setAdapter(dailyAdapter);
        dailyAdapter.setNewData(datas);
        dailyAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            WelfareTaskEntity data = dailyAdapter.getData().get(position);
            if (data.getLink().equals(Consts.INVITATION)) {
                go2InviteFriend();
//                    Tos.showShort(mActivity, "去邀请");
            } else if (data.getLink().equals(Consts.BE_INVITATION)) {
                go2FillInviteCode();
//                    Tos.showShort(mActivity, "去填写");
            } else if (data.getLink().equals(Consts.EVERYDAY_READING)) {
//                    Tos.showShort(mActivity, "去阅读");
                ((MainActivity) mActivity).switch2Bookstore();
            }
        });
    }

    private void initNewbieMission(WelfareNewBieTaskEntity mission) {
        if (headViewNewBie == null) {
            headViewNewBie = LayoutInflater.from(mActivity).inflate(R.layout.layout_rv_head_newbie_mission, recyclerView, false);
            mAdapter.addHeaderView(headViewNewBie);
            rvNewbie = $(headViewNewBie, R.id.recyclerView_newbie_task_welfare);
            rvNewbie.setLayoutManager(new LinearLayoutManager(mActivity));
        }
        WelfareMissionAdapter mNewbieAdapter = new WelfareMissionAdapter();
        rvNewbie.setAdapter(mNewbieAdapter);
        mNewbieAdapter.setNewData(mission.getOther_new());
        mNewbieAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            WelfareTaskEntity data = mNewbieAdapter.getData().get(position);
            if (data.getLink().equals(Consts.INVITATION)) {
                go2InviteFriend();
//                    Tos.showShort(mActivity, "去邀请");
            } else if (data.getLink().equals(Consts.BE_INVITATION)) {
                go2FillInviteCode();
//                    Tos.showShort(mActivity, "去填写");
            } else if (data.getLink().equals(Consts.EVERYDAY_READING)) {
//                    Tos.showShort(mActivity, "去阅读");
                ((MainActivity) mActivity).switch2Bookstore();
            }
        });

        HorizontalStepView setpview = $(headViewNewBie, R.id.step_view);
        NewBieSignInTaskEntity newSignInMission = mission.getNew_sign_in();
        if (newSignInMission != null) {
            TextView btnReceive = headViewNewBie.findViewById(R.id.btn_receive_nb_signin_rewards);
            btnReceive.setOnClickListener(new OnClickEvent() {
                @Override
                public void singleClick(View v) {
                    Tos.showShort(mActivity, "去领取");
                }
            });
            int completeSum = newSignInMission.getComplete_sum();
            setpview.setVisibility(View.VISIBLE);
            List<StepBean> stepsBeanList = new ArrayList<>();
            for (int i = 0; i < completeSum; i++) {
                StepBean stepBean = new StepBean(String.format(getString(R.string.txt_day_x), i + 1), 1);
                stepsBeanList.add(stepBean);
            }
            for (int j = 0; j < (7 - completeSum); j++) {
                StepBean stepBean = new StepBean(String.format(getString(R.string.txt_day_x), j + 1), 0);
                stepsBeanList.add(stepBean);
            }
//            StepBean stepBean6 = new StepBean("第7天", -1);

            setpview.setStepViewTexts(stepsBeanList)//总步骤
                    .setTextSize(10)//set textSize
                    .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(mActivity, R.color.col_red))//设置StepsViewIndicator完成线的颜色
                    .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(mActivity, R.color.txt_gray))//设置StepsViewIndicator未完成线的颜色
                    .setStepViewComplectedTextColor(ContextCompat.getColor(mActivity, R.color.txt_black))//设置StepsView text完成线的颜色
                    .setStepViewUnComplectedTextColor(ContextCompat.getColor(mActivity, R.color.txt_gray))//设置StepsView text未完成线的颜色
                    .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(mActivity, R.drawable.ic_stepview_complted_red_packet))//设置StepsViewIndicator CompleteIcon
                    .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(mActivity, R.drawable.ic_stepview_default_red_packet))//设置StepsViewIndicator DefaultIcon
                    .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(mActivity, R.drawable.ic_stepview_default_red_packet));//设置StepsViewIndicator AttentionIcon
        } else {
            setpview.setVisibility(View.GONE);
        }
    }


    private void initTopLayout() {
        headViewTop = LayoutInflater.from(mActivity).inflate(R.layout.layout_rv_head_welfare_top, recyclerView, false);
        mAdapter.addHeaderView(headViewTop);

        initBannerView(headViewTop);
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

        //设置图片集合
//        mBanner.setImages(bannerADs);
        //banner设置方法全部调用完毕时最后调用

        mBanner.start();
    }


    /**
     * 福利任务列表
     */
    private void reqGetWerfareTasks(boolean showDialog) {
        OkGo.<String>get(Consts.WELFARE_LIST_API)
                .execute(new LtbCallback((AppCompatActivity) mActivity, showDialog) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<WelfareIndexEntity> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<WelfareIndexEntity>>() {
                                });
                        if (entity.error_code == 0) {
                            WelfareIndexEntity welfareEntity = entity.getData();

                            //普通签到任务
                            NormalSignInTaskEntity normalSignInTask = welfareEntity.getSign_in();
                            signInFlag = normalSignInTask.getSign_successions();
                            if (!UserInfoCache.getIsVisitor(mActivity)) {
                                tvGoldCoinCount.setText(setNumColor(mActivity, String.format(getString(R.string.txt_today_signin_add_goldcoin_x), normalSignInTask.getReward())));
                                tvSignInCount.setText(setNumColor(mActivity, String.format(getString(R.string.txt_continuous_sign_in_day_x), normalSignInTask.getFrequency())));
                            } else {
                                tvGoldCoinCount.setText(null);
                                tvSignInCount.setText(null);
                            }

                            //新人任务
                            WelfareNewBieTaskEntity newManMission = welfareEntity.getNew_man();
                            if (newManMission != null) {
                                initNewbieMission(newManMission);
                            }

                            //日常任务
                            List<WelfareTaskEntity> dailyMissions = welfareEntity.getDay();
                            if (dailyMissions != null && dailyMissions.size() > 0) {
                                initDailyLayout(dailyMissions);
                            }

                            //阅读任务
                            List<WelfareReadTaskEntity> readTasks = welfareEntity.getRead();
                            List<WelfareReadTaskEntity> readMissions = new ArrayList<>();
                            for (int i = 0; i < readTasks.size(); i++) {
                                WelfareReadTaskEntity pTask = readTasks.get(i);
                                List<ReadSubTaskBean> subTasks = pTask.getTask();
                                if (subTasks != null && subTasks.size() > 0) {
                                    for (int j = 0; j < subTasks.size(); j++) {
                                        ReadSubTaskBean wfSubTaskBean = subTasks.get(j);
                                        pTask.setSubTaskId(wfSubTaskBean.getId());
                                        pTask.setName(wfSubTaskBean.getName());
                                        pTask.setReward(wfSubTaskBean.getReward());
                                        pTask.setComplete_task(wfSubTaskBean.getComplete_task());
                                        pTask.setTask(null);
                                        readMissions.add(pTask);
                                    }
                                } else {
                                    pTask.setTask(null);
                                    readMissions.add(pTask);
                                }
                            }
                            View headViewReadingMission = LayoutInflater.from(mActivity).inflate(R.layout.layout_rv_head_welfare_reading_mission, recyclerView, false);
                            mAdapter.addHeaderView(headViewReadingMission);
                            mAdapter.setNewData(readMissions);
                        }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        mRefreshLayout.finishRefresh();
                    }
                });
    }

    /**
     * banner
     */
    private void reqTopBannerData() {
        OkGo.<String>get(Consts.BANNER_READ_API)
                .params(Consts.POSITION, Consts.TYPE_WELFARE)
                .execute(new LtbCallback((AppCompatActivity) mActivity, false) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<List<BannerADEntity>> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<List<BannerADEntity>>>() {
                                });
                        if (entity.error_code == 0) {
                            bannerDatas = entity.getData();
                            if (bannerDatas != null) {
                                mBanner.update(bannerDatas);
                            }
                        }
                    }
                });
    }


    private void go2FillInviteCode() {
        Intent intent = new Intent(mActivity, InvitationCodeActivity.class);
        startActivity(intent);
    }

    private void go2InviteFriend() {
        Intent intent = new Intent(mActivity, InviteFriendsActivity.class);
        startActivity(intent);
    }


    private static SpannableStringBuilder setNumColor(Context context, String str) {
        SpannableStringBuilder style = new SpannableStringBuilder(str);
        for (int i = 0; i < str.length(); i++) {
            char a = str.charAt(i);
            if (a >= '0' && a <= '9') {
                style.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.txt_red)), i, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return style;
    }
}
