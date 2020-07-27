package com.huli.foxread.ui.activities;

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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huli.foxread.R;
import com.huli.foxread.RxHttp;
import com.huli.foxread.cache.TokenCache;
import com.huli.foxread.cache.UserInfoCache;
import com.huli.foxread.contact.Common;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.entity.FUser;
import com.huli.foxread.entity.LoginRpsEntity;
import com.huli.foxread.entity.umeng.WXLoginRespEntity;
import com.huli.foxread.rxhttp.OnError;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.ui.widget.TimingButton;
import com.huli.foxread.utils.SPFUtils;
import com.huli.foxread.utils.StatusBarUtils;
import com.huli.foxread.utils.Tos;
import com.huli.foxread.utils.UniqueIdManager;
import com.kongzue.dialog.v3.MessageDialog;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.rxjava.rxlife.RxLife;
import com.sh.sdk.shareinstall.autologin.AutoLoginManager;
import com.sh.sdk.shareinstall.autologin.listener.AvoidPwdLoginListener;
import com.sh.sdk.shareinstall.autologin.listener.PreGetNumberListener;
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
    private TextView btnOneClickLogin;

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
    }

    @Override
    public void doBusiness(Context mContext) {
        AutoLoginManager.getInstance().preAvoidPwdLogin(new PreGetNumberListener() {
            @Override
            public void onPreGetNumberSuccess(String secureMobile) {
                // 取号成功，请引导用户进行一键登录，优化用户体验
                btnOneClickLogin.setVisibility(View.VISIBLE);
                Log.e(TAG, "预取号成功：" + secureMobile);
            }

            @Override
            public void onPreGetNumberError(final String msg) {
                // 取号失败，请隐藏一键登陆按钮，优化用户体验
                Log.e(TAG, "预取号失败：" + msg);
            }
        });


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
                Intent intent = new Intent(LoginActivity.this, CommonWebActivity.class);
                intent.putExtra(Common.KEY_URL, Consts.PRIVACY_POLICY_URL);
                startActivity(intent);
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
            case R.id.tv_asBtn_login_one_click:
                preAvoidPwd1ClickLogin();
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
        RxHttp.get(Consts.USERS_INFO_API)
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
     * 一键登录预取号
     */
    private void preAvoidPwd1ClickLogin() {
        AutoLoginManager.getInstance().preAvoidPwdLogin(new PreGetNumberListener() {
            @Override
            public void onPreGetNumberSuccess(String secureMobile) {
                Log.e(TAG, "预取号成功：" + secureMobile);
                oneClickLogin();
            }

            @Override
            public void onPreGetNumberError(final String msg) {
                Log.e(TAG, "预取号失败：" + msg);
            }
        });
    }

    /**
     * 一键登录 调起
     */
    private void oneClickLogin() {
        AutoLoginManager.getInstance().doAvoidPwdLogin(this, new AvoidPwdLoginListener() {
            @Override
            public void onGetLoginTokenSuccess(String operatorType, String token, String secureMobile) {
//                Log.e(TAG, "operatorType>>" + operatorType + ">>token>>" + token + ">>secureMobile>>" + secureMobile);
                /*
                 * operatorType 运营商类型  1电信 2移动 3联通
                 * token 移动联通为登录token    电信为accessCode
                 * secureMobile 移动联通为带星手机号  电信为authCode
                 */
                getPhoneNum(operatorType, token, secureMobile);
                AutoLoginManager.getInstance().closeOperatorActivity();
            }

            @Override
            public void onGetLoginTokenFaild(String operatorType, String code, final String errorMsg) {
                /*
                 *operatorType 运营商类型  1电信 2移动 3联通
                 * code  1001用户关闭授权页取消授权 1002其他错误
                 * errorMsg 错误消息
                 * errorResultCode 运营商返回的错误码
                 */
//                Log.e(TAG, "获取授权码失败：" + errorMsg);
                AutoLoginManager.getInstance().closeOperatorActivity();
            }

            @Override
            public void onOtherWayLogin() {
                Log.e(TAG, "点击其他登录方式");
                AutoLoginManager.getInstance().closeOperatorActivity();
            }
        });
    }


    /**
     * 通过运营商一键登录返回的token 调用自身登录
     *
     * @param operatorType 运营商类型  1电信 2移动 3联通
     * @param uToken       电信运营商的token
     * @param authCode     移动联通为带星手机号  电信为authCode
     */
    private void getPhoneNum(String operatorType, String uToken, String authCode) {
        WaitDialog.show(LoginActivity.this, R.string.loading);
        String uniqueID = UniqueIdManager.getUniqueID(this);
        RxHttp.postForm(Consts.USE_PHONE_ONEKEY_LOGIN)
                .add(Consts.TYPE, operatorType)
                .add("authCode", operatorType.equals("1") ? authCode : "")
                .add(Consts.TOKEN, uToken)
                .add("plantFrom", "1")
                .add(Consts.UNIQUE_ID, uniqueID)
                .asResponse(LoginRpsEntity.class)
                .to(RxLife.toMain(this))  //感知生命周期，并在主线程回调
                .subscribe(data -> {
                    TokenCache.saveToken(LoginActivity.this, data.getToken());
                    reqUserInfo();
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