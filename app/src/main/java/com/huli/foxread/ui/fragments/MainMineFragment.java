package com.huli.foxread.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.huli.foxread.R;
import com.huli.foxread.RxHttp;
import com.huli.foxread.cache.TokenCache;
import com.huli.foxread.cache.UserInfoCache;
import com.huli.foxread.callbacks.ookkggoo.LtbCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.entity.CapitalEntity;
import com.huli.foxread.entity.FUser;
import com.huli.foxread.entity.MineWelfareZoneEntity;
import com.huli.foxread.entity.eventbus.ReadingTimeEvent;
import com.huli.foxread.entity.eventbus.UnReadMsgEvent;
import com.huli.foxread.entity.eventbus.VipChargerEvent;
import com.huli.foxread.listeners.OnClickEvent;
import com.huli.foxread.ui.activities.HelpAndFeedbackActivity;
import com.huli.foxread.ui.activities.InviteFriendsActivity2;
import com.huli.foxread.ui.activities.LoginActivity;
import com.huli.foxread.ui.activities.MainActivity;
import com.huli.foxread.ui.activities.MsgNotifyActivity;
import com.huli.foxread.ui.activities.MyGoldCoinActivity;
import com.huli.foxread.ui.activities.MyPrivilegeActivity;
import com.huli.foxread.ui.activities.ReadingPreferenceActivity;
import com.huli.foxread.ui.activities.ReadingRecordActivity;
import com.huli.foxread.ui.activities.SettingActivity;
import com.huli.foxread.ui.activities.SignInActivity;
import com.huli.foxread.ui.activities.UserBasicInfoActivity;
import com.huli.foxread.ui.activities.WithdrawalActivity;
import com.huli.foxread.ui.adapters.WelfareZoneMineAdapter;
import com.huli.foxread.ui.base.BaseFragment;
import com.huli.foxread.ui.decoration.HorizontalItemDecoration;
import com.huli.foxread.utils.ClickJumpUtil;
import com.huli.foxread.utils.DensityUtils;
import com.huli.foxread.utils.GlideUtil;
import com.huli.foxread.utils.StatusBarUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.rxjava.rxlife.RxLife;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.bingoogolapple.badgeview.BGABadgeTextView;
import rxhttp.wrapper.cahce.CacheMode;


public class MainMineFragment extends BaseFragment implements View.OnClickListener, OnItemClickListener {

    private View layoutLogged, layoutNotLogin;
    private Button btnLogin;

    private ImageView ivUserHeadImg;
    private ImageView ivHeadImgLmVipSign;       //终身会员标志
    private TextView tvNickname, tvUserId;
    private TextView tvMyGoldCoin, tvTodayGoldCoin, tvTodayReadingTime;

    private ConstraintLayout ctlVipCard;
    private TextView tvHuliVip, tvVipAdvantage;
    private TextView btnOpenVip;

    private BGABadgeTextView bgabadge;//未读消息

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
        ivHeadImgLmVipSign = $(view, R.id.iv_user_headImg_life_member_vip_sign);
        tvNickname = $(view, R.id.tv_user_nickname);
        tvUserId = $(view, R.id.tv_user_id);
        $(view, R.id.ll_my_gold_coin_mine).setOnClickListener(this);
        $(view, R.id.ll_today_gold_coin_mine).setOnClickListener(this);
//        $(view, R.id.ll_today_reading_count_mine).setOnClickListener(this);
        tvMyGoldCoin = $(view, R.id.tv_my_gold_coin_mine);
        tvTodayGoldCoin = $(view, R.id.tv_today_gold_coin_mine);
        tvTodayReadingTime = $(view, R.id.tv_today_reading_time_mine);

        TextView tvVoiceBook = $(view, R.id.tv_vip_privilege_VoiceBook);
        tvVoiceBook.getPaint().setAntiAlias(true); // 抗锯齿
        tvVoiceBook.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG); // 设置中划线并加清晰
        tvVoiceBook.setOnClickListener(new OnClickEvent() {
            @Override
            public void singleClick(View v) {
                Toast.makeText(mActivity, "此功能正在完善中，敬请期待！", Toast.LENGTH_SHORT).show();
            }
        });

        btnLogin = $(view, R.id.btn_login_mine);
        ctlVipCard = $(view, R.id.ctl_content_vip_card);
        tvHuliVip = $(view, R.id.tv_huli_vip_member);
        tvVipAdvantage = $(view, R.id.tv_huli_vip_advantage_tip);
        btnOpenVip = $(view, R.id.tv_asBtn_open_membership_account);

        $(view, R.id.ll_asBtn_sign_in_4_gold).setOnClickListener(this);
        $(view, R.id.rtl_asBtn_my_privilege).setOnClickListener(this);
        $(view, R.id.rtl_asBtn_msg_notify).setOnClickListener(this);
        $(view, R.id.rtl_asBtn_reading_preference).setOnClickListener(this);
        $(view, R.id.rtl_asBtn_reading_record).setOnClickListener(this);
        $(view, R.id.rtl_asBtn_invite_friends).setOnClickListener(this);
        $(view, R.id.rtl_asBtn_cash_withdrawal).setOnClickListener(this);
        $(view, R.id.rtl_asBtn_mode_adolescent).setOnClickListener(this);
        $(view, R.id.rtl_asBtn_help_and_feedback).setOnClickListener(this);
        $(view, R.id.iv_asBtn_setting_mine).setOnClickListener(this);
        bgabadge = $(view, R.id.bgabadge_msg_count);

        FUser userInfo = UserInfoCache.getUserInfo(mActivity);
        changeUIbyUserInfo(userInfo);
    }


    @Override
    public void setListener() {
        btnLogin.setOnClickListener(this);
        btnOpenVip.setOnClickListener(this);
        ivUserHeadImg.setOnClickListener(this);
        wzAdapter.setOnItemClickListener(this);

        bgabadge.setDragDismissDelegate(badge -> {
            reqSetMsgAllRead();
            ((MainActivity)mActivity).mTabLayout.hideMsg(4);
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
            reqMineWelfareZone();
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
        tvTodayGoldCoin.setText(String.valueOf(event.getToday_score()));
    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onReadingTimeEvent(ReadingTimeEvent event) {
        tvTodayReadingTime.setText(event.getReadMin());
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
            btnOpenVip.setVisibility(View.GONE);
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
        tvUserId.setText((getString(R.string.txt_id_colon) + fUser.getId()));
//        tvMyGoldCoin.setText(String.valueOf(UserInfoCache.getScore(mContext)));
//        tvTodayGoldCoin.setText(String.valueOf(UserInfoCache.getTodayScore(mContext)));
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            StatusBarUtils.setStatusBarTextDark(mActivity, true);

            reqMineWelfareZone();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_asBtn_sign_in_4_gold:          //签到
                startActivity(new Intent(mActivity, SignInActivity.class));
                break;
            case R.id.iv_asBtn_setting_mine:
                if (onMoreClick()) {
                    return;
                }
                startActivity(new Intent(mActivity, SettingActivity.class));
                break;
            case R.id.btn_login_mine:
                LoginActivity.start(mActivity);
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
            case R.id.rtl_asBtn_reading_preference:
                startActivity(new Intent(mActivity, ReadingPreferenceActivity.class));
                break;
            case R.id.rtl_asBtn_reading_record:
                startActivity(new Intent(mActivity, ReadingRecordActivity.class));
                break;
            case R.id.rtl_asBtn_invite_friends:
                startActivity(new Intent(mActivity, InviteFriendsActivity2.class));
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
        /*OkGo.<String>get(Consts.WELFARE_USERLIST_API)
                .execute(new LtbCallback((AppCompatActivity) mActivity, false) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<List<MineWelfareZoneEntity>> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<List<MineWelfareZoneEntity>>>() {
                                });
                        if (entity.error_code == 0) {
                            List<MineWelfareZoneEntity> datas = entity.getData();
                            wzAdapter.setList(datas);
                        }
                    }
                });*/
        //先获取缓存的
        RxHttp.postForm(Consts.WELFARE_USERLIST_API)
                .setCacheMode(CacheMode.ONLY_CACHE)
                .addHeader(Consts.TOKEN, TokenCache.getToken(mActivity))
                .asResponseList(MineWelfareZoneEntity.class)
                .to(RxLife.toMain(this))  //感知生命周期，并在主线程回调
                .subscribe(result -> {
                    wzAdapter.setList(result);
                });

        //再获取网络的
        RxHttp.postForm(Consts.WELFARE_USERLIST_API)
                .setCacheMode(CacheMode.NETWORK_SUCCESS_WRITE_CACHE)
                .addHeader(Consts.TOKEN, TokenCache.getToken(mActivity))
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
                .addHeader(Consts.TOKEN, TokenCache.getToken(mActivity))
                .asResponse(String.class)
                .to(RxLife.toMain(this))  //感知生命周期，并在主线程回调
                .subscribe(s -> {

                });
    }


}