package com.huli.foxread.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.huli.foxread.R;
import com.huli.foxread.RxHttp;
import com.huli.foxread.cache.TokenCache;
import com.huli.foxread.cache.UserInfoCache;
import com.huli.foxread.config.AdConfig;
import com.huli.foxread.config.TogetherAdConst;
import com.huli.foxread.contact.Common;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.ebsevent.WelfareChangeEvent;
import com.huli.foxread.engines.GlideImageLoaderWf;
import com.huli.foxread.entity.BannerADEntity;
import com.huli.foxread.entity.CapitalEntity;
import com.huli.foxread.entity.FUser;
import com.huli.foxread.entity.MissionEntity;
import com.huli.foxread.entity.MissionGroupEntity;
import com.huli.foxread.entity.SignInMissionEntity;
import com.huli.foxread.entity.WelfarePageEntity;
import com.huli.foxread.entity.sections.MissionSection2;
import com.huli.foxread.rxhttp.ErrorInfo;
import com.huli.foxread.rxhttp.OnError;
import com.huli.foxread.rxhttp.Tip;
import com.huli.foxread.ui.activities.AdvFreeSuccessActivity;
import com.huli.foxread.ui.activities.CommonWebActivity;
import com.huli.foxread.ui.activities.LoginActivity;
import com.huli.foxread.ui.activities.MainActivity;
import com.huli.foxread.ui.activities.MyGoldCoinActivity;
import com.huli.foxread.ui.activities.SignInActivity;
import com.huli.foxread.ui.adapters.WelfareMissionAdapter2;
import com.huli.foxread.ui.base.BaseFragment;
import com.huli.foxread.utils.ClickJumpUtil;
import com.huli.foxread.utils.StatusBarUtils;
import com.hytc.ads.helper.stimulatevideo.TogetherAdStimulate;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.rxjava.rxlife.RxLife;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainWelfareFragment2 extends BaseFragment implements OnBannerListener, View.OnClickListener, OnRefreshListener, OnItemChildClickListener {

    private SmartRefreshLayout mRefreshLayout;
    private TextView btnClick2Login;
    private LinearLayout btnGoldCoinUsable;
    private TextView tvGoldCoin;            //金币余额

    private RecyclerView recyclerView;
    private WelfareMissionAdapter2 mAdapter;

    private View headViewTop;
    private Banner mBanner;
    private List<BannerADEntity> bannerDatas;
    private TextView tvGoldCoinCount, tvSignInCount;
    private TextView btnSignInNow;

    private View footerRule;

    //当Fragment可见的时候刷新书架
    private boolean shouldRefresh = false;

    /* private TTAdNative mTTAdNative;
     private TTRewardVideoAd mttRewardVideoAd;

     private boolean mIsExpress = false; //是否请求模板广告
     private boolean mHasShowDownloadActive = false;*/
    /*观看视频验证*/
    private boolean verify = false;

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
        mAdapter = new WelfareMissionAdapter2();
        recyclerView.setAdapter(mAdapter);
        mAdapter.setEmptyView(R.layout.layout_empty);
        initTopLayout();

        initFooterView();
    }

    @Override
    public void setListener() {
        btnClick2Login.setOnClickListener(this);
        btnGoldCoinUsable.setOnClickListener(this);
        btnSignInNow.setOnClickListener(this);
        mRefreshLayout.setOnRefreshListener(this);

        mAdapter.setOnItemChildClickListener(this);
    }


    @Override
    public void doBusiness(Context mContext) {
        EventBus.getDefault().register(this);

        boolean isTourist = UserInfoCache.getIsTourist(mActivity);
        displayIsLoginUI(isTourist);

        reqGetWerfareTasks();


        //step1:初始化sdk
        /*TTAdManager ttAdManager = TTAdManagerHolder.get();
        //step2:(可选，强烈建议在合适的时机调用):申请部分权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题。
//        TTAdManagerHolder.get().requestPermissionIfNecessary(mActivity);
        //step3:创建TTAdNative对象,用于调用广告请求接口
        mTTAdNative = ttAdManager.createAdNative(mActivity.getApplicationContext());*/

    }

    @Override
    public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
        if (onMoreClick()) {
            return;
        }
        if (view.getId() == R.id.btn_welfare_mission_action) {
            MissionSection2 data = mAdapter.getData().get(position);
            Object object = data.getObject();
            if (object instanceof MissionEntity) {
                MissionEntity missionEntity = (MissionEntity) object;
                if (missionEntity.getStatus() == 0) {
                    String mlink = missionEntity.getLink();
                    if (mlink.equals(Common.SIGNIN_NEWBIE)) {       //新人七天签到
                        if (UserInfoCache.getIsTourist(mActivity)) {
                            LoginActivity.start(mActivity);
                        }
                    } else if (mlink.equals(Common.WATCH_VIDEO)) {
                        loadAd(missionEntity.getType());
                    } else {
                        ClickJumpUtil.handleJump(mActivity, mlink, missionEntity.getJump());
                    }
                } else if (missionEntity.getStatus() == 1) {
                    //领取免广告奖励
                    if (missionEntity.getLink().equals(Common.WATCH_VIDEO) && missionEntity.getType().equals(Common.VIDEO_ADVERT)) {
                        AdvFreeSuccessActivity.start(mActivity);
                        return;
                    }
                    //一般任务领取奖励
                    reqMissionComplete(missionEntity.getId());
                }
            }
        }
    }

    /**
     * 显示视频广告
     *
     * @param vType
     */
    private void loadAd(final String vType) {
        String token = TokenCache.getToken(mActivity);
        String adConst;
        if (vType.equals(Common.VIDEO_BONUSES)) {
            adConst = TogetherAdConst.AD_WELFARE_STIMULATE_COIN;
        } else {
            adConst = TogetherAdConst.AD_WELFARE_STIMULATE_2;
        }

        TogetherAdStimulate.showAdFull(mActivity, token, AdConfig.welfareBonusesConfig(mActivity), adConst, new TogetherAdStimulate.AdListenerSplashFull() {

            @Override
            public void onStartRequest(@NotNull String channel) {
                WaitDialog.show((AppCompatActivity) mActivity, R.string.loading).setCancelable(false);
            }

            @Override
            public void onADClick(@NotNull String channel) {

            }

            @Override
            public void onAdFailed(@Nullable String failedMsg) {
                Tip.show(failedMsg);
            }

            @Override
            public void onAdRewardVerify(boolean rewardVerify) {
                verify = rewardVerify;
                if (verify) {
                    if (vType.equals(Common.VIDEO_BONUSES)) {
                        reportStimulateMission(Consts.WELFARE_CHANGEBONUSES_API);
                    } else if (vType.equals(Common.VIDEO_ADVERT)) {
                        reportStimulateMission(Consts.WELFARE_CHANGEADVERT_API);
                    }
                }
            }

            @Override
            public void onAdDismissed() {
                if (verify) {
                    if (vType.equals(Common.VIDEO_BONUSES)) {
                        reqGetWerfareTasks();
                    } else if (vType.equals(Common.VIDEO_ADVERT)) {
                        AdvFreeSuccessActivity.start(mActivity);
                    }
                } else {
                    Tip.show("激励视频奖励验证未通过");
                }
                verify = false;
            }

            @Override
            public void onAdPrepared(@NotNull String channel) {
                WaitDialog.dismiss();
            }
        });

        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
       /* AdSlot adSlot;
        if (vType.equals(Common.VIDEO_BONUSES)) {
            //个性化模板广告需要传入期望广告view的宽、高，单位dp，
            adSlot = new AdSlot.Builder()
                    .setCodeId(CsjAdsCode.GOLD_COIN_CODE_ID)
                    .setSupportDeepLink(true)
//                    .setRewardName("金币") //奖励的名称
//                    .setRewardAmount(3)  //奖励的数量
                    //模板广告需要设置期望个性化模板广告的大小,单位dp,激励视频场景，只要设置的值大于0即可
//                    .setExpressViewAcceptedSize(500, 500)
                    .setUserID(TokenCache.getToken(mActivity))//用户id,必传参数
                    .setMediaExtra("media_extra") //附加参数，可选
                    .setOrientation(TTAdConstant.VERTICAL) //必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL
                    .build();
        } else if (vType.equals(Common.VIDEO_ADVERT)) {
            //模板广告需要设置期望个性化模板广告的大小,单位dp,代码位是否属于个性化模板广告，请在穿山甲平台查看
            adSlot = new AdSlot.Builder()
                    .setCodeId(CsjAdsCode.ADV_FREE_CODE_ID)
                    .setSupportDeepLink(true)
//                    .setRewardName("金币") //奖励的名称
//                    .setRewardAmount(3)  //奖励的数量
                    .setUserID(TokenCache.getToken(mActivity))//用户id,必传参数
                    .setMediaExtra("media_extra") //附加参数，可选
                    .setOrientation(TTAdConstant.VERTICAL) //必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL
                    .build();
        } else {
            WaitDialog.dismiss();
            return;
        }
        //step5:请求广告
        mTTAdNative.loadRewardVideoAd(adSlot, new TTAdNative.RewardVideoAdListener() {
            @Override
            public void onError(int code, String message) {
                WaitDialog.dismiss();
            }

            //视频广告加载后，视频资源缓存到本地的回调，在此回调后，播放本地视频，流畅不阻塞。
            @Override
            public void onRewardVideoCached() {
                WaitDialog.dismiss();

                if (mttRewardVideoAd != null) {
                    //step6:在获取到广告后展示,强烈建议在onRewardVideoCached回调后，展示广告，提升播放体验
                    //该方法直接展示广告
//                    mttRewardVideoAd.showRewardVideoAd(RewardVideoActivity.this);

                    //展示广告，并传入广告展示的场景
                    mttRewardVideoAd.showRewardVideoAd(mActivity, TTAdConstant.RitScenes.CUSTOMIZE_SCENES, "foxread_welfare");
                    mttRewardVideoAd = null;
                } else {
//                    TToast.show(RewardVideoActivity.this, "请先加载广告");
                }
            }

            //视频广告的素材加载完毕，比如视频url等，在此回调后，可以播放在线视频，网络不好可能出现加载缓冲，影响体验。
            @Override
            public void onRewardVideoAdLoad(TTRewardVideoAd ad) {
                mttRewardVideoAd = ad;

                mttRewardVideoAd.setRewardAdInteractionListener(new TTRewardVideoAd.RewardAdInteractionListener() {
                    @Override
                    public void onAdShow() {
                    }

                    @Override
                    public void onAdVideoBarClick() {
                    }

                    @Override
                    public void onAdClose() {
                        if (vType.equals(Common.VIDEO_BONUSES)) {
                            reqGetWerfareTasks(false);
                        } else if (vType.equals(Common.VIDEO_ADVERT)) {
                            AdvFreeSuccessActivity.start(mActivity);
                        }
//                        if (mRewardVerify) {
//                            if (vType.equals(Common.VIDEO_BONUSES)) {
//                                reqGetWerfareTasks(false);
//                            } else if (vType.equals(Common.VIDEO_ADVERT)) {
//                                AdvFreeSuccessActivity.start(mActivity);
//                            }
//                            mRewardVerify = false;
//                        } else {
//                            if (vType.equals(Common.VIDEO_ADVERT)) {
//                                Toast.makeText(mActivity, "激励视频验证失败！", Toast.LENGTH_SHORT).show();
//                                reqGetWerfareTasks(false);
//                            }
//                        }
                    }

                    //视频播放完成回调
                    @Override
                    public void onVideoComplete() {
                    }

                    @Override
                    public void onVideoError() {
                    }

                    //视频播放完成后，奖励验证回调，rewardVerify：是否有效，rewardAmount：奖励梳理，rewardName：奖励名称
                    @Override
                    public void onRewardVerify(boolean rewardVerify, int rewardAmount, String rewardName) {
//                        mRewardVerify = rewardVerify;
                    }

                    @Override
                    public void onSkippedVideo() {
                    }
                });
            }
        });*/
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /*如用户登录 登出*/
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onUserInfoChangeEvent(FUser event) {
        boolean isTourist = event.isIs_tourist();
        displayIsLoginUI(isTourist);
        reqGetWerfareTasks();
    }

    /*刷新UI金币（资金）*/
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onCapitalRefreshEvent(CapitalEntity event) {
        tvGoldCoin.setText(String.valueOf(event.getScore()));
    }

    /*开放evb通知 刷新福利列表*/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWelfareChangeEvent(WelfareChangeEvent event) {
        if (event.isRefreshImmediately()) {
            reqGetWerfareTasks();
        } else {
            shouldRefresh = true;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            StatusBarUtils.setStatusBarTextDark(mActivity, false);

            if (isVisible() && shouldRefresh) {
//                Log.e("ssssssss", "onHiddenChanged可见");
                reqGetWerfareTasks();
                shouldRefresh = false;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isVisible() && shouldRefresh) {
//            Log.e("ssssssss", "onResume可见");
            reqGetWerfareTasks();
            shouldRefresh = false;
        }
    }

    private void displayIsLoginUI(boolean isTourist) {
        if (isTourist) {
            btnClick2Login.setVisibility(View.VISIBLE);
            btnGoldCoinUsable.setVisibility(View.GONE);
        } else {
            btnClick2Login.setVisibility(View.GONE);
            btnGoldCoinUsable.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        reqGetWerfareTasks();

        ((MainActivity) mActivity).reqMyCapitalDetail();
        ((MainActivity) mActivity).getUserReadTime();
    }

    @Override
    public void onClick(View view) {
        if (onMoreClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.btn_click_2_login:
                LoginActivity.start(mActivity);
                break;
            case R.id.ll_asBtn_gold_coin_usable:
                startActivity(new Intent(mActivity, MyGoldCoinActivity.class));
                break;
            case R.id.tv_asBtn_sign_in_now:
                startActivity(new Intent(mActivity, SignInActivity.class));
                break;
            default:
                break;
        }
    }


    @Override
    public void OnBannerClick(int position) {
        if (bannerDatas != null && bannerDatas.size() > position) {
            BannerADEntity entity = bannerDatas.get(position);
            ClickJumpUtil.handleJump(mActivity, entity.getLink(), entity.getJump());
        }
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

    private void initFooterView() {
        footerRule = LayoutInflater.from(mActivity).inflate(R.layout.layout_rv_footer_welfare_rule, recyclerView, false);
        mAdapter.addFooterView(footerRule);
        SpannableString spannableString = new SpannableString(getString(R.string.txt_tips_refer_2_rule));
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), spannableString.length() - 4, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent intent = new Intent(mActivity, CommonWebActivity.class);
                intent.putExtra(Common.KEY_URL, Consts.WELFARE_RULE_URL);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                /**set textColor**/
                ds.setColor(ContextCompat.getColor(mActivity, R.color.txt_black_191919));
                /**Remove the underline**/
                ds.setUnderlineText(true);
            }
        }, spannableString.length() - 4, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        TextView txRule = footerRule.findViewById(R.id.tv_welfare_rule);
        txRule.setMovementMethod(LinkMovementMethod.getInstance());//不设置 没有点击事件
        txRule.setHighlightColor(ContextCompat.getColor(mActivity, R.color.transparent));
        txRule.setText(spannableString);

        footerRule.setVisibility(View.GONE);
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
        mBanner.setImageLoader(new GlideImageLoaderWf());
        //设置banner动画效果
        mBanner.setBannerAnimation(Transformer.Default);
        //设置自动轮播，默认为true
        mBanner.isAutoPlay(true);
        //设置轮播时间
        mBanner.setDelayTime(6000);
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
    private void reqGetWerfareTasks() {
        RxHttp.postForm(Consts.WELFARE_LIST_API)
                .asResponse(WelfarePageEntity.class)
                .doFinally(() -> mRefreshLayout.finishRefresh())
                .to(RxLife.toMain(this))  //感知生命周期，并在主线程回调
                .subscribe(welfarePageEntity -> {
                    //banner
                    bannerDatas = welfarePageEntity.getBanner();
                    if (bannerDatas != null) {
                        mBanner.update(bannerDatas);
                    }

                    //普通签到
                    SignInMissionEntity signInEntity = welfarePageEntity.getSign_in();
                    int signInFlag = signInEntity.getStatus();
                    btnSignInNow.setText(signInEntity.getProgress());
                    if (signInFlag == 1) {
                        btnSignInNow.setTextColor(ContextCompat.getColor(mActivity, R.color.txt_white));
                        btnSignInNow.setBackgroundResource(R.drawable.ripple_semicircle_btn_gradual_bg_yellow);
                    } else {
                        btnSignInNow.setTextColor(ContextCompat.getColor(mActivity, R.color.txt_gray));
                        btnSignInNow.setBackgroundResource(R.drawable.shape_btn_semicircle_bg_disabled);
                    }
                    tvGoldCoinCount.setText(setNumColor(mActivity, signInEntity.getType_name()));
                    tvSignInCount.setText(setNumColor(mActivity, signInEntity.getContent()));

                    //列表任务
                    List<MissionSection2> list = new ArrayList<>();
                    List<MissionGroupEntity> missionGroups = welfarePageEntity.getList();
                    for (int i = 0; i < missionGroups.size(); i++) {
                        MissionGroupEntity group = missionGroups.get(i);
                        List<MissionEntity> welfares = group.getWelfare();
                        if (welfares == null) {
                            continue;
                        }
                        list.add(new MissionSection2(true, group.getTitle()));
                        for (int j = 0; j < welfares.size(); j++) {
                            MissionEntity missionEntity = welfares.get(j);
                            if (missionEntity.getSign_successions() > 0 && missionEntity.getSign_successions() <= 7) {
                                list.add(new MissionSection2(false, MissionSection2.TYPE_MISSION_7DAY, missionEntity));
                            } else {
                                list.add(new MissionSection2(false, MissionSection2.TYPE_MISSION_NOR, missionEntity));
                            }
                        }
                    }
                    mAdapter.setVipMode(UserInfoCache.getIsVip(mActivity));
                    mAdapter.setList(list);

                    footerRule.setVisibility(View.VISIBLE);
                });
       /* OkGo.<String>get(Consts.WELFARE_LIST_API)
                .tag(Consts.WELFARE_LIST_API)
                .execute(new LtbCallback((AppCompatActivity) mActivity, showDialog) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<WelfarePageEntity> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<WelfarePageEntity>>() {
                                });
                        if (entity.error_code == 0) {
                            WelfarePageEntity welfarePageEntity = entity.getData();

                            //banner
                            bannerDatas = welfarePageEntity.getBanner();
                            if (bannerDatas != null) {
                                mBanner.update(bannerDatas);
                            }

                            //普通签到
                            SignInMissionEntity signInEntity = welfarePageEntity.getSign_in();
                            int signInFlag = signInEntity.getStatus();
                            btnSignInNow.setText(signInEntity.getProgress());
                            if (signInFlag == 1) {
                                btnSignInNow.setTextColor(ContextCompat.getColor(mActivity, R.color.txt_white));
                                btnSignInNow.setBackgroundResource(R.drawable.ripple_semicircle_btn_gradual_bg_yellow);
                            } else {
                                btnSignInNow.setTextColor(ContextCompat.getColor(mActivity, R.color.txt_gray));
                                btnSignInNow.setBackgroundResource(R.drawable.shape_btn_semicircle_bg_disabled);
                            }
                            tvGoldCoinCount.setText(setNumColor(mActivity, signInEntity.getType_name()));
                            tvSignInCount.setText(setNumColor(mActivity, signInEntity.getContent()));

                            //列表任务
                            List<MissionSection2> list = new ArrayList<>();
                            List<MissionGroupEntity> missionGroups = welfarePageEntity.getList();
                            for (int i = 0; i < missionGroups.size(); i++) {
                                MissionGroupEntity group = missionGroups.get(i);
                                List<MissionEntity> welfares = group.getWelfare();
                                if (welfares == null) {
                                    continue;
                                }
                                list.add(new MissionSection2(true, group.getTitle()));
                                for (int j = 0; j < welfares.size(); j++) {
                                    MissionEntity missionEntity = welfares.get(j);
                                    if (missionEntity.getSign_successions() > 0 && missionEntity.getSign_successions() <= 7) {
                                        list.add(new MissionSection2(false, MissionSection2.TYPE_MISSION_7DAY, missionEntity));
                                    } else {
                                        list.add(new MissionSection2(false, MissionSection2.TYPE_MISSION_NOR, missionEntity));
                                    }
                                }
                            }
                            mAdapter.setVipMode(UserInfoCache.getIsVip(mActivity));
                            mAdapter.setList(list);

                            footerRule.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        mRefreshLayout.finishRefresh();
                    }
                });*/
    }


    /**
     * 完成任务领取奖励
     */
    private void reqMissionComplete(String id) {
        /*OkGo.<String>get(Consts.WELFARE_COMPLETE_API)
                .params(Consts.MISSION_ID, id)
                .execute(new LtbCallback((AppCompatActivity) mActivity) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<String> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<String>>() {
                                });
                        if (entity.error_code == 0) {
                            //刷新任务列表
                            reqGetWerfareTasks(false);
                            //刷新我的资产
                            ((MainActivity) mActivity).reqMyCapitalDetail();
                            TipDialog.show((AppCompatActivity) mActivity, entity.msg, TipDialog.TYPE.SUCCESS);
                        } else {
                            TipDialog.show((AppCompatActivity) mActivity, entity.msg, TipDialog.TYPE.ERROR);
                        }
                    }
                });*/
        RxHttp.postForm(Consts.WELFARE_COMPLETE_API)
                .addHeader(Consts.TOKEN, TokenCache.getToken(mActivity))
                .add(Consts.MISSION_ID, id)
                .asResponse(String.class)
                .doOnSubscribe(disposable -> WaitDialog.show((AppCompatActivity) mActivity, R.string.loading))
                .to(RxLife.toMain(this))  //感知生命周期，并在主线程回调
                .subscribe(s -> {
                    //刷新任务列表
                    reqGetWerfareTasks();
                    //刷新我的资产
                    ((MainActivity) mActivity).reqMyCapitalDetail();
                    TipDialog.show((AppCompatActivity) mActivity, "奖励领取成功", TipDialog.TYPE.SUCCESS);
                }, (OnError) error -> TipDialog.show((AppCompatActivity) mActivity, error.getErrorMsg(), TipDialog.TYPE.ERROR));
    }

    /**
     * 上报激励视频任务验证状况
     */
    private void reportStimulateMission(String url) {
        RxHttp.postForm(url)
                .addHeader(Consts.TOKEN, TokenCache.getToken(mActivity))
                .asResponse(String.class)
                .subscribe(s -> {
                }, (OnError) ErrorInfo::show);
    }


    //字符串中的数字变色
    private static SpannableStringBuilder setNumColor(Context context, String str) {
        SpannableStringBuilder style = new SpannableStringBuilder(str);
        for (int i = 0; i < str.length(); i++) {
            char a = str.charAt(i);
            if ((a >= '0' && a <= '9') || (a == '+')) {
                style.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.txt_red)), i, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return style;
    }
}
