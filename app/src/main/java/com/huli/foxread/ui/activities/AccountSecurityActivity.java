package com.huli.foxread.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.huli.foxread.R;
import com.huli.foxread.cache.UserInfoCache2;
import com.huli.foxread.entity.FUser;
import com.huli.foxread.ui.base.BaseActivity;

import androidx.appcompat.widget.Toolbar;

public class AccountSecurityActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvAccountId, tvTelNum, tvWechatBindingState;

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_account_security;
    }

    @Override
    public void initView(View view) {
        Toolbar toolbar = $(R.id.toolbar_normal);
        initToolBar(toolbar, R.string.txt_account_security);

        tvAccountId = $(R.id.tv_user_account_id);
        tvTelNum = $(R.id.tv_user_cellphone_number);
        tvWechatBindingState = $(R.id.tv_user_wechat_binding_state);


        findViewById(R.id.tv_asBtn_account_security).setVisibility(View.GONE);
    }

    @Override
    public void setListener() {
        findViewById(R.id.rtl_asBtn_user_cellphone_number).setOnClickListener(this);
        findViewById(R.id.rtl_asBtn_user_wechat).setOnClickListener(this);
        findViewById(R.id.tv_asBtn_account_security).setOnClickListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {
//        phoneNum = UserInfoCache.getMobile(mContext);
//        tvAccountId.setText(UserInfoCache.getUserId(mContext));
//        tvTelNum.setText(phoneNum.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2"));
//        tvWechatBindingState.setText(R.string.txt_unbind);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FUser fUser = UserInfoCache2.getUserInfo(this);
        phoneNum = fUser.getMobile();
        tvAccountId.setText(fUser.getId());
        tvTelNum.setText(phoneNum.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2"));
        tvWechatBindingState.setText(fUser.getIs_wx() == 1 ? R.string.txt_has_been_bind : R.string.txt_unbind);
    }

    private String phoneNum;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rtl_asBtn_user_cellphone_number:
                if (TextUtils.isEmpty(phoneNum)) {
                    //手机号为空（即 微信登录）
                    startActivity(new Intent(this, BindingCellphoneActivty.class));
                } else {
                    //手机号不为空
                    startActivity(new Intent(this, ChangeBindingActivity.class));
                }
                break;
            case R.id.rtl_asBtn_user_wechat:

                break;
            case R.id.tv_asBtn_account_security:
                startActivity(new Intent(this, ASecurityActivity.class));
                break;
            default:
                break;
        }
    }
}
