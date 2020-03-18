package com.huli.foxread.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.huli.foxread.R;
import com.huli.foxread.cache.UserInfoCache;
import com.huli.foxread.entity.eventbus.LoginChangeEvent;
import com.huli.foxread.ui.activities.HelpAndFeedbackActivity;
import com.huli.foxread.ui.activities.InvitationCodeActivity;
import com.huli.foxread.ui.activities.LoginActivity;
import com.huli.foxread.ui.activities.MsgNotifyActivity;
import com.huli.foxread.ui.activities.MyGoldCoinActivity;
import com.huli.foxread.ui.activities.MyPrivilegeActivity;
import com.huli.foxread.ui.activities.ReadingRecordActivity;
import com.huli.foxread.ui.activities.SettingActivity;
import com.huli.foxread.ui.activities.UserBasicInfoActivity;
import com.huli.foxread.ui.activities.WithdrawalActivity;
import com.huli.foxread.ui.adapters.SignInActivity;
import com.huli.foxread.ui.adapters.WelfareZoneMineAdapter;
import com.huli.foxread.ui.base.BaseFragment;
import com.huli.foxread.ui.decoration.HorizontalItemDecoration;
import com.huli.foxread.utils.GlideUtil;
import com.huli.foxread.utils.StatusBarUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class MainMineFragment extends BaseFragment implements View.OnClickListener {

    public static final int REQCODE_LOGIN = 0x5688;
    private static final int REQCODE_SETTING_AC = 0x8865;
    public static final int REQCODE_USER_ATTR = 0x9999;

    private View layoutLogged, layoutNotLogin;
    private Button btnLogin;

    private ImageView ivUserHeadImg;
    private TextView tvNickname, tvUserId;
    private TextView tvMyGoldCoin, tvTodayGoldCoin, tvTodayReadingTime;

    private TextView tvHuliVip, tvVipAdvantage;
    private TextView btnOpenVip;

    private RecyclerView rvWelfareZone;
    private WelfareZoneMineAdapter wzAdapter;

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
//        $(view, R.id.ll_today_gold_coin_mine).setOnClickListener(this);
//        $(view, R.id.ll_today_reading_count_mine).setOnClickListener(this);
        tvMyGoldCoin = $(view, R.id.tv_my_gold_coin_mine);
        tvTodayGoldCoin = $(view, R.id.tv_today_gold_coin_mine);
        tvTodayReadingTime = $(view, R.id.tv_today_reading_time_mine);

        btnLogin = $(view, R.id.btn_login_mine);
        tvHuliVip = $(view, R.id.tv_huli_vip_member);
        tvVipAdvantage = $(view, R.id.tv_huli_vip_advantage_tip);
        btnOpenVip = $(view, R.id.tv_asBtn_open_membership_account);

        $(view, R.id.ll_asBtn_sign_in_4_gold).setOnClickListener(this);
        $(view, R.id.rtl_asBtn_my_privilege).setOnClickListener(this);
        $(view, R.id.rtl_asBtn_msg_notify).setOnClickListener(this);
        $(view, R.id.rtl_asBtn_reading_record).setOnClickListener(this);
        $(view, R.id.rtl_asBtn_inviter).setOnClickListener(this);
        $(view, R.id.rtl_asBtn_cash_withdrawal).setOnClickListener(this);
        $(view, R.id.rtl_asBtn_mode_adolescent).setOnClickListener(this);
        $(view, R.id.rtl_asBtn_help_and_feedback).setOnClickListener(this);
        $(view, R.id.iv_asBtn_setting_mine).setOnClickListener(this);

        if (UserInfoCache.getIsInvited(mActivity) == 0) {
            //如果未填写邀请码
            $(view, R.id.rtl_asBtn_inviter).setVisibility(View.VISIBLE);
        } else {
            $(view, R.id.rtl_asBtn_inviter).setVisibility(View.GONE);
        }
    }

    @Override
    public void setListener() {
        btnLogin.setOnClickListener(this);
        btnOpenVip.setOnClickListener(this);
        ivUserHeadImg.setOnClickListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {
        EventBus.getDefault().register(this);

        changeUIbyIsVisitor(mContext);

        List<String> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add("ssssss" + i);
        }
        wzAdapter.setNewData(list);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginChangeEvent(LoginChangeEvent event){
        changeUIbyIsVisitor(mActivity);
    }


    private void changeUIbyIsVisitor(Context mContext) {
        boolean isVisitor = UserInfoCache.getIsVisitor(mContext);
        if (isVisitor) {
            layoutNotLogin.setVisibility(View.VISIBLE);
            layoutLogged.setVisibility(View.GONE);
        } else {
            layoutNotLogin.setVisibility(View.GONE);
            layoutLogged.setVisibility(View.VISIBLE);
            displayUserInfo(mContext);

            changeUIbyIsVip(mContext);
        }
    }


    private void changeUIbyIsVip(Context mContext) {
        if (UserInfoCache.getIsVip(mContext)) {
            //已成为VIP
            tvHuliVip.setText(R.string.txt_you_have_become_a_vip);
            Drawable drawable = ContextCompat.getDrawable(mContext, R.mipmap.icon_vip_symbol);
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

    private void displayUserInfo(Context mContext) {
        GlideUtil.loadCircle(mContext, ivUserHeadImg, UserInfoCache.getHeadPic(mContext));
        tvNickname.setText(UserInfoCache.getUserName(mContext));
        tvUserId.setText((getString(R.string.txt_id_colon) + UserInfoCache.getUserId(mContext)));
        tvMyGoldCoin.setText(String.valueOf(UserInfoCache.getScore(mContext)));
        tvTodayGoldCoin.setText(String.valueOf(UserInfoCache.getTodayScore(mContext)));
//        tvTodayReadingTime.setText();
        //TODO 今日阅读时间
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
                if (UserInfoCache.getIsVisitor(mActivity)) {
                    go2LoginAndResult();
                } else {
                    startActivity(new Intent(mActivity, SignInActivity.class));
                }
                break;
            case R.id.iv_asBtn_setting_mine:
                startActivityForResult(new Intent(mActivity, SettingActivity.class), REQCODE_SETTING_AC);
                break;
            case R.id.btn_login_mine:
                go2LoginAndResult();
                break;
            case R.id.iv_user_headImg:
                startActivityForResult(new Intent(mActivity, UserBasicInfoActivity.class), REQCODE_USER_ATTR);
                break;
            case R.id.ll_my_gold_coin_mine:
                if (UserInfoCache.getIsVisitor(mActivity)) {
                    go2LoginAndResult();
                } else {
                    startActivity(new Intent(mActivity, MyGoldCoinActivity.class));
                }
                break;
            case R.id.rtl_asBtn_my_privilege:                   //VIP
            case R.id.tv_asBtn_open_membership_account:         //VIP
                if (UserInfoCache.getIsVisitor(mActivity)) {
                    go2LoginAndResult();
                } else {
                    startActivity(new Intent(mActivity, MyPrivilegeActivity.class));
                }
                break;
            case R.id.rtl_asBtn_msg_notify:
                startActivity(new Intent(mActivity, MsgNotifyActivity.class));
                break;
            case R.id.rtl_asBtn_reading_record:
                startActivity(new Intent(mActivity, ReadingRecordActivity.class));
                break;
            case R.id.rtl_asBtn_inviter:
                if (!UserInfoCache.getIsVisitor(mActivity) && UserInfoCache.getIsInvited(mActivity) == 0) {
                    startActivity(new Intent(mActivity, InvitationCodeActivity.class));
                } else {
                    go2LoginAndResult();
                }
                break;
            case R.id.rtl_asBtn_cash_withdrawal:
                if (UserInfoCache.getIsVisitor(mActivity)) {
                    go2LoginAndResult();
                } else {
                    startActivity(new Intent(mActivity, WithdrawalActivity.class));
                }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQCODE_LOGIN:
                case REQCODE_SETTING_AC:
                    changeUIbyIsVisitor(mActivity);
                    break;
                case REQCODE_USER_ATTR:                  //修改用户属性返回
                    displayUserInfo(mActivity);
                    break;
                default:
                    break;
            }
        } else if (resultCode == REQCODE_USER_ATTR) {      //修改用户属性返回（中间SettingActivity）
            if (requestCode == REQCODE_SETTING_AC) {
                displayUserInfo(mActivity);
            }
        }
    }

    private void go2LoginAndResult() {
        startActivityForResult(new Intent(mActivity, LoginActivity.class), REQCODE_LOGIN);
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
}
