package com.nnmedia.read.ui.activities;

import android.app.Activity;
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

import com.kongzue.dialog.v3.MessageDialog;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.nnmedia.novel.R;
import com.nnmedia.novel.RxHttp;
import com.nnmedia.read.cache.TokenCache;
import com.nnmedia.read.cache.UserInfoCache;
import com.nnmedia.read.contact.Common;
import com.nnmedia.read.contact.Consts;
import com.nnmedia.read.entity.FUser;
import com.nnmedia.read.entity.LoginRpsEntity;
import com.nnmedia.read.entity.umeng.WXLoginRespEntity;
import com.nnmedia.read.rxhttp.OnError;
import com.nnmedia.read.ui.base.BaseActivity;
import com.nnmedia.read.ui.widget.TimingButton;
import com.nnmedia.read.utils.SPFUtils;
import com.nnmedia.read.utils.StatusBarUtils;
import com.nnmedia.read.utils.Tos;
import com.nnmedia.read.utils.UniqueIdManager;
import com.rxjava.rxlife.RxLife;

import org.greenrobot.eventbus.EventBus;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private ImageView btnCloseLoginPage;
    private TimingButton tBtnVCode;
    private TextView tvAgreement;
    private Button btnLogin;
    private TextView btnLoginWechat;
    private TextView btnOneClickLogin;
    private TextView btnRegister;

    private EditText etPhoneNum, etAuthCode;

    public static final int REQCODE_LOGIN = 0x1688;

    public static void start4Result(Activity context, int reqCode) {
        Intent starter = new Intent(context, LoginActivity.class);
        context.startActivityForResult(starter, reqCode);
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, LoginActivity.class);
        context.startActivity(starter);
    }

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
        btnOneClickLogin = $(R.id.tv_asBtn_login_one_click);
        btnRegister = $(R.id.btn_register);

        etPhoneNum = $(R.id.et_phone_number);
        etAuthCode = $(R.id.et_login_authCode);
    }

    @Override
    public void setListener() {
        btnCloseLoginPage.setOnClickListener(this);
        tBtnVCode.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnLoginWechat.setOnClickListener(this);
        btnOneClickLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {

        SpannableString spannableString = new SpannableString(getString(R.string.txt_agree_login_agreement));
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
//                Tos.showShort(LoginActivity.this, "用户协议");
//                Intent intent = new Intent(LoginActivity.this, CommonWebActivity.class);
//                intent.putExtra(Common.KEY_URL, Consts.USER_AGREEMENT_URL);
//                startActivity(intent);
            }

            //去除连接下划线
            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                /*set textColor*/
//                ds.setColor(ds.linkColor);
                ds.setColor(ContextCompat.getColor(LoginActivity.this, R.color.txt_red));
                /*the underline*/
                ds.setUnderlineText(false);
            }
        }, 11, 17, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
//                Tos.showShort(LoginActivity.this, "隐私策略");
//                Intent intent = new Intent(LoginActivity.this, CommonWebActivity.class);
//                intent.putExtra(Common.KEY_URL, Consts.PRIVACY_POLICY_URL);
//                startActivity(intent);
            }

            //去除连接下划线
            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                /*set textColor*/
//                ds.setColor(ds.linkColor);
                ds.setColor(ContextCompat.getColor(LoginActivity.this, R.color.txt_red));
                /* the underline*/
                ds.setUnderlineText(false);
            }
        }, 18, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
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
            case R.id.btn_register:
                RegisterActivity.start(this);
                break;
            case R.id.tv_asBtn_login_wechat:
//                if (onMoreClick()) {
//                    return;
//                }
//                UMShareAPI.get(this).getPlatformInfo(this, SHARE_MEDIA.WEIXIN, new UMAuthListener() {
//                    @Override
//                    public void onStart(SHARE_MEDIA share_media) {
//
//                    }
//
//                    @Override
//                    public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
//                        String jsonString = JSON.toJSONString(map);
//                        WXLoginRespEntity wxLoginResp = JSONObject.parseObject(jsonString, WXLoginRespEntity.class);
//                        loginByWeChat(wxLoginResp);
//                    }
//
//                    @Override
//                    public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
//                        TipDialog.show(LoginActivity.this, "微信登录失败！", TipDialog.TYPE.ERROR);
//                    }
//
//                    @Override
//                    public void onCancel(SHARE_MEDIA share_media, int i) {
//                        Tos.showShort(LoginActivity.this, "微信登录取消...");
//                    }
//                });
                break;
            case R.id.tv_asBtn_login_one_click:
//                preAvoidPwd1ClickLogin();
                break;
            default:
                break;
        }
    }

    /**
     * 手机号获取验证码
     *
     * @param tel 手机号
     */
    private void reqAuthCode(String tel) {
        RxHttp.postForm(Consts.SMS_SEND_API)
                .add(Consts.MOBILE, tel)
                .add(Consts.EVENT, Consts.SMS_LOGIN)
                .asResponse(String.class)
                .to(RxLife.toMain(this))
                .subscribe(s -> Tos.showShort(LoginActivity.this, R.string.txt_sms_verification_code_has_been_issued), (OnError) error -> {
                    tBtnVCode.reset();
                    Tos.showShort(LoginActivity.this, error.getErrorMsg());
                });
    }


    /**
     * 手机号登录
     *
     * @param tel      手机号
     * @param authCode 验证码
     */
    private void loginPhone(String tel, String authCode) {
        String uniqueID = UniqueIdManager.getUniqueID(this);
        RxHttp.postForm(Consts.USER_MOBILE_LOGIN_API)
                .add(Consts.MOBILE, tel)
                .add(Consts.CAPTCHA, authCode)
                .add(Consts.UNIQUE_ID, uniqueID)
                .asResponse(LoginRpsEntity.class)
                .to(RxLife.toMain(this))
                .subscribe(loginRpsEntity -> {
                    TokenCache.saveToken(LoginActivity.this, loginRpsEntity.getToken());
                    reqUserInfo();
                }, (OnError) error -> TipDialog.show(LoginActivity.this, error.getErrorMsg(), TipDialog.TYPE.ERROR));
    }

    /**
     * 微信第三方登录
     */
    private void loginByWeChat(WXLoginRespEntity wxLoginResp) {
        WaitDialog.show(LoginActivity.this, R.string.loading);
        String uniqueID = UniqueIdManager.getUniqueID(this);
        RxHttp.postForm(Consts.USER_WX_LOGIN_API)
                .add(Consts.USERNAME, wxLoginResp.getName())
                .add(Consts.UNIONID, wxLoginResp.getUnionid())
                .add(Consts.OPENID, wxLoginResp.getOpenid())
                .add(Consts.UNIQUE_ID, uniqueID)
                .asResponse(LoginRpsEntity.class)
                .to(RxLife.toMain(this))
                .subscribe(loginRpsEntity -> {
                    TokenCache.saveToken(LoginActivity.this, loginRpsEntity.getToken());
                    //获取用户信息
                    reqUserInfo();
                }, (OnError) error -> TipDialog.show(LoginActivity.this, error.getErrorMsg(), TipDialog.TYPE.ERROR));
    }

    /**
     * 获取用户信息
     */
    private void reqUserInfo() {
        String uniqueID = UniqueIdManager.getUniqueID(this);
        RxHttp.get(Consts.USERS_INFO_API)
                .add(Consts.UNIQUE_ID, uniqueID)
                .asResponse(FUser.class)
                .to(RxLife.toMain(this))
                .subscribe(fUser -> {
                    UserInfoCache.saveUserInfo(LoginActivity.this, fUser);
                    EventBus.getDefault().postSticky(fUser);
                    //是否已经填写邀请码
                    boolean isInvited = fUser.isIs_invited();
                    String inviteCode = (String) SPFUtils.get(LoginActivity.this, Common.INVITE_CODE, "");
                    if (!isInvited && !TextUtils.isEmpty(inviteCode)) {
                        reqInviteCodeSubmit(inviteCode);
                    }

                    if (!TextUtils.isEmpty(fUser.getMobile())) {     //微信登录，且绑定手机号、 或者直接手机号登录
                        TipDialog.show(LoginActivity.this, R.string.txt_login_success, TipDialog.TYPE.SUCCESS)
                                .setOnDismissListener(() -> {
                                    setResult(RESULT_OK);
                                    finish();
                                });
                    } else {        //微信登录，且没绑定手机号
                        TipDialog.dismiss();
                        MessageDialog.show(LoginActivity.this, R.string.txt_login_success, R.string.txt_binding_cellphone_hint,
                                R.string.txt_go2_binding, R.string.txt_withhold)
                                .setOnOkButtonClickListener((baseDialog, v) -> {
                                    // 去绑定
                                    Intent intent = new Intent(LoginActivity.this, WXBindingPhoneActivity.class);
                                    startActivity(intent);
                                    setResult(RESULT_OK);
                                    finish();
                                    return false;
                                })
                                .setOnCancelButtonClickListener((baseDialog, v) -> {
                                    setResult(RESULT_OK);
                                    finish();
                                    return false;
                                });
                    }
                }, (OnError) error -> TipDialog.show(LoginActivity.this, error.getErrorMsg(), TipDialog.TYPE.ERROR));
    }

    /**
     * 提交邀请码
     * token在CallBack中统一加到header
     *
     * @param inviteCode 邀请码
     */
    private void reqInviteCodeSubmit(String inviteCode) {
        RxHttp.postForm(Consts.FILLIN_INVITE_CODE_API)
                .add(Consts.CODE, inviteCode)
                .asResponse(String.class)
                .to(RxLife.toMain(this))
                .subscribe(s -> {
                    SPFUtils.remove(LoginActivity.this, Common.INVITE_CODE);
                    UserInfoCache.saveIsInvited(LoginActivity.this, true);
                });
    }
}