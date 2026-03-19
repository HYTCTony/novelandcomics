package com.nnmedia.read.ui.fragments;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.kongzue.dialog.v3.CustomDialog;
import com.kongzue.dialog.v3.MessageDialog;
import com.kongzue.dialog.v3.TipDialog;
import com.nnmedia.novel.R;
import com.nnmedia.novel.RxHttp;
import com.nnmedia.page.utils.SpanUtils;
import com.nnmedia.read.GlideApp;
import com.nnmedia.read.cache.AccountCache;
import com.nnmedia.read.cache.UserInfoCache;
import com.nnmedia.read.contact.Common;
import com.nnmedia.read.contact.Consts;
import com.nnmedia.read.ebsevent.UnReadMsgEvent;
import com.nnmedia.read.ebsevent.VipChargerEvent;
import com.nnmedia.read.entity.AdEntity;
import com.nnmedia.read.entity.CapitalEntity;
import com.nnmedia.read.entity.FUser;
import com.nnmedia.read.entity.MineWelfareZoneEntity;
import com.nnmedia.read.handlers.EncodingHandler;
import com.nnmedia.read.rxhttp.OnError;
import com.nnmedia.read.ui.activities.CDKEYActivity;
import com.nnmedia.read.ui.activities.CommonWebActivity;
import com.nnmedia.read.ui.activities.FreeGetVipActivity;
import com.nnmedia.read.ui.activities.GoldExchangeVipActivity;
import com.nnmedia.read.ui.activities.HelpAndFeedbackActivity;
import com.nnmedia.read.ui.activities.LoginActivity2;
import com.nnmedia.read.ui.activities.MainActivity;
import com.nnmedia.read.ui.activities.MsgNotifyActivity;
import com.nnmedia.read.ui.activities.MyGoldCoinActivity;
import com.nnmedia.read.ui.activities.MyHPointActivity;
import com.nnmedia.read.ui.activities.MyPointActivity;
import com.nnmedia.read.ui.activities.ReadingPreferenceActivity;
import com.nnmedia.read.ui.activities.ReadingRecordActivity;
import com.nnmedia.read.ui.activities.ScanCodeActivity;
import com.nnmedia.read.ui.activities.SettingActivity;
import com.nnmedia.read.ui.activities.ShareActivity;
import com.nnmedia.read.ui.activities.UserBasicInfoActivity;
import com.nnmedia.read.ui.adapters.WelfareZoneMineAdapter;
import com.nnmedia.read.ui.base.BaseFragment;
import com.nnmedia.read.ui.decoration.HorizontalItemDecoration;
import com.nnmedia.read.utils.ClickJumpUtil;
import com.nnmedia.read.utils.DateTimeUtil;
import com.nnmedia.read.utils.DensityUtils;
import com.nnmedia.read.utils.GlideUtil;
import com.nnmedia.read.utils.SPFUtils;
import com.nnmedia.read.utils.StatusBarUtils;
import com.nnmedia.read.utils.VerifyDevice;
import com.rxjava.rxlife.RxLife;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerAdapter;
import com.youth.banner.indicator.CircleIndicator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.bingoogolapple.badgeview.BGABadgeTextView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;
import rxhttp.wrapper.cahce.CacheMode;


public class MainMineFragment extends BaseFragment implements View.OnClickListener, OnItemClickListener, EasyPermissions.PermissionCallbacks {

    private View layoutLogged, layoutNotLogin;
    private Button btnLogin;

    private ImageView ivUserHeadImg;
    private ImageView ivHeadImgLmVipSign;       //终身会员标志
    private TextView tvNickname, tvUserId;
    private TextView tvMyGoldCoin, tvTodayGoldCoin, tvTodayReadingTime;

    private ConstraintLayout ctlVipCard;
    private TextView tvHuliVip, tvVipAdvantage;
    private TextView btnOpenVip;
    private LinearLayout btnSignIn;

    private RelativeLayout rvAdView;

    private BGABadgeTextView bgabadge;//未读消息

    private RecyclerView rvWelfareZone;
    private WelfareZoneMineAdapter wzAdapter;

    private ImageView btnScan;
    private ImageView btnQrcode;

    private Bitmap bmpShare;

    private RelativeLayout rlAdView;
    private ImageView ivAdView;

    @Override
    public int bindLayout() {
        return R.layout.fragment_main_mine;
    }

    @Override
    public void setStatusBar(View view) {
        StatusBarUtils.offsetView(mActivity, $(view, R.id.ctl_top_bar_mine));
        StatusBarUtils.setAndroidNativeLightStatusBar(mActivity, true);
    }

    @Override
    public void initView(View view) {

        layoutNotLogin = $(view, R.id.ll_not_login_show_mine);
        layoutLogged = $(view, R.id.ctl_logged_show_mine);

        initRecyWelfareZone(view);

        ivUserHeadImg = $(view, R.id.iv_user_headImg);
        ivHeadImgLmVipSign = $(view, R.id.iv_user_headImg_life_member_vip_sign);
        tvNickname = $(view, R.id.tv_user_nickname);
        tvUserId = $(view, R.id.tv_user_id);
        $(view, R.id.ll_my_gold_coin_mine).setOnClickListener(this);
        $(view, R.id.ll_today_gold_coin_mine).setOnClickListener(this);
        $(view, R.id.ll_today_reading_count_mine).setOnClickListener(this);
        tvMyGoldCoin = $(view, R.id.tv_my_gold_coin_mine);
        tvTodayGoldCoin = $(view, R.id.tv_today_gold_coin_mine);
        tvTodayReadingTime = $(view, R.id.tv_today_reading_time_mine);

        btnLogin = $(view, R.id.btn_login_mine);
        ctlVipCard = $(view, R.id.ctl_content_vip_card);
        tvHuliVip = $(view, R.id.tv_huli_vip_member);
        tvVipAdvantage = $(view, R.id.tv_huli_vip_advantage_tip);
        btnOpenVip = $(view, R.id.tv_asBtn_open_membership_account);
        btnSignIn = $(view, R.id.ll_asBtn_sign_in_4_gold);

        btnSignIn.setOnClickListener(this);
        $(view, R.id.rtl_asBtn_my_privilege).setOnClickListener(this);
        $(view, R.id.rtl_asBtn_msg_notify).setOnClickListener(this);
        $(view, R.id.rtl_asBtn_reading_preference).setOnClickListener(this);
        $(view, R.id.rtl_asBtn_reading_record).setOnClickListener(this);
        $(view, R.id.rtl_asBtn_invite_friends).setOnClickListener(this);
        $(view, R.id.rtl_asBtn_business_cooperation).setOnClickListener(this);
        $(view, R.id.rtl_asBtn_send_email).setOnClickListener(this);
        $(view, R.id.rtl_asBtn_fans_base).setOnClickListener(this);
        $(view, R.id.rtl_asBtn_cash_withdrawal).setOnClickListener(this);
        $(view, R.id.rtl_asBtn_mode_adolescent).setOnClickListener(this);
        $(view, R.id.rtl_asBtn_help_and_feedback).setOnClickListener(this);
        $(view, R.id.iv_asBtn_setting_mine).setOnClickListener(this);
        bgabadge = $(view, R.id.bgabadge_msg_count);

        btnScan = $(view, R.id.iv_asBtn_scan_code);
        btnQrcode = $(view, R.id.iv_asBtn_qr_code);

        FUser userInfo = UserInfoCache.getUserInfo(mActivity);
        changeUIbyUserInfo(userInfo);
//        if (!UserInfoCache.getIsTourist(mActivity)) {
//            if ((boolean) SPFUtils.get(mActivity, Common.KEY_FIRST_LOGIN, false)) {
//                showAccountMsgDialog();
//            }
//        }
        rvAdView = $(view, R.id.rv_ad_layout);
        rlAdView = $(view, R.id.rl_ad_view);
        ivAdView = $(view, R.id.iv_ad_view);
        $(view, R.id.iv_ad_view).setOnClickListener(this);

        initBannerView(view);
    }

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
            mBanner.stop();
        }
    }

    class myBannerAdapter extends BannerAdapter<AdEntity, myBannerAdapter.BannerViewHolder> {

        public myBannerAdapter(List<AdEntity> mDatas) {
            super(mDatas);
        }

        @Override
        public BannerViewHolder onCreateHolder(ViewGroup parent, int viewType) {
            return new BannerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.banner_layout, parent, false));
        }

        @Override
        public void onBindView(BannerViewHolder holder, AdEntity data, int position, int size) {
            //Glide 加载图片简单用法
            GlideApp.with(holder.itemView)
                    .load(data.getImageText())
                    .placeholder(R.mipmap.banner_place_holder)
                    .error(R.mipmap.banner_place_holder)
                    .transform(new CircleCrop())
                    .into(holder.imageView);
            String title = data.getName() + "，" + data.getIntroduce();
            holder.title.setText(title);
        }

        class BannerViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView title;

            public BannerViewHolder(@NonNull View view) {
                super(view);
                imageView = view.findViewById(R.id.image);
                title = view.findViewById(R.id.bannerTitle);
            }
        }
    }

    private Banner mBanner;

    /**
     * 轮播广告
     */
    private void initBannerView(View rootView) {
        mBanner = $(rootView, R.id.banner_boy_girl_top);
        mBanner.setAdapter(new myBannerAdapter(adBannerData));
        mBanner.setOnBannerListener((data, position) -> {
            AdEntity entity = (AdEntity) data;
            if (entity.getJump() == 2) {
                jumpInSide(entity.getLink());
            } else {
                jumpOutSide(entity.getLink());
            }
        });
        mBanner.addBannerLifecycleObserver(this);
        mBanner.setIndicator(new CircleIndicator(mActivity));
    }

    List<AdEntity> adBannerData = new ArrayList<>();
    private int adPos = 0;
    private String link = "";
    private int jump;
    private boolean isFirstRequest = true;

    private void reqBannerAd() {
//        if (testingIsABC()) {
//            isFirstRequest = true;
//            return;
//        }

        if (adBannerData.isEmpty()) {
            RxHttp.postForm(Consts.AD_BANNER_USER_URL)
                    .asResponseList(AdEntity.class)
                    .to(RxLife.toMain(this))
                    .subscribe(ad -> {
                        mBanner.setDatas(ad);
//                        rlAdView.setVisibility(View.VISIBLE);
                        rvAdView.setVisibility(View.VISIBLE);
                        ivAdView.setVisibility(View.VISIBLE);
                        adBannerData.clear();
                        adBannerData.addAll(ad);
                        String url = adBannerData.get(0).getImageText();
                        link = adBannerData.get(0).getLink();
                        jump = adBannerData.get(0).getJump();
                        GlideUtil.loadRoundRect(mActivity, ivAdView, url, -1);
                        isFirstRequest = false;
                    }, (OnError) error -> {
//                        rlAdView.setVisibility(View.GONE);
                        rvAdView.setVisibility(View.GONE);
                        isFirstRequest = true;
                    });
        } else {
            if (adPos >= adBannerData.size())
                adPos = 0;
            link = adBannerData.get(adPos).getLink();
            jump = adBannerData.get(adPos).getJump();
            String url = adBannerData.get(adPos).getImageText();
            GlideUtil.loadRoundRect(mActivity, ivAdView, url, -1);
            adPos++;
        }
    }

    private boolean testingIsABC() {
//        return false;
        if (UserInfoCache.getSuperVip(mActivity) == 1) {
            return true;
        }
        if (UserInfoCache.getIsVip(mActivity)) {
            return true;
        }
//        if (UserInfoCache.getIsNewMan(mContext)) {
//            return true;
//        }
        return false;
    }

    private CustomDialog loadingDialog;

    /**
     * 签到
     */
    private void reqSignIn() {
        btnSignIn.setEnabled(false);
        RxHttp.postForm(Consts.WELFARE_COMPLETESINGIN_API)
                .asResponse(Integer.class)
                .doOnSubscribe(disposable -> {
                    showLoadingDialog();
                })
                .doFinally(() -> {
                    dismissLoadingDialog();
                    btnSignIn.setEnabled(true);
                })
                .to(RxLife.toMain(this))  //感知生命周期，并在主线程回调
                .subscribe(i -> {
                    int getGb = i;       //签到的奖励
                    //弹窗提示签到成功
                    CustomDialog.build((AppCompatActivity) mActivity, R.layout.layout_custom_dialog_sign_in_success, (dialog, v) -> {
                        TextView tvGetGold = v.findViewById(R.id.iv_get_gold_coin_count);
                        tvGetGold.setText(String.format(getString(R.string.txt_get_goldcoin_x), getGb));
                        v.findViewById(R.id.iv_asBtn_close).setOnClickListener(view1 -> dialog.doDismiss());
                        v.findViewById(R.id.btn_i_see).setOnClickListener(view12 -> dialog.doDismiss());
                    }).show();
                    reqMyCapitalDetail();
                }, (OnError) error -> {
                    CustomDialog.build((AppCompatActivity) mActivity, R.layout.layout_custom_dialog_sign_in_success, (dialog, v) -> {
                        TextView tvGetGold = v.findViewById(R.id.iv_get_gold_coin_count);
                        tvGetGold.setText("已签到成功，请勿重复签到。");
                        v.findViewById(R.id.iv_asBtn_close).setOnClickListener(view1 -> dialog.doDismiss());
                        v.findViewById(R.id.btn_i_see).setOnClickListener(view12 -> dialog.doDismiss());
                    }).show();
                });
    }

    /**
     * 我的资金详情
     */
    public void reqMyCapitalDetail() {
        RxHttp.postForm(Consts.USER_CAPITAL_API)
                .asResponse(CapitalEntity.class)
                .to(RxLife.toMain(this))  //感知生命周期，并在主线程回调
                .subscribe(capitalEntity -> {
                    EventBus.getDefault().postSticky(capitalEntity);
                });
    }

    @Override
    public void setListener() {
        btnLogin.setOnClickListener(this);
        btnOpenVip.setOnClickListener(this);
        ivUserHeadImg.setOnClickListener(this);
        btnScan.setOnClickListener(this);
        btnQrcode.setOnClickListener(this);
//        wzAdapter.setOnItemClickListener(this);

        bgabadge.setDragDismissDelegate(badge -> {
            reqSetMsgAllRead();
            ((MainActivity) mActivity).mTabLayout.hideMsg(4);
        });
    }

    @Override
    public void doBusiness(Context mContext) {
        EventBus.getDefault().register(this);

        FUser userInfo = UserInfoCache.getUserInfo(mActivity);
        changeUIbyUserInfo(userInfo);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isVisible()) {
//            reqMineWelfareZone();
//            getUserReadTime();
        }
        reqBannerAd();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            StatusBarUtils.setStatusBarTextDark(mActivity, true);

//            reqMineWelfareZone();
//            getUserReadTime();
        }
    }


    @Override
    public void onItemClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
        MineWelfareZoneEntity data = wzAdapter.getData().get(position);
        ClickJumpUtil.handleJump(mActivity, data.getLink(), data.getJump());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mBanner != null)
            mBanner.destroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onUserInfoChangeEvent(FUser event) {
        changeUIbyUserInfo(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onVipChargerEvent(VipChargerEvent event) {
        changeUIbyIsVip(UserInfoCache.getSuperVip(mActivity), event.isBecomingVip());
        EventBus.getDefault().removeStickyEvent(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onCapitalRefreshEvent(CapitalEntity event) {
        tvMyGoldCoin.setText(String.valueOf(event.getScore()));
        tvTodayGoldCoin.setText(String.valueOf(event.getH_point()));
        tvTodayReadingTime.setText(String.valueOf(event.getPoint()));
    }

    /**
     * 未读消息
     */
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onUnReadMsgEvent(UnReadMsgEvent event) {
        if (event.getMessage() > 0) {
            bgabadge.showTextBadge("" + event.getMessage());
        } else {
            bgabadge.hiddenBadge();
        }
        EventBus.getDefault().removeStickyEvent(event);
    }

    /**
     * 用户信息改变的时候改变UI
     *
     * @param fUser
     */
    private void changeUIbyUserInfo(FUser fUser) {
        boolean isVisitor = fUser.isIs_tourist();
        if (isVisitor) {
            layoutNotLogin.setVisibility(View.VISIBLE);
            layoutLogged.setVisibility(View.GONE);
        } else {
            layoutNotLogin.setVisibility(View.GONE);
            layoutLogged.setVisibility(View.VISIBLE);
            displayUserInfo(fUser);
        }
        //VIP（vip用户登出也要改变ui）
        changeUIbyIsVip(fUser.getSuper_vip(), fUser.isIs_vip());
    }

    /**
     * 不同身份用户对应不同UI
     *
     * @param lifeMember 终身会员
     * @param isVip      是不是VIP
     */
    private void changeUIbyIsVip(int lifeMember, boolean isVip) {
        if (lifeMember == 1) {  //终身VIP
            //用户昵称的
            Drawable drawableR = ContextCompat.getDrawable(mActivity, R.drawable.ic_yellow_diamond_18dp);
            if (drawableR != null) {
                drawableR.setBounds(0, 0, drawableR.getMinimumWidth(), drawableR.getMinimumHeight());
                tvNickname.setCompoundDrawables(null, null, drawableR, null);
                tvNickname.setCompoundDrawablePadding(DensityUtils.dp2px(mActivity, 8));
            }

            ctlVipCard.setBackgroundResource(R.drawable.bg_mine_life_member_vip_card);
            tvHuliVip.setText(R.string.txt_you_have_become_a_life_member_vip);
            tvHuliVip.setTextColor(ContextCompat.getColor(mActivity, R.color.txt_col_life_member));
            ivHeadImgLmVipSign.setVisibility(View.VISIBLE);
            //会员card的
            Drawable drawableL = ContextCompat.getDrawable(mActivity, R.mipmap.icon_vip_symbol);
            if (drawableL != null) {
                drawableL.setBounds(0, 0, drawableL.getMinimumWidth(), drawableL.getMinimumHeight());
                tvHuliVip.setCompoundDrawables(drawableL, null, null, null);
            }
            tvVipAdvantage.setVisibility(View.GONE);
            btnOpenVip.setText(R.string.txt_get_rights_and_interests);
//            btnOpenVip.setVisibility(View.GONE);
        } else {    //非终身VIP
            ctlVipCard.setBackgroundResource(R.drawable.bg_mine_vip_card);
            tvHuliVip.setTextColor(ContextCompat.getColor(mActivity, R.color.txt_white));
            tvNickname.setCompoundDrawables(null, null, null, null);
            ivHeadImgLmVipSign.setVisibility(View.GONE);
            btnOpenVip.setVisibility(View.VISIBLE);
            if (isVip) {        //普通VIP
                tvHuliVip.setText(R.string.txt_you_have_become_a_vip);
                //会员card的
                Drawable drawable = ContextCompat.getDrawable(mActivity, R.mipmap.icon_vip_symbol);
                if (drawable != null) {
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    tvHuliVip.setCompoundDrawables(drawable, null, null, null);
                }
                tvVipAdvantage.setVisibility(View.GONE);
                btnOpenVip.setText(R.string.txt_view_details);
            } else {            //非VIP
                tvHuliVip.setText(null);
                tvHuliVip.setCompoundDrawables(null, null, null, null);
                tvVipAdvantage.setVisibility(View.VISIBLE);
//                if (UserInfoCache.isGetFree(mActivity) && !VerifyDevice.verify())
//                    btnOpenVip.setText(R.string.txt_get_free_vip);
//                else
                btnOpenVip.setText(R.string.txt_activate_immediately);
            }
        }
    }

    /**
     * 显示用户信息
     */
    private void displayUserInfo(FUser fUser) {
        GlideUtil.loadCircle(mActivity, ivUserHeadImg, fUser.getHttp_avatar());
        tvNickname.setText(fUser.getUsername());
        if (fUser.getSuper_vip() == 1) {
            SpannableStringBuilder vipTag = new SpanUtils(mActivity)
                    .append(" 终身VIP ")
                    .setForegroundColor(ContextCompat.getColor(mActivity, R.color.white))
                    .setBackgroundColor(ContextCompat.getColor(mActivity, R.color.txt_dark_gold))
                    .create();
            tvUserId.setText(vipTag);
            return;
        }
        if (fUser.isIs_vip()) {
            SpannableStringBuilder vipTag = new SpanUtils(mActivity)
                    .append(" VIP会员 ")
                    .setForegroundColor(ContextCompat.getColor(mActivity, R.color.white))
                    .setBackgroundColor(ContextCompat.getColor(mActivity, R.color.col_red))
                    .appendLine(" " + String.format(getString(R.string.txt_vip_end_time_colon), DateTimeUtil.formatDateTime(fUser.getVip_end() * 1000,
                            DateTimeUtil.DF_YYYY_MM_DD)))
                    .create();
            tvUserId.setText(vipTag);
        } else {
            SpannableStringBuilder vipTag = new SpanUtils(mActivity)
                    .append(" 普通会员 ")
                    .setBackgroundColor(ContextCompat.getColor(mActivity, R.color.light_translucent))
                    .create();
            tvUserId.setText(vipTag);
        }
//        tvUserId.setText((getString(R.string.txt_id_colon) + fUser.getId()));
//        tvMyGoldCoin.setText(String.valueOf(UserInfoCache.getScore(mContext)));
//        tvTodayGoldCoin.setText(String.valueOf(UserInfoCache.getTodayScore(mContext)));
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_asBtn_sign_in_4_gold:          //签到
                if (UserInfoCache.getIsTourist(mActivity)) {
                    LoginActivity2.start(mActivity);
                } else {
                    reqSignIn();
                }
                break;
            case R.id.iv_asBtn_setting_mine:
                if (onMoreClick()) {
                    return;
                }
                startActivity(new Intent(mActivity, SettingActivity.class));
                break;
            case R.id.btn_login_mine:
                LoginActivity2.start(mActivity);
                break;
            case R.id.iv_user_headImg:
                startActivity(new Intent(mActivity, UserBasicInfoActivity.class));
                break;
            case R.id.ll_my_gold_coin_mine:
                startActivity(new Intent(mActivity, MyGoldCoinActivity.class));
                break;
            case R.id.ll_today_gold_coin_mine:
                startActivity(new Intent(mActivity, MyHPointActivity.class));
                break;
            case R.id.ll_today_reading_count_mine:
                startActivity(new Intent(mActivity, MyPointActivity.class));
                break;
            case R.id.rtl_asBtn_my_privilege:                   //go2 VIP页面
            case R.id.tv_asBtn_open_membership_account:         //go2 VIP页面
//                if (UserInfoCache.isGetFree(mActivity))
//                    FreeGetVipActivity.start(mActivity);
//                else
                    GoldExchangeVipActivity.start(mActivity, 0);
                break;
            case R.id.rtl_asBtn_msg_notify:
                startActivity(new Intent(mActivity, MsgNotifyActivity.class));
                break;
            case R.id.rtl_asBtn_reading_preference:
                startActivity(new Intent(mActivity, ReadingPreferenceActivity.class));
                break;
            case R.id.rtl_asBtn_reading_record:
                startActivity(new Intent(mActivity, ReadingRecordActivity.class));
                break;
            case R.id.rtl_asBtn_invite_friends:
                ShareActivity.start(mActivity);
                break;
            case R.id.rtl_asBtn_cash_withdrawal:
                CDKEYActivity.start(mActivity);
                break;
            case R.id.rtl_asBtn_business_cooperation:

                

//                MessageDialog.show((AppCompatActivity) mActivity, R.string.txt_telegram_title_1, R.string.txt_telegram_content_1, R.string.txt_jump,
//                        R.string.txt_copy)
//                        .setCancelable(true)
//                        .setOnCancelButtonClickListener((baseDialog, v) -> {
//                            ClipboardManager cm = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
//                            if (cm != null) {
//                                ClipData mClipData = ClipData.newPlainText("share", "@haitunxiaoshuo");
//                                cm.setPrimaryClip(mClipData);
//                                TipDialog.show((AppCompatActivity) mActivity, "拷贝成功", TipDialog.TYPE.SUCCESS);
//                            }
//                            baseDialog.doDismiss();
//                            return false;
//                        })
//                        .setOnOkButtonClickListener((baseDialog, v) -> {
//                            jumpInSide("https://t.me/haitunxiaoshuo");
//                            baseDialog.doDismiss();
//                            return false;
//                        });
                break;
            case R.id.rtl_asBtn_send_email:
                MessageDialog.show((AppCompatActivity) mActivity, R.string.txt_email_title_1, R.string.txt_email_content_1, R.string.txt_jump,
                        R.string.txt_copy)
                        .setCancelable(true)
                        .setOnCancelButtonClickListener((baseDialog, v) -> {
                            ClipboardManager cm = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
                            if (cm != null) {
                                ClipData mClipData = ClipData.newPlainText("share", "9906613@gmail.com");
                                cm.setPrimaryClip(mClipData);
                                TipDialog.show((AppCompatActivity) mActivity, "拷贝成功", TipDialog.TYPE.SUCCESS);
                            }
                            baseDialog.doDismiss();
                            baseDialog.doDismiss();
                            return false;
                        })
                        .setOnOkButtonClickListener((baseDialog, v) -> {
                            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                            emailIntent.setData(Uri.parse("mailto:9906613@gmail.com"));
                            startActivity(emailIntent);
                            baseDialog.doDismiss();
                            return false;
                        });
                break;
            case R.id.rtl_asBtn_fans_base:
                MessageDialog.show((AppCompatActivity) mActivity, R.string.txt_telegram_title_2, R.string.txt_telegram_content_2, R.string.txt_jump,
                        R.string.txt_copy)
                        .setCancelable(true)
                        .setOnCancelButtonClickListener((baseDialog, v) -> {
                            ClipboardManager cm = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
                            if (cm != null) {
                                ClipData mClipData = ClipData.newPlainText("share", "https://t.me/htcrxs");
                                cm.setPrimaryClip(mClipData);
                                TipDialog.show((AppCompatActivity) mActivity, "拷贝成功", TipDialog.TYPE.SUCCESS);
                            }
                            baseDialog.doDismiss();
                            baseDialog.doDismiss();
                            return false;
                        })
                        .setOnOkButtonClickListener((baseDialog, v) -> {
                            jumpInSide("https://t.me/htcrxs");
                            baseDialog.doDismiss();
                            return false;
                        });
                break;
            case R.id.rtl_asBtn_mode_adolescent:

                break;
            case R.id.rtl_asBtn_help_and_feedback:
                startActivity(new Intent(mActivity, HelpAndFeedbackActivity.class));
                break;
            case R.id.iv_asBtn_scan_code:
                requestCameraPermission();
                break;
            case R.id.iv_asBtn_qr_code:
                if (UserInfoCache.getIsTourist(mActivity)) {
                    LoginActivity2.start(mActivity);
                } else {
                    showAccountMsgDialog();
                }
                break;
            case R.id.iv_ad_view:
                if (jump == 2) {
                    jumpInSide(link);
                } else {
                    jumpOutSide(link);
                }
                break;
            default:
                break;
        }
    }

    private void jumpInSide(String link) {
        Intent intent = new Intent(mActivity, CommonWebActivity.class);
        intent.putExtra(Common.KEY_URL, link);
        startActivity(intent);
    }

    private void jumpOutSide(String link) {
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(link));
        // 注意此处的判断intent.resolveActivity()可以返回显示该Intent的Activity对应的组件名
        // 官方解释 : Name of the component implementing an activity that can display the intent
        if (intent.resolveActivity(mActivity.getPackageManager()) != null) {
            final ComponentName componentName = intent.resolveActivity(mActivity.getPackageManager());
            startActivity(Intent.createChooser(intent, "请选择浏览器"));
        } else {
            TipDialog.show((MainActivity) mActivity, "链接错误或无浏览器", TipDialog.TYPE.ERROR);
        }
    }

    private void initRecyWelfareZone(View view) {
        rvWelfareZone = $(view, R.id.recyclerView_welfare_zone);
        LinearLayoutManager llManager = new LinearLayoutManager(mActivity) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        llManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvWelfareZone.setLayoutManager(llManager);
        rvWelfareZone.addItemDecoration(new HorizontalItemDecoration(16, mActivity, true));
        wzAdapter = new WelfareZoneMineAdapter();
        rvWelfareZone.setAdapter(wzAdapter);
    }


    /**
     * 福利专区
     */
    private void reqMineWelfareZone() {
        //先获取缓存的
        RxHttp.postForm(Consts.WELFARE_USERLIST_API)
                .setCacheMode(CacheMode.ONLY_CACHE)
                .asResponseList(MineWelfareZoneEntity.class)
                .to(RxLife.toMain(this))  //感知生命周期，并在主线程回调
                .subscribe(result -> {
                    wzAdapter.setList(result);
                });

        //再获取网络的
        RxHttp.postForm(Consts.WELFARE_USERLIST_API)
                .setCacheMode(CacheMode.NETWORK_SUCCESS_WRITE_CACHE)
                .asResponseList(MineWelfareZoneEntity.class)
                .to(RxLife.toMain(this))  //感知生命周期，并在主线程回调
                .subscribe(result -> {
                    wzAdapter.setList(result);
                });
    }

    /**
     * 全部标记为已读
     */
    private void reqSetMsgAllRead() {
        RxHttp.postForm(Consts.MSG_SET_ALL_READ_API)
                .asResponse(String.class)
                .to(RxLife.toMain(this))  //感知生命周期，并在主线程回调
                .subscribe(s -> {

                });
    }

    /**
     * 获取用户阅读时间
     */
    private void getUserReadTime() {
        RxHttp.get(Consts.USER_READ_TIME_API)
                .asResponse(String.class)
                .to(RxLife.toMain(this))  //感知生命周期，并在主线程回调
                .subscribe(rTime -> {
                    tvTodayReadingTime.setText(rTime);
                });
    }

    private void showAccountMsgDialog() {
        CustomDialog.show((AppCompatActivity) mActivity, R.layout.layout_custom_dialog_remenber_account, (dialog, v) -> {
            v.findViewById(R.id.iv_asBtn_close).setOnClickListener(view1 -> dialog.doDismiss());
            ImageView ivQrCode = v.findViewById(R.id.iv_invite_qr_code);
            bmpShare = createQrCode(AccountCache.getAccout(mActivity));
            ivQrCode.setImageBitmap(bmpShare);
            ivQrCode.setOnLongClickListener(v1 -> {
                requestPhoneStatePermission();
                dialog.doDismiss();
                return false;
            });
        });
        SPFUtils.put(mActivity, Common.KEY_FIRST_LOGIN, false);
    }

    /**
     * 生成二维码
     */
    private Bitmap createQrCode(String inviteUrl) {
        Bitmap resource = BitmapFactory.decodeResource(getResources(), R.mipmap.app_logo);
        Bitmap logoBorder = getRoundedCornerBorderBitmap(mActivity, resource);
        return EncodingHandler.createQRImage(inviteUrl, logoBorder, 512);
    }

    /**
     * 圆角白边图片
     *
     * @param bitmap
     * @return
     */
    private static Bitmap getRoundedCornerBorderBitmap(Context context, Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int mBorderColor = Color.WHITE;
        float mBorderWidth = (float) DensityUtils.dp2px(context, 4);
        float mCornerRadius = (float) DensityUtils.dp2px(context, 2);

        Paint mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);

//        Matrix mMatrix = new Matrix();
        // 将bmp作为着色器，就是在指定区域内绘制bmp
        BitmapShader mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
//        float scale = 1.0f;

        // shader的变换矩阵，我们这里主要用于放大或者缩小
//        mMatrix.preScale(scale, scale);
//        mBitmapShader.setLocalMatrix(mMatrix);
//        // 设置变换矩阵
//        mBitmapShader.setLocalMatrix(mMatrix);
        // 设置shader
        mBitmapPaint.setShader(mBitmapShader);


        Paint mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(mBorderWidth);

        Bitmap output = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        RectF mRoundRect = new RectF(mBorderWidth / 2, mBorderWidth / 2, w - mBorderWidth / 2, h - mBorderWidth / 2);

        Path mRoundPath = new Path();
        mRoundPath.reset();
        mRoundPath.addRoundRect(mRoundRect,
                new float[]{mCornerRadius, mCornerRadius,
                        mCornerRadius, mCornerRadius,
                        mCornerRadius, mCornerRadius,
                        mCornerRadius, mCornerRadius},
                Path.Direction.CW);
        //绘制描边(其实是圆角矩形)
        canvas.drawPath(mRoundPath, mBorderPaint);

        canvas.drawPath(mRoundPath, mBitmapPaint);

        return output;
    }

    private void saveImageToLocal(Bitmap bitmap) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            TipDialog.show((AppCompatActivity) mActivity, "未插入sd卡", TipDialog.TYPE.SUCCESS);
            return;
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if (bitmap == null) return;
//        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
//                .getAbsolutePath();
        String path = mActivity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                .getAbsolutePath();
        String imageName = "Share-" + df.format(new Date().getTime()) + ".jpg";
        File imageFile = new File(path, imageName);

        try {
            FileOutputStream out = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            bitmap.recycle();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 下面的步骤必须有，不然在相册里找不到图片，若不需要让用户知道你保存了图片，可以不写下面的代码。
        // 把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(mActivity.getContentResolver(),
                    imageFile.getAbsolutePath(), imageName, null);
            TipDialog.show((AppCompatActivity) mActivity, "保存成功，请您到 相册/图库 中查看", TipDialog.TYPE.SUCCESS);
        } catch (FileNotFoundException e) {
            TipDialog.show((AppCompatActivity) mActivity, "保存失败", TipDialog.TYPE.SUCCESS);
            e.printStackTrace();
        }
        // 最后通知图库更新
        mActivity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.fromFile(new File(imageFile.getPath()))));
    }

    private static final int RC_CAMERA_AND_LOCATION = 100;
    private static final int RC_PHONE_STATE_PERM = 101;

    @AfterPermissionGranted(RC_CAMERA_AND_LOCATION)
    private void requestCameraPermission() {
        List<String> list = new ArrayList<>();
        if (!EasyPermissions.hasPermissions(mActivity, Manifest.permission.CAMERA)) {
            list.add(Manifest.permission.CAMERA);
        }
        int size = list.size();
        if (size > 0) {
            String[] perms = list.toArray(new String[size]);
            EasyPermissions.requestPermissions(new PermissionRequest.Builder(this, RC_CAMERA_AND_LOCATION, perms).setRationale
                    ("使用扫描功能需要相机权限，点击确定授予权限。").setNegativeButtonText("取消").setPositiveButtonText
                    ("确定").build());
        } else {
            ScanCodeActivity.start(mActivity);
        }
    }

    @AfterPermissionGranted(RC_PHONE_STATE_PERM)
    private void requestPhoneStatePermission() {
        List<String> list = new ArrayList<>();
        if (!EasyPermissions.hasPermissions(mActivity, Manifest.permission.READ_PHONE_STATE)) {
            list.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (!EasyPermissions.hasPermissions(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            list.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!EasyPermissions.hasPermissions(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            list.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        int size = list.size();
        if (size > 0) {
            String[] perms = list.toArray(new String[size]);
            EasyPermissions.requestPermissions(new PermissionRequest.Builder(this, RC_PHONE_STATE_PERM, perms).setRationale
                    ("保存二维码需要相册权限，点击确定授予权限。").setNegativeButtonText("取消").setPositiveButtonText
                    ("确定").build());
        } else {
            saveImageToLocal(bmpShare);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

}