package com.huli.foxread.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.huli.foxread.R;
import com.huli.foxread.cache.UserInfoCache;
import com.huli.foxread.callbacks.ookkggoo.LtbCallback;
import com.huli.foxread.callbacks.ookkggoo.LtbJsonCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.entity.CapitalEntity;
import com.huli.foxread.entity.FUser;
import com.huli.foxread.entity.MineWelfareZoneEntity;
import com.huli.foxread.entity.eventbus.VipChargerEvent;
import com.huli.foxread.ui.activities.HelpAndFeedbackActivity;
import com.huli.foxread.ui.activities.InvitationCodeActivity;
import com.huli.foxread.ui.activities.InviteFriendsActivity;
import com.huli.foxread.ui.activities.LoginActivity;
import com.huli.foxread.ui.activities.MainActivity;
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
import com.huli.foxread.utils.GlideUtil;
import com.huli.foxread.utils.StatusBarUtils;
import com.kongzue.dialog.v3.TipDialog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class MainMineFragment extends BaseFragment implements View.OnClickListener, OnItemClickListener {

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

    private View btnIviter;

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

        changeUIbyIsVisitor(mActivity);
        changeUIbyIsVip(mActivity);
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
        getUserReadTime();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        MineWelfareZoneEntity data = wzAdapter.getData().get(position);
        String link = data.getLink();
        if (link.equals(Consts.INVITATION)) {       //去邀请
            if (UserInfoCache.getIsVisitor(mActivity)) {
                startActivity(new Intent(mActivity, LoginActivity.class));
                return;
            }
            Intent intent = new Intent(mActivity, InviteFriendsActivity.class);
            startActivity(intent);
        } else if (data.getLink().equals(Consts.BE_INVITATION)) {       //去填写邀请码
            if (UserInfoCache.getIsVisitor(mActivity)) {
                startActivity(new Intent(mActivity, LoginActivity.class));
                return;
            }
            Intent intent = new Intent(mActivity, InvitationCodeActivity.class);
            startActivity(intent);
        } else if (data.getLink().equals(Consts.EVERYDAY_READING)) {
            ((MainActivity) mActivity).switch2Bookstore();
        } else if (data.getLink().equals(Consts.READING)) {
            ((MainActivity) mActivity).switch2Bookstore();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onUserInfoChangeEvent(FUser event) {
        changeUIbyIsVisitor(mActivity);
        changeUIbyIsVip(mActivity);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVipChargerEvent(VipChargerEvent event) {
        changeUIbyIsVip(mActivity);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onCapitalRefreshEvent(CapitalEntity event) {
        tvMyGoldCoin.setText(String.valueOf(event.getScore()));
        tvTodayGoldCoin.setText(String.valueOf(event.getToday_score()));
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

        //是否已经填写邀请码
        if (UserInfoCache.getIsInvited(mActivity) == 0) {
            //如果未填写邀请码
            btnIviter.setVisibility(View.VISIBLE);
        } else {
            btnIviter.setVisibility(View.GONE);
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

    /**
     * 显示用户信息
     *
     * @param mContext
     */
    private void displayUserInfo(Context mContext) {
        GlideUtil.loadCircle(mContext, ivUserHeadImg, UserInfoCache.getHeadPic(mContext));
        tvNickname.setText(UserInfoCache.getUserName(mContext));
        tvUserId.setText((getString(R.string.txt_id_colon) + UserInfoCache.getUserId(mContext)));
        tvMyGoldCoin.setText(String.valueOf(UserInfoCache.getScore(mContext)));
        tvTodayGoldCoin.setText(String.valueOf(UserInfoCache.getTodayScore(mContext)));
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
            case R.id.ll_today_gold_coin_mine:
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
       /* if (resultCode == Activity.RESULT_OK) {
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
        }*/
    }

    private void go2LoginAndResult() {
        startActivity(new Intent(mActivity, LoginActivity.class));
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

    /**
     * 获取用户阅读时间
     */
    private void getUserReadTime() {
        OkGo.<String>get(Consts.USER_READ_TIME_API)
                .execute(new LtbCallback((AppCompatActivity) mActivity, false) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<String> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<String>>() {
                                });
                        if (entity.error_code == 0) {
                            tvTodayReadingTime.setText(entity.getData());
                        } else {
                            TipDialog.show((AppCompatActivity) mActivity, entity.msg, TipDialog.TYPE.ERROR);
                        }
                    }
                });
    }

}
