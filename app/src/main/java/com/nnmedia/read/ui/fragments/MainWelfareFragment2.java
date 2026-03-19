package com.nnmedia.read.ui.fragments;

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

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.nnmedia.novel.R;
import com.nnmedia.novel.RxHttp;
import com.nnmedia.read.GlideApp;
import com.nnmedia.read.cache.TokenCache;
import com.nnmedia.read.cache.UserInfoCache;
import com.nnmedia.read.config.TogetherAdConst;
import com.nnmedia.read.contact.Common;
import com.nnmedia.read.contact.Consts;
import com.nnmedia.read.ebsevent.WelfareChangeEvent;
import com.nnmedia.read.entity.BannerADEntity;
import com.nnmedia.read.entity.CapitalEntity;
import com.nnmedia.read.entity.FUser;
import com.nnmedia.read.entity.MissionEntity;
import com.nnmedia.read.entity.MissionGroupEntity;
import com.nnmedia.read.entity.SignInMissionEntity;
import com.nnmedia.read.entity.WelfarePageEntity;
import com.nnmedia.read.entity.sections.MissionSection2;
import com.nnmedia.read.rxhttp.ErrorInfo;
import com.nnmedia.read.rxhttp.OnError;
import com.nnmedia.read.ui.activities.AdvFreeSuccessActivity;
import com.nnmedia.read.ui.activities.CommonWebActivity;
import com.nnmedia.read.ui.activities.LoginActivity2;
import com.nnmedia.read.ui.activities.MainActivity;
import com.nnmedia.read.ui.activities.MyGoldCoinActivity;
import com.nnmedia.read.ui.activities.SignInActivity;
import com.nnmedia.read.ui.adapters.WelfareMissionAdapter2;
import com.nnmedia.read.ui.base.BaseFragment;
import com.nnmedia.read.utils.ClickJumpUtil;
import com.nnmedia.read.utils.DensityUtils;
import com.nnmedia.read.utils.StatusBarUtils;
import com.rxjava.rxlife.RxLife;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.CircleIndicator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainWelfareFragment2 extends BaseFragment implements View.OnClickListener, OnRefreshListener, OnItemChildClickListener {

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
                            LoginActivity2.start(mActivity);
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
    }

    @Override
    public void onClick(View view) {
        if (onMoreClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.btn_click_2_login:
                LoginActivity2.start(mActivity);
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
            mBanner.destroy();
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

        mBanner.setAdapter(new BannerImageAdapter<BannerADEntity>(bannerDatas) {

            @Override
            public void onBindView(BannerImageHolder holder, BannerADEntity data, int position, int size) {
                if (data instanceof BannerADEntity) {
                    BannerADEntity adEntity = (BannerADEntity) data;
                    //Glide 加载图片简单用法
                    GlideApp.with(holder.itemView)
                            .load(adEntity.getImageText())
                            .transform(new CenterCrop(), new RoundedCorners(DensityUtils.dp2px(mActivity, 8)))
//                    .placeholder(R.mipmap.banner_place_holder)
                            .error(R.mipmap.banner_place_holder)
                            .into(holder.imageView);
                }
            }
        }).setOnBannerListener((data, position) -> {
            if (bannerDatas != null && bannerDatas.size() > position) {
                BannerADEntity entity = bannerDatas.get(position);
                ClickJumpUtil.handleJump(mActivity, entity.getLink(), entity.getJump());
            }
        })
                .addBannerLifecycleObserver(this)
                .setIndicator(new CircleIndicator(mActivity));

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
                        mBanner.setDatas(bannerDatas);
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
    }


    /**
     * 完成任务领取奖励
     */
    private void reqMissionComplete(String id) {
        RxHttp.postForm(Consts.WELFARE_COMPLETE_API)
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
