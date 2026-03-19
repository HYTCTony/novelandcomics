package com.nnmedia.read.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.kongzue.dialog.util.InputInfo;
import com.kongzue.dialog.util.TextInfo;
import com.kongzue.dialog.v3.InputDialog;
import com.kongzue.dialog.v3.TipDialog;
import com.nnmedia.novel.R;
import com.nnmedia.novel.RxHttp;
import com.nnmedia.read.cache.UserInfoCache;
import com.nnmedia.read.contact.Consts;
import com.nnmedia.read.entity.FUser;
import com.nnmedia.read.entity.umeng.WXLoginRespEntity;
import com.nnmedia.read.rxhttp.OnError;
import com.nnmedia.read.ui.base.BaseActivity;
import com.nnmedia.read.utils.Tos;
import com.rxjava.rxlife.RxLife;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

public class AccountSecurityActivity extends BaseActivity implements View.OnClickListener {
    public static final int REQCODE_ATTR_MODIFY = 0x1554;

    private TextView tvAccountId, tvTelNum, tvWechatBindingState;

    //手机号码
    private String phoneNum;

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

    }

    @Override
    public void setListener() {
        findViewById(R.id.rtl_asBtn_user_cellphone_number).setOnClickListener(this);
        findViewById(R.id.rtl_asBtn_user_wechat).setOnClickListener(this);
        findViewById(R.id.tv_asBtn_account_security).setOnClickListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {
        showUserInfo();
    }

    private void showUserInfo() {
        FUser fUser = UserInfoCache.getUserInfo(this);
        phoneNum = fUser.getMobile();
//        .replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2")
        tvAccountId.setText(fUser.getId());
        tvTelNum.setText(phoneNum);
        tvWechatBindingState.setText(fUser.isIs_wx() ? R.string.txt_has_been_bind : R.string.txt_unbind);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQCODE_ATTR_MODIFY) {
                showUserInfo();
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rtl_asBtn_user_cellphone_number:
//                if (TextUtils.isEmpty(phoneNum)) {
//                    //手机号为空（即 微信登录）
//                    startActivityForResult(new Intent(this, WXBindingPhoneActivity.class), REQCODE_ATTR_MODIFY);
//                } else {
//                    //手机号不为空
//                    startActivityForResult(new Intent(this, ChangeBindingActivity.class), REQCODE_ATTR_MODIFY);
//                }
                break;
            case R.id.rtl_asBtn_user_wechat:
//                if (!UserInfoCache.getIsBindWechat(this) && !TextUtils.isEmpty(phoneNum)) {
//                    reqAuthCode(phoneNum);
//                }
                break;
            case R.id.tv_asBtn_account_security:
                VerifyPasswordActivity.start(this);
                break;
            default:
                break;
        }
    }


    /**
     * 手机号获取验证码
     *
     * @param tel
     */
    private void reqAuthCode(String tel) {
        RxHttp.postForm(Consts.SMS_SEND_API)
                .add(Consts.MOBILE, tel)
                .add(Consts.EVENT, Consts.SMS_LOGIN)
                .asResponse(String.class)
                .doOnSubscribe(disposable -> showLoadingDialog())
                .doFinally(this::dismissLoadingDialog)
                .to(RxLife.toMain(this))
                .subscribe(s -> {
                    String titleStr = getString(R.string.txt_title_bind_wechat);
                    String contentStr = String.format(getString(R.string.txt_plz_input_verify_code_form_phone_x), tel.replaceAll("(\\d{3})\\d{4}(\\d{4})",
                            "$1****$2"));
                    String okStr = getString(R.string.txt_confirm);
                    String cancelStr = getString(R.string.txt_cancel);
                    InputDialog.show(AccountSecurityActivity.this, titleStr, contentStr, okStr, cancelStr)
                            .setInputInfo(new InputInfo()
                                    .setTextInfo(new TextInfo().setFontSize(16))
                                    .setMAX_LENGTH(6)               //限制最大输入长度
                                    .setMultipleLines(false)        //是否支持多行输入
                            )
                            .setCancelable(false)
                            .setOnOkButtonClickListener((baseDialog, v1, inputStr) -> {
                                String verifyCode = inputStr.trim();
                                if (TextUtils.isEmpty(verifyCode)) {
                                    Tos.showShort(AccountSecurityActivity.this, R.string.txt_plz_input_verification_code);
                                    return true;
                                }
                                return false;
                            });
                }, (OnError) error -> Tos.showShort(AccountSecurityActivity.this, error.getErrorMsg()));
    }


    /**
     * 绑定微信
     */
    private void bindWechat(WXLoginRespEntity wxLoginResp, String verifyCode) {
        RxHttp.postForm(Consts.BINDING_WECHAT_API)
                .add(Consts.UNIONID, wxLoginResp.getUnionid())
                .add(Consts.OPENID, wxLoginResp.getOpenid())
                .add(Consts.MOBILE_CAPTCHA, verifyCode)
                .asResponse(String.class)
                .doOnSubscribe(disposable -> showLoadingDialog())
                .doFinally(this::dismissLoadingDialog)
                .to(RxLife.toMain(this))
                .subscribe(s -> {
                    UserInfoCache.saveIsBindWechat(AccountSecurityActivity.this, true);
                    //改变ui信息
                    tvWechatBindingState.setText(R.string.txt_has_been_bind);
                    TipDialog.show(AccountSecurityActivity.this, R.string.txt_binding_success, TipDialog.TYPE.SUCCESS);
                }, (OnError) error -> TipDialog.show(AccountSecurityActivity.this, error.getErrorMsg(), TipDialog.TYPE.ERROR));
    }

}
