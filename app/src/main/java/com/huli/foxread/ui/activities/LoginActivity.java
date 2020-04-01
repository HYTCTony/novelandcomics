package com.huli.foxread.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.huli.foxread.R;
import com.huli.foxread.cache.TokenCache;
import com.huli.foxread.cache.UserInfoCache2;
import com.huli.foxread.callbacks.ookkggoo.LtbCallback;
import com.huli.foxread.callbacks.ookkggoo.LtbJsonCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Common;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.entity.FUser;
import com.huli.foxread.entity.LoginRpsEntity;
import com.huli.foxread.entity.umeng.WXLoginRespEntity;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.ui.widget.TimingButton;
import com.huli.foxread.utils.StatusBarUtils;
import com.huli.foxread.utils.Tos;
import com.huli.foxread.utils.UniqueIdManager;
import com.kongzue.dialog.v3.MessageDialog;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private ImageView btnCloseLoginPage;
    private TimingButton tBtnVCode;
    private TextView tvAgreement;
    private Button btnLogin;
    private TextView btnLoginWechat;

    private EditText etPhoneNum, etAuthCode;

    @Override
    protected void setStatusBar() {
        StatusBarUtils.setColor(this, ContextCompat.getColor(this, R.color.col_f8f8f8), 0);
        StatusBarUtils.setAndroidNativeLightStatusBar(this, true);
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
        return R.layout.activity_login;
    }

    @Override
    public void initView(View view) {
        btnCloseLoginPage = $(R.id.iv_asBtn_close_login_page);
        tBtnVCode = $(R.id.timingButton_get_vCode);
        tvAgreement = $(R.id.tv_login_agreement);
        btnLogin = $(R.id.btn_login_cellphone);
        btnLoginWechat = $(R.id.tv_asBtn_login_wechat);

        etPhoneNum = $(R.id.et_phone_number);
        etAuthCode = $(R.id.et_login_authCode);
    }

    @Override
    public void setListener() {
        btnCloseLoginPage.setOnClickListener(this);
        tBtnVCode.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnLoginWechat.setOnClickListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {
        SpannableString spannableString = new SpannableString(getString(R.string.txt_agree_login_agreement));
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
//                Tos.showShort(LoginActivity.this, "用户协议");
                Intent intent = new Intent(LoginActivity.this, CommonWebActivity.class);
                intent.putExtra(Common.KEY_URL, Consts.USER_AGREEMENT_URL);
                startActivity(intent);
            }

            //去除连接下划线
            @Override
            public void updateDrawState(TextPaint ds) {
                /**set textColor**/
//                ds.setColor(ds.linkColor);
                ds.setColor(ContextCompat.getColor(LoginActivity.this, R.color.txt_red));
                /** the underline**/
                ds.setUnderlineText(true);
            }
        }, 11, 15, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
//                Tos.showShort(LoginActivity.this, "隐私策略");
                Intent intent = new Intent(LoginActivity.this, CommonWebActivity.class);
                intent.putExtra(Common.KEY_URL, Consts.PRIVACY_POLICY_URL);
                startActivity(intent);
            }

            //去除连接下划线
            @Override
            public void updateDrawState(TextPaint ds) {
                /**set textColor**/
//                ds.setColor(ds.linkColor);
                ds.setColor(ContextCompat.getColor(LoginActivity.this, R.color.txt_red));
                /** the underline**/
                ds.setUnderlineText(true);
            }
        }, 16, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tvAgreement.setMovementMethod(LinkMovementMethod.getInstance());//不设置 没有点击事件
        tvAgreement.setHighlightColor(ContextCompat.getColor(LoginActivity.this, R.color.transparent));
        tvAgreement.setText(spannableString);
    }

    @Override
    public void onClick(View view) {
        String phoneNum;
        switch (view.getId()) {
            case R.id.iv_asBtn_close_login_page:
                onBackPressed();
                break;
            case R.id.timingButton_get_vCode:
                phoneNum = etPhoneNum.getText().toString();
                if (TextUtils.isEmpty(phoneNum)) {
                    Tos.showShort(this, R.string.txt_plz_input_phone_number);
                    return;
                }
                tBtnVCode.start();
                reqAuthCode(phoneNum);
                break;
            case R.id.btn_login_cellphone:
                phoneNum = etPhoneNum.getText().toString();
                String authCode = etAuthCode.getText().toString();
                if (TextUtils.isEmpty(phoneNum)) {
                    Tos.showShort(this, R.string.txt_plz_input_phone_number);
                    return;
                }
                if (TextUtils.isEmpty(authCode)) {
                    Tos.showShort(this, R.string.txt_plz_input_verification_code);
                    return;
                }
                loginPhone(phoneNum, authCode);
                break;
            case R.id.tv_asBtn_login_wechat:
                if (onMoreClick()) {
                    return;
                }
                UMShareAPI.get(this).getPlatformInfo(this, SHARE_MEDIA.WEIXIN, new UMAuthListener() {
                    @Override
                    public void onStart(SHARE_MEDIA share_media) {

                    }

                    @Override
                    public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                        String jsonString = JSON.toJSONString(map);
                        WXLoginRespEntity wxLoginResp = JSONObject.parseObject(jsonString, WXLoginRespEntity.class);
                        loginByWeChat(wxLoginResp);
                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                        TipDialog.show(LoginActivity.this, "微信登录失败！", TipDialog.TYPE.ERROR);
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media, int i) {
                        Tos.showShort(LoginActivity.this, "微信登录取消...");
                    }
                });
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
        OkGo.<String>post(Consts.SMS_SEND_API)
                .params(Consts.MOBILE, tel)
                .params(Consts.EVENT, Consts.SMS_LOGIN)
                .execute(new LtbCallback(this) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<String> entity = JSONObject.parseObject(response.body(), new TypeReference<LzyResponse<String>>() {
                        });
                        if (entity.error_code != 0) {
                            tBtnVCode.reset();
                        }
                        Tos.showShort(LoginActivity.this, entity.msg);
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        tBtnVCode.reset();
                    }
                });
    }


    /**
     * 手机号登录
     *
     * @param tel
     * @param authCode
     */
    private void loginPhone(String tel, String authCode) {
        String uniqueID = UniqueIdManager.getUniqueID(this);
        OkGo.<LzyResponse<LoginRpsEntity>>post(Consts.USER_MOBILE_LOGIN_API)
                .params(Consts.MOBILE, tel)
                .params(Consts.CAPTCHA, authCode)
                .params(Consts.UNIQUE_ID, uniqueID)
                .execute(new LtbJsonCallback<LzyResponse<LoginRpsEntity>>(this, false,
                        new TypeReference<LzyResponse<LoginRpsEntity>>() {
                        }) {
                    @Override
                    public void onSuccess(Response<LzyResponse<LoginRpsEntity>> response) {
                        if (response.body().error_code == 0) {
                            LoginRpsEntity data = response.body().getData();
                            TokenCache.saveToken(LoginActivity.this, data.getToken());

                            reqUserInfo();
                        } else {
                            TipDialog.show(LoginActivity.this, response.body().msg, TipDialog.TYPE.ERROR);
                        }
                    }
                });
    }

    /**
     * 微信第三方登录
     */
    private void loginByWeChat(WXLoginRespEntity wxLoginResp) {
        WaitDialog.show(LoginActivity.this, R.string.loading);
        String uniqueID = UniqueIdManager.getUniqueID(this);
        OkGo.<LzyResponse<LoginRpsEntity>>post(Consts.USER_WX_LOGIN_API)
                .params(Consts.USERNAME, wxLoginResp.getName())
                .params(Consts.UNIONID, wxLoginResp.getUnionid())
                .params(Consts.OPENID, wxLoginResp.getOpenid())
                .params(Consts.UNIQUE_ID, uniqueID)
                .execute(new LtbJsonCallback<LzyResponse<LoginRpsEntity>>(this, false,
                        new TypeReference<LzyResponse<LoginRpsEntity>>() {
                        }) {
                    @Override
                    public void onSuccess(Response<LzyResponse<LoginRpsEntity>> response) {
                        if (response.body().error_code == 0) {
                            LoginRpsEntity data = response.body().getData();
                            TokenCache.saveToken(LoginActivity.this, data.getToken());

                            //获取用户信息
                            reqUserInfo();
                        } else {
                            TipDialog.show(LoginActivity.this, response.body().msg, TipDialog.TYPE.ERROR);
                        }
                    }

                    @Override
                    public void onError(Response<LzyResponse<LoginRpsEntity>> response) {
                        super.onError(response);
                        TipDialog.dismiss();
                    }
                });
    }

    /**
     * 获取用户信息
     */
    private void reqUserInfo() {
        OkGo.<LzyResponse<FUser>>get(Consts.USERS_INFO_API)
                .execute(new LtbJsonCallback<LzyResponse<FUser>>(this, false,
                        new TypeReference<LzyResponse<FUser>>() {
                        }) {
                    @Override
                    public void onSuccess(Response<LzyResponse<FUser>> response) {
                        int errorCode = response.body().error_code;
                        if (errorCode == 0) {
                            FUser data = response.body().getData();
                            UserInfoCache2.saveUserInfo(LoginActivity.this, data);
                            EventBus.getDefault().postSticky(data);

                            if (!TextUtils.isEmpty(data.getMobile())) {     //微信登录，且绑定手机号、 或者直接手机号登录
                                TipDialog.show(LoginActivity.this, R.string.txt_login_success, TipDialog.TYPE.SUCCESS)
                                        .setOnDismissListener(() -> finish());
                            } else {        //微信登录，且没绑定手机号
                                TipDialog.dismiss();
                                MessageDialog.show(LoginActivity.this, R.string.txt_login_success, R.string.txt_binding_cellphone_hint, R.string.txt_go2_binding, R.string.txt_withhold)
                                        .setOnOkButtonClickListener((baseDialog, v) -> {
                                            // 去绑定
                                            Intent intent = new Intent(LoginActivity.this, WXBindingPhoneActivity.class);
                                            startActivity(intent);
                                            finish();
                                            return false;
                                        })
                                        .setOnCancelButtonClickListener((baseDialog, v) -> {
                                            finish();
                                            return false;
                                        });
                            }
                        } else {
                            TipDialog.show(LoginActivity.this, response.body().msg, TipDialog.TYPE.ERROR);
                        }
                    }

                    @Override
                    public void onError(Response<LzyResponse<FUser>> response) {
                        super.onError(response);
                        TipDialog.dismiss();
                    }
                });
    }
}
