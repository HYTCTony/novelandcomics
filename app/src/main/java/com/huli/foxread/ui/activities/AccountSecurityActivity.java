package com.huli.foxread.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huli.foxread.R;
import com.huli.foxread.RxHttp;
import com.huli.foxread.cache.UserInfoCache;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.entity.FUser;
import com.huli.foxread.entity.umeng.WXLoginRespEntity;
import com.huli.foxread.rxhttp.OnError;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.utils.Tos;
import com.kongzue.dialog.util.InputInfo;
import com.kongzue.dialog.util.TextInfo;
import com.kongzue.dialog.v3.InputDialog;
import com.kongzue.dialog.v3.TipDialog;
import com.rxjava.rxlife.RxLife;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

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
        showUserInfo();
    }

    private void showUserInfo() {
        FUser fUser = UserInfoCache.getUserInfo(this);
        phoneNum = fUser.getMobile();
        tvAccountId.setText(fUser.getId());
        tvTelNum.setText(phoneNum.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2"));
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
                if (TextUtils.isEmpty(phoneNum)) {
                    //手机号为空（即 微信登录）
                    startActivityForResult(new Intent(this, WXBindingPhoneActivity.class), REQCODE_ATTR_MODIFY);
                } else {
                    //手机号不为空
                    startActivityForResult(new Intent(this, ChangeBindingActivity.class), REQCODE_ATTR_MODIFY);
                }
                break;
            case R.id.rtl_asBtn_user_wechat:
                if (!UserInfoCache.getIsBindWechat(this) && !TextUtils.isEmpty(phoneNum)) {
                    reqAuthCode(phoneNum);
                }
                break;
            case R.id.tv_asBtn_account_security:
                startActivity(new Intent(this, ASecurityActivity.class));
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
                    String contentStr = String.format(getString(R.string.txt_plz_input_verify_code_form_phone_x), tel.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2"));
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
                                //获取微信返回信息
                                getWechatAuthorizationInfo(verifyCode);
                                return false;
                            });
                }, (OnError) error -> Tos.showShort(AccountSecurityActivity.this, error.getErrorMsg()));
    }

    /**
     * 获取微信返回信息
     *
     * @param verifyCode 手机验证码
     */
    private void getWechatAuthorizationInfo(String verifyCode) {
        UMShareAPI.get(AccountSecurityActivity.this).getPlatformInfo(AccountSecurityActivity.this, SHARE_MEDIA.WEIXIN, new UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {

            }

            @Override
            public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                String jsonString = JSON.toJSONString(map);
                WXLoginRespEntity wxLoginResp = JSONObject.parseObject(jsonString, WXLoginRespEntity.class);
                bindWechat(wxLoginResp, verifyCode);
            }

            @Override
            public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                TipDialog.show(AccountSecurityActivity.this, "微信授权失败！", TipDialog.TYPE.ERROR);
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media, int i) {
                Tos.showShort(AccountSecurityActivity.this, "取消微信授权...");
            }
        });
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
