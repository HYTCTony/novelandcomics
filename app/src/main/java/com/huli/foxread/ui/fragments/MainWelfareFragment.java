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
import android.text.style.TypefaceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.huli.foxread.R;
import com.huli.foxread.cache.UserInfoCache;
import com.huli.foxread.callbacks.ookkggoo.LtbCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Common;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.engines.GlideImageLoaderWf;
import com.huli.foxread.entity.BannerADEntity;
import com.huli.foxread.entity.CapitalEntity;
import com.huli.foxread.entity.FUser;
import com.huli.foxread.entity.MissionEntity;
import com.huli.foxread.entity.MissionGroupEntity;
import com.huli.foxread.entity.SignInMissionEntity;
import com.huli.foxread.entity.WelfarePageEntity;
import com.huli.foxread.entity.sections.MissionSection;
import com.huli.foxread.ui.activities.CommonWebActivity;
import com.huli.foxread.ui.activities.LoginActivity;
import com.huli.foxread.ui.activities.MainActivity;
import com.huli.foxread.ui.activities.MyGoldCoinActivity;
import com.huli.foxread.ui.activities.SignInActivity;
import com.huli.foxread.ui.adapters.WelfareMissionAdapter;
import com.huli.foxread.ui.base.BaseFragment;
import com.huli.foxread.utils.ClickJumpUtil;
import com.huli.foxread.utils.StatusBarUtils;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainWelfareFragment extends BaseFragment implements OnBannerListener, View.OnClickListener, OnRefreshListener {

    private SmartRefreshLayout mRefreshLayout;
    private TextView btnClick2Login;
    private LinearLayout btnGoldCoinUsable;
    private TextView tvGoldCoin;            //金币余额

    private RecyclerView recyclerView;
    private WelfareMissionAdapter mAdapter;

    private View headViewTop;
    private Banner mBanner;
    private List<BannerADEntity> bannerDatas;
    private TextView tvGoldCoinCount, tvSignInCount;
    private TextView btnSignInNow;

    private View footerRule;

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
        mAdapter = new WelfareMissionAdapter();
        recyclerView.setAdapter(mAdapter);
        initTopLayout();

        initFooterView();
    }

    @Override
    public void setListener() {
        btnClick2Login.setOnClickListener(this);
        btnGoldCoinUsable.setOnClickListener(this);
        btnSignInNow.setOnClickListener(this);
        mRefreshLayout.setOnRefreshListener(this);

        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.btn_welfare_mission_action) {
                MissionSection<MissionEntity> data = mAdapter.getData().get(position);
                MissionEntity missionEntity = data.getObject();
                if (missionEntity.getStatus() == 0) {
                    String mlink = missionEntity.getLink();
                    if (mlink.equals(Common.SIGNIN_NEWBIE) || mlink.equals(Common.SWITCH2_WELFARE)) {
                        if (missionEntity.getNeed_login() == 1) {
                            LoginActivity.start(mActivity);
                            return;
                        }
                    }
                    ClickJumpUtil.handleJump(mActivity, mlink, missionEntity.getJump());
                } else if (missionEntity.getStatus() == 1) {
                    reqMissionComplete(missionEntity.getId());
                }
            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {
        EventBus.getDefault().register(this);

        boolean isTourist = UserInfoCache.getIsTourist(mActivity);
        displayIsLoginUI(isTourist);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

        OkGo.getInstance().cancelTag(Consts.WELFARE_LIST_API);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onUserInfoChangeEvent(FUser event) {
        boolean isTourist = event.isIs_tourist();
        displayIsLoginUI(isTourist);
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
        reqGetWerfareTasks(false);

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
        mBanner.setDelayTime(4200);
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
                            List<MissionSection<MissionEntity>> list = new ArrayList<>();
                            List<MissionGroupEntity> missionGroups = welfarePageEntity.getList();
                            for (int i = 0; i < missionGroups.size(); i++) {
                                MissionGroupEntity group = missionGroups.get(i);
                                List<MissionEntity> welfares = group.getWelfare();
                                if (welfares != null) {
                                    list.add(new MissionSection<>(group.getTitle(), null));
                                    for (int j = 0; j < welfares.size(); j++) {
                                        list.add(new MissionSection<>(group.getTitle(), welfares.get(j)));
                                    }
                                }
                            }
                            mAdapter.setVipMode(UserInfoCache.getIsVip(mActivity));
                            mAdapter.setNewInstance(list);

                            footerRule.setVisibility(View.VISIBLE);
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
    private void reqMissionComplete(String id) {
        OkGo.<String>get(Consts.WELFARE_COMPLETE_API)
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
                });
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
