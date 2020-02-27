package com.huli.foxread.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.huli.foxread.R;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.utils.StatusBarUtils;
import com.huli.foxread.utils.Tos;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

public class InviteFriendsActivity extends BaseActivity {

    private TextView tvInviteCode;

    private TextView btnGo2Check, btnGo2Withdrawal;

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

        tvInviteCode = $(R.id.tv_my_invite_code);

        btnGo2Check = $(R.id.tv_has_invited_friends_num_go2_check);
        btnGo2Withdrawal = $(R.id.tv_has_made_money_go2_withdrawal);
        initBtnCheckItf();
        initBtnWithdrawal();
    }

    @Override
    public void setListener() {

    }

    @Override
    public void doBusiness(Context mContext) {
        tvInviteCode.setText("JK00001");

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
                Tos.showShort(InviteFriendsActivity.this, "去查看");
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
     * 去提现
     */
    private void initBtnWithdrawal() {
        String withdrawalStr = getString(R.string.txt_made_money_go2_withdrawal);
        SpannableString spannableString = new SpannableString(withdrawalStr);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Tos.showShort(InviteFriendsActivity.this, "去提现");
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
        btnGo2Withdrawal.setMovementMethod(LinkMovementMethod.getInstance());//不设置 没有点击事件
        btnGo2Withdrawal.setText(spannableString);
    }
}
