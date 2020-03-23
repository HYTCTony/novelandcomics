package com.huli.foxread.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
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
import com.huli.foxread.entity.CapitalEntity;
import com.huli.foxread.entity.FUser;
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
import com.huli.foxread.ui.activities.SignInActivity;
import com.huli.foxread.ui.adapters.WelfareMissionAdapter;
import com.huli.foxread.ui.adapters.WelfareReadMissionAdapter;
import com.huli.foxread.ui.base.BaseFragment;
import com.huli.foxread.utils.StatusBarUtils;
import com.huli.foxread.utils.Tos;
import com.kongzue.dialog.v3.TipDialog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainWelfareFragment extends BaseFragment implements OnBannerListener, View.OnClickListener, OnRefreshListener {
    private static final int REQCODE_FILL_INVITE_CODE = 0x1234;
    private static final int REQCODE_NOR_SIGNIN = 0x5241;

    private SmartRefreshLayout mRefreshLayout;
    private TextView btnClick2Login;
    private LinearLayout btnGoldCoinUsable;
    private TextView tvGoldCoin;            //金币余额

    private RecyclerView recyclerView;
    private WelfareReadMissionAdapter mAdapter;

    private View headViewTop;
    private Banner mBanner;
    private List<BannerADEntity> bannerDatas;
    private TextView tvGoldCoinCount, tvSignInCount;
    private TextView btnSignInNow;

    private View headViewNewBie;
    private RecyclerView rvNewbie;

    private View headViewReadingMission;

    private View headViewDaily;
    private RecyclerView rvDaily;

    private int signInFlag = -1;


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

        tvGoldCoinCount = $(view, R.id.tv_gold_coin_count_today_sign_in);
        tvSignInCount = $(view, R.id.tv_continuous_sign_in_count);
        btnSignInNow = $(view, R.id.tv_asBtn_sign_in_now);

        recyclerView = $(view, R.id.recyclerView_reading_task);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mAdapter = new WelfareReadMissionAdapter();
        recyclerView.setAdapter(mAdapter);
        initTopLayout();
    }

    @Override
    public void setListener() {
        btnClick2Login.setOnClickListener(this);
        btnGoldCoinUsable.setOnClickListener(this);
        btnSignInNow.setOnClickListener(this);
        mRefreshLayout.setOnRefreshListener(this);

        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.btn_welfare_mission_action) {
                WelfareReadTaskEntity data = mAdapter.getData().get(position);
                if (data.getState() == 0) {
                    ((MainActivity) mActivity).switch2Bookstore();
                } else if (data.getState() == 1) {
                    reqMissionComplete(data.getId(), data.getSubTaskId());
                }
            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {
        EventBus.getDefault().register(this);

        displayIsLoginUI(mContext);

        reqTopBannerData();

        reqGetWerfareTasks(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onUserInfoChangeEvent(FUser event) {
        displayIsLoginUI(mActivity);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onCapitalRefreshEvent(CapitalEntity event) {
        tvGoldCoin.setText(String.valueOf(event.getScore()));
    }


    @Override
    public void onResume() {
        super.onResume();
        reqGetWerfareTasks(false);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            StatusBarUtils.setStatusBarTextDark(mActivity, false);
        }
    }

    private void displayIsLoginUI(Context context) {
        boolean isVisitor = UserInfoCache.getIsVisitor(context);
        if (isVisitor) {
            btnClick2Login.setVisibility(View.VISIBLE);
            btnGoldCoinUsable.setVisibility(View.GONE);
        } else {
            btnClick2Login.setVisibility(View.GONE);
            btnGoldCoinUsable.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        reqTopBannerData();
        reqGetWerfareTasks(false);

        ((MainActivity) mActivity).reqMyCapitalDetail();
    }

    @Override
    public void onClick(View view) {
        if (onMoreClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.btn_click_2_login:
                startActivity(new Intent(mActivity, LoginActivity.class));
                break;
            case R.id.ll_asBtn_gold_coin_usable:
                startActivity(new Intent(mActivity, MyGoldCoinActivity.class));
                break;
            case R.id.tv_asBtn_sign_in_now:
                if (UserInfoCache.getIsVisitor(mActivity)) {
                    //去登陆
                    startActivity(new Intent(mActivity, LoginActivity.class));
                } else {
                    //签到页面
                    startActivityForResult(new Intent(mActivity, SignInActivity.class), REQCODE_NOR_SIGNIN);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       /* if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQCODE_FILL_INVITE_CODE) {
                reqGetWerfareTasks(false);
            } else if (requestCode == REQCODE_NOR_SIGNIN) {
                reqGetWerfareTasks(false);
            }
        }*/
    }

    private void initReadingMossion(List<WelfareReadTaskEntity> readTasks) {
        if (headViewReadingMission == null) {
            headViewReadingMission = LayoutInflater.from(mActivity).inflate(R.layout.layout_rv_head_welfare_reading_mission, recyclerView, false);
            mAdapter.addHeaderView(headViewReadingMission);
        }

        List<WelfareReadTaskEntity> readMissions = new ArrayList<>();           //自己构建的Adapter数据
        for (int i = 0; i < readTasks.size(); i++) {
            WelfareReadTaskEntity pTask = readTasks.get(i);

            List<ReadSubTaskBean> subTasks = pTask.getTask();   //获取子任务
            if (subTasks != null && subTasks.size() > 0) {      //有子任务
                for (int j = 0; j < subTasks.size(); j++) {
                    ReadSubTaskBean wfSubTaskBean = subTasks.get(j);

                    readMissions.add(new WelfareReadTaskEntity(pTask.getId(), wfSubTaskBean.getName(), pTask.getType(), pTask.getContent(), pTask.getStatus(),
                            pTask.getWelfare_category_id(), wfSubTaskBean.getReward(), pTask.getIs_new_man(), pTask.getFrequency(), pTask.getNumber(),
                            pTask.getLink(), pTask.getHttp_logo_image(), wfSubTaskBean.getComplete_task(), wfSubTaskBean.getId(), wfSubTaskBean.getState(), null));
                }
            } else {
                pTask.setTask(null);
                readMissions.add(pTask);
            }
        }
        mAdapter.setNewData(readMissions);
    }


    private void initDailyLayout(List<WelfareTaskEntity> datas) {
        if (headViewDaily == null) {
            headViewDaily = LayoutInflater.from(mActivity).inflate(R.layout.layout_rv_head_daily_mission, recyclerView, false);
            mAdapter.addHeaderView(headViewDaily);
            rvDaily = $(headViewDaily, R.id.recyclerView_daily_mission_welfare);
            rvDaily.setLayoutManager(new LinearLayoutManager(mActivity));
            rvDaily.setNestedScrollingEnabled(false);
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
            rvNewbie.setNestedScrollingEnabled(false);
        }
        WelfareMissionAdapter mNewbieAdapter = new WelfareMissionAdapter();
        rvNewbie.setAdapter(mNewbieAdapter);
        mNewbieAdapter.setNewData(mission.getOther_new());
        mNewbieAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            WelfareTaskEntity data = mNewbieAdapter.getData().get(position);
            if (data.getLink().equals(Consts.INVITATION)) {                     //去邀请
                go2InviteFriend();
            } else if (data.getLink().equals(Consts.BE_INVITATION)) {           //去填写
                go2FillInviteCode();
            } else if (data.getLink().equals(Consts.EVERYDAY_READING)) {        //去阅读
                ((MainActivity) mActivity).switch2Bookstore();
            }
        });

        HorizontalStepView setpview = $(headViewNewBie, R.id.step_view);
        NewBieSignInTaskEntity newSignInMission = mission.getNew_sign_in();
        if (newSignInMission != null) {
            $(headViewNewBie, R.id.ctl_gold_coin_daily_newbie).setVisibility(View.VISIBLE);
            TextView btnReceive = $(headViewNewBie, R.id.btn_receive_nb_signin_rewards);
            if (newSignInMission.getComplete_task() == 0) {
                btnReceive.setEnabled(true);
                btnReceive.setText(R.string.txt_go2_received);
                btnReceive.setBackgroundResource(R.drawable.ripple_semicircle_btn_gradual_bg_red);
                btnReceive.setTextColor(ContextCompat.getColor(mActivity, R.color.txt_white));
                btnReceive.setOnClickListener(new OnClickEvent() {
                    @Override
                    public void singleClick(View v) {
                        //完成新人签到任务
                        if (UserInfoCache.getIsVisitor(mActivity)) {
                            startActivity(new Intent(mActivity, LoginActivity.class));
                            return;
                        }
                        reqMissionComplete(newSignInMission.getId(), null);
                    }
                });
            } else {
                btnReceive.setEnabled(false);
                btnReceive.setBackgroundResource(R.drawable.shape_btn_semicircle_bg_disabled);
                btnReceive.setText(R.string.txt_already_received);
                btnReceive.setTextColor(ContextCompat.getColor(mActivity, R.color.txt_gray));
            }

            int completeSum = newSignInMission.getComplete_sum();
            List<StepBean> stepsBeanList = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                StepBean stepBean;
                if (i < completeSum) {
                    stepBean = new StepBean(String.format(getResources().getString(R.string.txt_day_x), i + 1), StepBean.STEP_COMPLETED);
                } else {
                    stepBean = new StepBean(String.format(getResources().getString(R.string.txt_day_x), i + 1), StepBean.STEP_UNDO);
                }
                stepsBeanList.add(stepBean);
            }
            setpview.setStepViewTexts(stepsBeanList)//总步骤
                    .setTextSize(10)//set textSize
                    .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(mActivity, R.color.txt_red))//设置StepsViewIndicator完成线的颜色
                    .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(mActivity, R.color.txt_gray))//设置StepsViewIndicator未完成线的颜色
                    .setStepViewComplectedTextColor(ContextCompat.getColor(mActivity, R.color.txt_red))//设置StepsView text完成的颜色
                    .setStepViewUnComplectedTextColor(ContextCompat.getColor(mActivity, R.color.txt_gray))//设置StepsView text未完成的颜色
                    .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(mActivity, R.drawable.ic_stepview_complted_red_packet))//设置StepsViewIndicator CompleteIcon
                    .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(mActivity, R.drawable.ic_stepview_default_red_packet))//设置StepsViewIndicator DefaultIcon
                    .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(mActivity, R.drawable.ic_stepview_default_red_packet));//设置StepsViewIndicator AttentionIcon
        } else {
            $(headViewNewBie, R.id.ctl_gold_coin_daily_newbie).setVisibility(View.GONE);
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
                            signInFlag = normalSignInTask.getComplete_task();   //1是已签到，0是未签到
                            if (signInFlag == 0) {
                                btnSignInNow.setText(R.string.txt_sign_in_immediately);
                                btnSignInNow.setTextColor(ContextCompat.getColor(mActivity, R.color.txt_white));
                                btnSignInNow.setBackgroundResource(R.drawable.ripple_semicircle_btn_gradual_bg_yellow);
                            } else if (signInFlag == 1) {
                                btnSignInNow.setText(R.string.txt_already_sign_in);
                                btnSignInNow.setTextColor(ContextCompat.getColor(mActivity, R.color.txt_gray));
                                btnSignInNow.setBackgroundResource(R.drawable.shape_btn_semicircle_bg_disabled);
                            }
                            if (!UserInfoCache.getIsVisitor(mActivity)) {
                                tvGoldCoinCount.setText(setNumColor(mActivity, String.format(getResources().getString(R.string.txt_today_signin_add_goldcoin_x), normalSignInTask.getReward())));
                                tvSignInCount.setText(setNumColor(mActivity, String.format(getResources().getString(R.string.txt_continuous_sign_in_day_x), normalSignInTask.getSign_successions())));
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
                            List<WelfareReadTaskEntity> readTasks = welfareEntity.getRead();        //得到的数据
                            initReadingMossion(readTasks);
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
     * 完成任务领取奖励
     */
    private void reqMissionComplete(String missionId, String subMissionId) {
        OkGo.<String>get(Consts.WELFARE_COMPLETE_API)
                .params(Consts.MISSION_ID, missionId)
                .params(Consts.SUB_MISSION_ID, subMissionId)
                .execute(new LtbCallback((AppCompatActivity) mActivity) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<String> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<String>>() {
                                });
                        if (entity.error_code == 0) {
                            reqGetWerfareTasks(false);
                            TipDialog.show((AppCompatActivity) mActivity, entity.msg, TipDialog.TYPE.SUCCESS);
                        } else {
                            Tos.showShort(mActivity, entity.msg);
                        }
                    }
                });
    }

    /**
     * 获取banner
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
        if (UserInfoCache.getIsVisitor(mActivity)) {
            startActivity(new Intent(mActivity, LoginActivity.class));
            return;
        }
        Intent intent = new Intent(mActivity, InvitationCodeActivity.class);
        startActivityForResult(intent, REQCODE_FILL_INVITE_CODE);
    }

    private void go2InviteFriend() {
        if (UserInfoCache.getIsVisitor(mActivity)) {
            startActivity(new Intent(mActivity, LoginActivity.class));
            return;
        }
        Intent intent = new Intent(mActivity, InviteFriendsActivity.class);
        startActivity(intent);
    }

    //字符串中的数字变色
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
