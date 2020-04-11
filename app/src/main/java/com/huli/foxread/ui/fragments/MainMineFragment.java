package com.huli.foxread.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.huli.foxread.R;
import com.huli.foxread.cache.TokenCache;
import com.huli.foxread.cache.UserInfoCache2;
import com.huli.foxread.callbacks.ookkggoo.LtbCallback;
import com.huli.foxread.callbacks.ookkggoo.LtbJsonCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.entity.CapitalEntity;
import com.huli.foxread.entity.FUser;
import com.huli.foxread.entity.LoginRpsEntity;
import com.huli.foxread.entity.MineWelfareZoneEntity;
import com.huli.foxread.entity.eventbus.ReadingTimeEvent;
import com.huli.foxread.entity.eventbus.VipChargerEvent;
import com.huli.foxread.ui.activities.HelpAndFeedbackActivity;
import com.huli.foxread.ui.activities.InvitationCodeActivity;
import com.huli.foxread.ui.activities.MsgNotifyActivity;
import com.huli.foxread.ui.activities.MyGoldCoinActivity;
import com.huli.foxread.ui.activities.MyPrivilegeActivity;
import com.huli.foxread.ui.activities.ReadingRecordActivity;
import com.huli.foxread.ui.activities.SettingActivity;
import com.huli.foxread.ui.activities.SignInActivity;
import com.huli.foxread.ui.activities.UserBasicInfoActivity;
import com.huli.foxread.ui.activities.WithdrawalActivity;
import com.huli.foxread.ui.adapters.WelfareZoneMineAdapter;
import com.huli.foxread.ui.base.BaseFragment;
import com.huli.foxread.ui.decoration.HorizontalItemDecoration;
import com.huli.foxread.utils.ClickJumpUtil;
import com.huli.foxread.utils.GlideUtil;
import com.huli.foxread.utils.StatusBarUtils;
import com.huli.foxread.utils.UniqueIdManager;
import com.kongzue.dialog.v3.TipDialog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.sh.sdk.shareinstall.autologin.AutoLoginManager;
import com.sh.sdk.shareinstall.autologin.listener.AvoidPwdLoginListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class MainMineFragment extends BaseFragment implements View.OnClickListener, OnItemClickListener {

    private View layoutLogged, layoutNotLogin;
    private Button btnLogin;

    private ImageView ivUserHeadImg;
    private TextView tvNickname, tvUserId;
    private TextView tvMyGoldCoin, tvTodayGoldCoin, tvTodayReadingTime;

    private TextView tvHuliVip, tvVipAdvantage;
    private TextView btnOpenVip;

    private View btnIviter;

    private RecyclerView rvWelfareZone;
    private WelfareZoneMineAdapter wzAdapter;

    private boolean isVisitor;
    private boolean isVip;
    private boolean isInvited;

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
        tvNickname = $(view, R.id.tv_user_nickname);
        tvUserId = $(view, R.id.tv_user_id);
        $(view, R.id.ll_my_gold_coin_mine).setOnClickListener(this);
        $(view, R.id.ll_today_gold_coin_mine).setOnClickListener(this);
//        $(view, R.id.ll_today_reading_count_mine).setOnClickListener(this);
        tvMyGoldCoin = $(view, R.id.tv_my_gold_coin_mine);
        tvTodayGoldCoin = $(view, R.id.tv_today_gold_coin_mine);
        tvTodayReadingTime = $(view, R.id.tv_today_reading_time_mine);

        btnLogin = $(view, R.id.btn_login_mine);
        tvHuliVip = $(view, R.id.tv_huli_vip_member);
        tvVipAdvantage = $(view, R.id.tv_huli_vip_advantage_tip);
        btnOpenVip = $(view, R.id.tv_asBtn_open_membership_account);

        btnIviter = $(view, R.id.rtl_asBtn_inviter);
        $(view, R.id.ll_asBtn_sign_in_4_gold).setOnClickListener(this);
        $(view, R.id.rtl_asBtn_my_privilege).setOnClickListener(this);
        $(view, R.id.rtl_asBtn_msg_notify).setOnClickListener(this);
        $(view, R.id.rtl_asBtn_reading_record).setOnClickListener(this);
        $(view, R.id.rtl_asBtn_inviter).setOnClickListener(this);
        $(view, R.id.rtl_asBtn_cash_withdrawal).setOnClickListener(this);
        $(view, R.id.rtl_asBtn_mode_adolescent).setOnClickListener(this);
        $(view, R.id.rtl_asBtn_help_and_feedback).setOnClickListener(this);
        $(view, R.id.iv_asBtn_setting_mine).setOnClickListener(this);

        FUser userInfo = UserInfoCache2.getUserInfo(mActivity);
        isVisitor = userInfo.getIs_visitor() == 1;
        changeUIbyUserInfo(userInfo);
    }

    @Override
    public void setListener() {
        btnLogin.setOnClickListener(this);
        btnOpenVip.setOnClickListener(this);
        ivUserHeadImg.setOnClickListener(this);
        wzAdapter.setOnItemClickListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {
        EventBus.getDefault().register(this);


    }

    @Override
    public void onResume() {
        super.onResume();

        reqMineWelfareZone();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        MineWelfareZoneEntity data = wzAdapter.getData().get(position);
        ClickJumpUtil.handleJump(mActivity, data.getLink(), data.getJump());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onUserInfoChangeEvent(FUser event) {
        isVisitor = event.getIs_visitor() == 1;
        changeUIbyUserInfo(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onVipChargerEvent(VipChargerEvent event) {
        isVip = event.isBecomingVip();
        changeUIbyIsVip(event.isBecomingVip());
        EventBus.getDefault().removeStickyEvent(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onCapitalRefreshEvent(CapitalEntity event) {
        tvMyGoldCoin.setText(String.valueOf(event.getScore()));
        tvTodayGoldCoin.setText(String.valueOf(event.getToday_score()));
    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onReadingTimeEvent(ReadingTimeEvent event) {
        tvTodayReadingTime.setText(event.getReadMin());
    }


    /**
     * 用户信息改变的时候改变UI
     *
     * @param fUser
     */
    private void changeUIbyUserInfo(FUser fUser) {
        isVisitor = fUser.getIs_visitor() == 1;
        if (isVisitor) {
            layoutNotLogin.setVisibility(View.VISIBLE);
            layoutLogged.setVisibility(View.GONE);
        } else {
            layoutNotLogin.setVisibility(View.GONE);
            layoutLogged.setVisibility(View.VISIBLE);
            displayUserInfo(fUser);
        }

        //VIP
        isVip = fUser.getIs_vip() == 1;
        changeUIbyIsVip(isVip);

        //是否已经填写邀请码
        isInvited = fUser.getIs_invited() > 0;
        if (isInvited) {
            btnIviter.setVisibility(View.GONE);
        } else {
            //如果未填写邀请码
            btnIviter.setVisibility(View.VISIBLE);
        }

    }


    private void changeUIbyIsVip(boolean isVip) {
        if (isVip) {
            //已成为VIP
            tvHuliVip.setText(R.string.txt_you_have_become_a_vip);
            Drawable drawable = ContextCompat.getDrawable(mActivity, R.mipmap.icon_vip_symbol);
            if (drawable != null) {
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                tvHuliVip.setCompoundDrawables(drawable, null, null, null);
            }
            tvVipAdvantage.setVisibility(View.GONE);
            btnOpenVip.setText(R.string.txt_view_details);
        } else {
            tvHuliVip.setText(null);
            tvHuliVip.setCompoundDrawables(null, null, null, null);
            tvVipAdvantage.setVisibility(View.VISIBLE);
            btnOpenVip.setText(R.string.txt_activate_immediately);
        }
    }

    /**
     * 显示用户信息
     */
    private void displayUserInfo(FUser fUser) {
        GlideUtil.loadCircle(mActivity, ivUserHeadImg, fUser.getHttp_avatar());
        tvNickname.setText(fUser.getUsername());
        tvUserId.setText((getString(R.string.txt_id_colon) + fUser.getId()));
//        tvMyGoldCoin.setText(String.valueOf(UserInfoCache.getScore(mContext)));
//        tvTodayGoldCoin.setText(String.valueOf(UserInfoCache.getTodayScore(mContext)));
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            StatusBarUtils.setStatusBarTextDark(mActivity, true);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_asBtn_sign_in_4_gold:          //签到
                startActivity(new Intent(mActivity, SignInActivity.class));
                break;
            case R.id.iv_asBtn_setting_mine:
                startActivity(new Intent(mActivity, SettingActivity.class));
                break;
            case R.id.btn_login_mine:
//                LoginActivity.start(mActivity);
                AutoLoginManager.getInstance().doAvoidPwdLogin(getActivity(), new AvoidPwdLoginListener() {

                    @Override
                    public void onGetLoginTokenSuccess(String operatorType, String token, String secureMobile) {
                        Log.e(TAG, "operatorType>>" + operatorType + ">>token>>" + token + ">>secureMobile>>" + secureMobile);
                        /**
                         * operatorType 运营商类型  1电信 2移动 3联通
                         * token 移动联通为登录token    电信为accessCode
                         * secureMobile 移动联通为带星手机号  电信为authCode
                         */
                        getPhoneNum(operatorType, token, secureMobile);
                        AutoLoginManager.getInstance().closeOperatorActivity();
                    }

                    @Override
                    public void onGetLoginTokenFaild(String operatorType, String code, final String errorMsg) {
                        /**
                         *
                         *operatorType 运营商类型  1电信 2移动 3联通
                         * code  1001用户关闭授权页取消授权 1002其他错误
                         * errorMsg 错误消息
                         * errorResultCode 运营商返回的错误码
                         */
                        Log.e(TAG, "获取授权码失败：" + errorMsg);
                        AutoLoginManager.getInstance().closeOperatorActivity();
                    }

                    @Override
                    public void onOtherWayLogin() {
                        Log.e(TAG, "点击其他登录方式");
                        AutoLoginManager.getInstance().closeOperatorActivity();
                    }
                });
                break;
            case R.id.iv_user_headImg:
                startActivity(new Intent(mActivity, UserBasicInfoActivity.class));
                break;
            case R.id.ll_my_gold_coin_mine:
            case R.id.ll_today_gold_coin_mine:
                startActivity(new Intent(mActivity, MyGoldCoinActivity.class));
                break;
            case R.id.rtl_asBtn_my_privilege:                   //go2 VIP页面
            case R.id.tv_asBtn_open_membership_account:         //go2 VIP页面
                startActivity(new Intent(mActivity, MyPrivilegeActivity.class));
                break;
            case R.id.rtl_asBtn_msg_notify:
                startActivity(new Intent(mActivity, MsgNotifyActivity.class));
                break;
            case R.id.rtl_asBtn_reading_record:
                startActivity(new Intent(mActivity, ReadingRecordActivity.class));
                break;
            case R.id.rtl_asBtn_inviter:
                if (!isInvited) {
                    startActivity(new Intent(mActivity, InvitationCodeActivity.class));
                }
                break;
            case R.id.rtl_asBtn_cash_withdrawal:
                startActivity(new Intent(mActivity, WithdrawalActivity.class));
                break;
            case R.id.rtl_asBtn_mode_adolescent:

                break;
            case R.id.rtl_asBtn_help_and_feedback:
                startActivity(new Intent(mActivity, HelpAndFeedbackActivity.class));
                break;

            default:
                break;
        }
    }

    private void getPhoneNum(String operatorType, String token, String authCode) {
        String uniqueID = UniqueIdManager.getUniqueID(mActivity);
        OkGo.<LzyResponse<LoginRpsEntity>>post(Consts.USE_PHONE_ONEKEY_LOGIN)
                .params(Consts.TYPE, operatorType)
                .params("authCode", operatorType.equals("1") ? authCode : "")
                .params(Consts.TOKEN, token)
                .params("plantFrom", "1")
                .params(Consts.UNIQUE_ID, uniqueID)
                .execute(new LtbJsonCallback<LzyResponse<LoginRpsEntity>>((AppCompatActivity) mActivity, false,
                        new TypeReference<LzyResponse<LoginRpsEntity>>() {
                        }) {
                    @Override
                    public void onSuccess(Response<LzyResponse<LoginRpsEntity>> response) {
                        if (response.body().error_code == 0) {
                            LoginRpsEntity data = response.body().getData();
                            TokenCache.saveToken(mActivity, data.getToken());
                        } else {
                            TipDialog.show((AppCompatActivity) mActivity, response.body().msg, TipDialog.TYPE.ERROR);
                        }
                    }
                });
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
        OkGo.<String>get(Consts.WELFARE_USERLIST_API)
                .execute(new LtbCallback((AppCompatActivity) mActivity, false) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<List<MineWelfareZoneEntity>> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<List<MineWelfareZoneEntity>>>() {
                                });
                        if (entity.error_code == 0) {
                            List<MineWelfareZoneEntity> datas = entity.getData();
                            wzAdapter.setNewData(datas);
                        }
                    }
                });
    }

}
