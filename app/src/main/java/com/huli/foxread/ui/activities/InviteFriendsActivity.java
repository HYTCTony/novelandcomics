package com.huli.foxread.ui.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.huli.foxread.R;
import com.huli.foxread.cache.UserInfoCache;
import com.huli.foxread.callbacks.ookkggoo.LtbCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Common;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.entity.InviteFriendsPageBean;
import com.huli.foxread.entity.InviteRewardBean;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.utils.StatusBarUtils;
import com.huli.foxread.utils.Tos;
import com.kongzue.dialog.v3.TipDialog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.text.DecimalFormat;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

public class InviteFriendsActivity extends BaseActivity implements View.OnClickListener {

    private TextView btnExplain;
    private TextView tvInviteCode;
    private Button btnCopy;
    private Button btnImmediatelyInvite;

    private TextView tvInvitedNum, tvMyMoney;
    private TextView btnGo2Check, btnGo2Withdrawal;

    private TextView tvDay1Tips, tvDay2Tips, tvDay3Tips;
    private TextView tvRewardMoneyDay1, tvRewardMoneyDay2, tvRewardMoneyDay3;

    //我的现金余额
    private double myMoney;

    //获取剪贴板管理器：
    private ClipboardManager cm;

    private boolean isInit = true;

    @Override
    protected void setStatusBar() {
        StatusBarUtils.setColor(this, ContextCompat.getColor(this, R.color.transparent), 0);
        StatusBarUtils.setAndroidNativeLightStatusBar(this, false);
    }

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_invite_friends;
    }

    @Override
    public void initView(View view) {
        Toolbar toolbar = $(R.id.toolbar_normal);
        toolbar.setBackgroundResource(R.color.transparent);
        initToolBar(toolbar, R.string.txt_invite_friends);
        StatusBarUtils.offsetView(this, toolbar);

        btnExplain = $(R.id.tv_asBtn_explain);
        tvInviteCode = $(R.id.tv_my_invite_code);
        btnCopy = $(R.id.btn_copy);
        btnImmediatelyInvite = $(R.id.btn_immediately_invite);

        tvInvitedNum = $(R.id.tv_has_invited_friends_num);
        tvMyMoney = $(R.id.tv_has_made_money);

        tvDay1Tips = $(R.id.tv_invite_award_day1_tips);
        tvDay2Tips = $(R.id.tv_invite_award_day2_tips);
        tvDay3Tips = $(R.id.tv_invite_award_day3_tips);
        tvRewardMoneyDay1 = $(R.id.tv_reward_money_day1);
        tvRewardMoneyDay2 = $(R.id.tv_reward_money_day2);
        tvRewardMoneyDay3 = $(R.id.tv_reward_money_day3);

        btnGo2Check = $(R.id.tv_has_invited_friends_num_go2_check);
        btnGo2Withdrawal = $(R.id.tv_has_made_money_go2_withdrawal);
        initBtnCheckItf();
        initBtnWithdrawal();
    }

    @Override
    public void setListener() {
        btnExplain.setOnClickListener(this);
        btnCopy.setOnClickListener(this);
        btnImmediatelyInvite.setOnClickListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {
        tvInviteCode.setText(UserInfoCache.getDistribution(mContext));

        cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        reqInviteFriendsInfo(isInit);
    }

    @Override
    public void onClick(View view) {
        if (onMoreClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.tv_asBtn_explain:
                Intent intent = new Intent(this, CommonWebActivity.class);
                intent.putExtra(Common.KEY_URL, Consts.INVITE_FRIENDS_EXPLAIN_URL);
                startActivity(intent);
                break;
            case R.id.btn_copy:
                // 创建普通字符型ClipData
                ClipData mClipData = ClipData.newPlainText("my_invite_code", UserInfoCache.getDistribution(this));
                // 将ClipData内容放到系统剪贴板里。
                cm.setPrimaryClip(mClipData);
                Tos.showShort(this, R.string.tips_copy_success);
                break;
            case R.id.btn_immediately_invite:
                //TODO 立即邀请
                Tos.showShort(this, "立即邀请");
                break;
            default:
                break;
        }
    }

    /**
     * 查看已邀请的好友
     */
    private void initBtnCheckItf() {
        String itfCheckStr = getString(R.string.txt_itf_go2_check);
        SpannableString spannableString = new SpannableString(itfCheckStr);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                startActivity(new Intent(InviteFriendsActivity.this, MyInviteFriendsActivity.class));
            }

            //去除连接下划线
            @Override
            public void updateDrawState(TextPaint ds) {
                /**set textColor**/
//                ds.setColor(ds.linkColor);
                ds.setColor(ContextCompat.getColor(InviteFriendsActivity.this, R.color.txt_red));
                /**Remove the underline**/
                ds.setUnderlineText(false);
            }
        };
        spannableString.setSpan(clickableSpan, itfCheckStr.length() - 3, itfCheckStr.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        btnGo2Check.setMovementMethod(LinkMovementMethod.getInstance());//不设置 没有点击事件
        btnGo2Check.setText(spannableString);
    }

    /**
     * 现金提现
     */
    private void initBtnWithdrawal() {
        String withdrawalStr = getString(R.string.txt_made_money_go2_withdrawal);
        SpannableString spannableString = new SpannableString(withdrawalStr);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent intent = new Intent(InviteFriendsActivity.this, WithdrawalRMBActivity.class);
                intent.putExtra(Common.KEY_MONEY, myMoney);
                startActivity(intent);
            }

            //去除连接下划线
            @Override
            public void updateDrawState(TextPaint ds) {
                /**set textColor**/
                ds.setColor(ContextCompat.getColor(InviteFriendsActivity.this, R.color.txt_red));
                /**Remove the underline**/
                ds.setUnderlineText(false);
            }
        };
        spannableString.setSpan(clickableSpan, withdrawalStr.length() - 3, withdrawalStr.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        btnGo2Withdrawal.setMovementMethod(LinkMovementMethod.getInstance());   //不设置 没有点击事件
        btnGo2Withdrawal.setText(spannableString);
    }


    /**
     * 邀请好友页面信息
     */
    private void reqInviteFriendsInfo(boolean showDialog) {
        isInit = false;
        OkGo.<String>get(Consts.WELFARE_INVITE_API)
                .execute(new LtbCallback(this, showDialog) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<InviteFriendsPageBean> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<InviteFriendsPageBean>>() {
                                });
                        if (entity.error_code == 0) {
                            InviteFriendsPageBean data = entity.getData();
                            tvInvitedNum.setText(String.valueOf(data.getSum_man()));
                            myMoney = data.getMoney();
                            tvMyMoney.setText(new DecimalFormat("######0.00").format(myMoney));

                            List<InviteRewardBean> list = data.getList();
                            if (list != null && list.size() >= 3) {
                                InviteRewardBean rewardDay1 = list.get(0);
                                InviteRewardBean rewardDay2 = list.get(1);
                                InviteRewardBean rewardDay3 = list.get(2);
                                tvDay1Tips.setText(String.format(getString(R.string.txt_friend_reading_time_x), rewardDay1.getDuration()));
                                tvDay2Tips.setText(String.format(getString(R.string.txt_friend_reading_time_x), rewardDay2.getDuration()));
                                tvDay3Tips.setText(String.format(getString(R.string.txt_friend_reading_time_x), rewardDay3.getDuration()));

                                DecimalFormat dFormat = new DecimalFormat("#######" + "元");
                                tvRewardMoneyDay1.setText(dFormat.format(rewardDay1.getMoney()));
                                tvRewardMoneyDay2.setText(dFormat.format(rewardDay2.getMoney()));
                                tvRewardMoneyDay3.setText(dFormat.format(rewardDay3.getMoney()));
                            }
                        } else {
                            TipDialog.show(InviteFriendsActivity.this, entity.msg, TipDialog.TYPE.ERROR);
                        }
                    }
                });
    }

}
