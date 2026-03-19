package com.nnmedia.read.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.nnmedia.novel.R;
import com.nnmedia.novel.RxHttp;
import com.nnmedia.read.cache.AccountCache;
import com.nnmedia.read.cache.TokenCache;
import com.nnmedia.read.cache.UserInfoCache;
import com.nnmedia.read.contact.Common;
import com.nnmedia.read.contact.Consts;
import com.nnmedia.read.ebsevent.VipChargerEvent;
import com.nnmedia.read.entity.FUser;
import com.nnmedia.read.entity.LoginRpsEntity;
import com.nnmedia.read.rxhttp.OnError;
import com.nnmedia.read.ui.base.BaseActivity;
import com.nnmedia.read.utils.AESCBCUtil;
import com.nnmedia.read.utils.SPFUtils;
import com.nnmedia.read.utils.StatusBarUtils;
import com.nnmedia.read.utils.Tos;
import com.nnmedia.read.utils.UniqueIdManager;
import com.nnmedia.read.utils.VerifyDevice;
import com.orhanobut.logger.Logger;
import com.rxjava.rxlife.RxLife;

import org.greenrobot.eventbus.EventBus;

import androidx.core.content.ContextCompat;

public class LoginActivity2 extends BaseActivity implements View.OnClickListener {

    private ImageView btnCloseLoginPage;
    private Button btnLogin;
    private TextView btnRegister;

    private EditText etPhoneNum, etPassword;

    public static final int REQCODE_LOGIN = 0x1688;
    public static final int REQCODE_LOGIN2 = 0x9527;

    public static void start4Result(Activity context, int reqCode) {
        Intent starter = new Intent(context, LoginActivity2.class);
        context.startActivityForResult(starter, reqCode);
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, LoginActivity2.class);
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
        return R.layout.activity_login2;
    }

    @Override
    public void initView(View view) {
        btnCloseLoginPage = $(R.id.iv_asBtn_close_login_page);
        btnLogin = $(R.id.btn_login_cellphone);
        btnRegister = $(R.id.btn_register);

        etPhoneNum = $(R.id.et_phone_number);
        etPassword = $(R.id.et_password);
    }

    @Override
    public void setListener() {
        btnCloseLoginPage.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void onClick(View view) {
        String phoneNum;
        switch (view.getId()) {
            case R.id.iv_asBtn_close_login_page:
                onBackPressed();
                break;
            case R.id.btn_login_cellphone:
                phoneNum = etPhoneNum.getText().toString();
                String pwd = etPassword.getText().toString();
                if (TextUtils.isEmpty(phoneNum)) {
                    Tos.showShort(this, R.string.txt_plz_input_phone_number);
                    return;
                }
                if (TextUtils.isEmpty(pwd)) {
                    Tos.showShort(this, R.string.txt_plz_input_password);
                    return;
                }
                loginPhone(phoneNum, pwd);
                break;
            case R.id.btn_register:
                RegisterActivity.start(this);
                break;
            default:
                break;
        }
    }

    /**
     * 账号密码登录
     *
     * @param tel      手机号
     * @param password 密码
     */
    private void loginPhone(String tel, String password) {
        WaitDialog.show(LoginActivity2.this, "正在登录！");
        String uniqueID = UniqueIdManager.getUniqueID(this);
        RxHttp.postForm(Consts.USER_PASSWORD_LOGIN_API)
                .add(Consts.MOBILE, tel)
                .add(Consts.PASSWORD, password)
                .add(Consts.UNIQUE_ID, uniqueID)
                .add(Consts.SIMULATOR, VerifyDevice.verify() ? 1 : 0)
                .asResponse(LoginRpsEntity.class)
                .to(RxLife.toMain(this))
                .subscribe(loginRpsEntity -> {
                    SPFUtils.put(LoginActivity2.this, Common.KEY_FIRST_LOGIN, true);
                    TokenCache.saveToken(LoginActivity2.this, loginRpsEntity.getToken());
                    String account = AESCBCUtil.encrypt(tel + "&" + password, AESCBCUtil.key, AESCBCUtil.iv);
                    Logger.e("AES:" + account);
                    AccountCache.saveAccout(LoginActivity2.this, account);
                    reqUserInfo();
                }, (OnError) error -> TipDialog.show(LoginActivity2.this, error.getErrorMsg(), TipDialog.TYPE.ERROR));
    }

    /**
     * 获取用户信息
     */
    private void reqUserInfo() {
        String uniqueID = UniqueIdManager.getUniqueID(this);
        RxHttp.postForm(Consts.USERS_INFO_API)
                .add(Consts.UNIQUE_ID, uniqueID)
                .asResponse(FUser.class)
                .to(RxLife.toMain(this))
                .subscribe(fUser -> {
                    UserInfoCache.saveUserInfo(LoginActivity2.this, fUser);
                    if (UserInfoCache.getIsVip(LoginActivity2.this))
                        EventBus.getDefault().postSticky(new VipChargerEvent(true));
                    EventBus.getDefault().postSticky(fUser);
                    TipDialog.show(LoginActivity2.this, R.string.txt_login_success, TipDialog.TYPE.SUCCESS)
                            .setOnDismissListener(() -> {
                                setResult(RESULT_OK);
                                finish();
                            });
                }, (OnError) error -> {
                    TipDialog.show(LoginActivity2.this, error.getErrorMsg(), TipDialog.TYPE.ERROR);
                });
    }

}