package com.nnmedia.read.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.kongzue.dialog.v3.TipDialog;
import com.nnmedia.novel.R;
import com.nnmedia.novel.RxHttp;
import com.nnmedia.read.FrApp;
import com.nnmedia.read.cache.TokenCache;
import com.nnmedia.read.cache.UserInfoCache;
import com.nnmedia.read.contact.Consts;
import com.nnmedia.read.entity.FUser;
import com.nnmedia.read.entity.LoginRpsEntity;
import com.nnmedia.read.rxhttp.OnError;
import com.nnmedia.read.ui.base.BaseActivity;
import com.nnmedia.read.utils.Tos;
import com.nnmedia.read.utils.UniqueIdManager;
import com.rxjava.rxlife.RxLife;

import org.greenrobot.eventbus.EventBus;

public class ChangePasswordActivity extends BaseActivity implements View.OnClickListener {

    private ImageView ivClose;
    private Button btnConfirm;

    private EditText etPassword_1, etPassword_2;

    private String old_pwd;

    public static void start(Context context, String pwd) {
        Intent starter = new Intent(context, ChangePasswordActivity.class);
        starter.putExtra("pwd", pwd);
        context.startActivity(starter);
    }

    @Override
    public void initParms(Bundle parms) {
        old_pwd = parms.getString("pwd");
    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_change_password;
    }

    @Override
    public void initView(View view) {
        ivClose = $(R.id.iv_asBtn_close);
        btnConfirm = $(R.id.btn_confirm);
        etPassword_1 = $(R.id.et_password_1);
        etPassword_2 = $(R.id.et_password_2);
    }

    @Override
    public void setListener() {
        ivClose.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_asBtn_close:
                onBackPressed();
                break;
            case R.id.btn_confirm:
                String phoneNum = UserInfoCache.getUserInfo(this).getMobile();
                String pwd1 = etPassword_1.getText().toString().trim();
                String pwd2 = etPassword_2.getText().toString().trim();
                if (pwd1.isEmpty() || pwd2.isEmpty() || pwd1.length() < 6 || pwd2.length() < 6) {
                    Tos.showShort(this, "请输入正确的密码");
                    return;
                }
                if (!pwd1.equals(pwd2)) {
                    Tos.showShort(this, "两次密码不一致");
                    return;
                }
                changePwd(phoneNum, old_pwd, pwd1, pwd2);
                break;
        }
    }

    /**
     * 修改密码
     *
     * @param tel          手机号
     * @param old_password 老密码
     * @param password     新密码
     * @param re_password  确认密码
     */
    private void changePwd(String tel, String old_password, String password, String re_password) {
        RxHttp.postForm(Consts.USER_CHANGE_PASSWORD_API)
                .add(Consts.MOBILE, tel)
                .add(Consts.OLD_PASSWORD, old_password)
                .add(Consts.PASSWORD, password)
                .add(Consts.RE_PASSWORD, re_password)
                .asResponse(String.class)
                .to(RxLife.toMain(this))
                .subscribe(string -> {
                    TipDialog.show(ChangePasswordActivity.this, "修改成功，重新登录！", TipDialog.TYPE.SUCCESS)
                            .setOnDismissListener(() -> {
                                setResult(RESULT_OK);
                                logout();
                            });
                }, (OnError) error -> TipDialog.show(ChangePasswordActivity.this, error.getErrorMsg(), TipDialog.TYPE.ERROR));
    }

    /**
     * 退出登录
     */
    private void logout() {
        RxHttp.postForm(Consts.USER_LOGOUT_API) //发送登出请求
                .asResponse(LoginRpsEntity.class)
                .flatMap(loginRpsEntity -> {
                    UserInfoCache.clearCache(ChangePasswordActivity.this);
                    TokenCache.saveToken(ChangePasswordActivity.this, loginRpsEntity.getToken());
                    String uniqueID = UniqueIdManager.getUniqueID(this);
                    //登出成功，得到新的Token去获取游客身份信息，并返回User对象
                    return RxHttp.postForm(Consts.USERS_INFO_API) //发送登录请求
                            .add(Consts.UNIQUE_ID, uniqueID)
                            .subscribeOnCurrent() //当前线程发送登录请求
                            .asResponse(FUser.class);
                })
                .doOnSubscribe(disposable -> showLoadingDialog())
                .doFinally(this::dismissLoadingDialog)
                .to(RxLife.toMain(this))  //感知生命周期，并在主线程回调
                .subscribe(fUser -> {
                    UserInfoCache.saveUserInfo(ChangePasswordActivity.this, fUser);
                    EventBus.getDefault().postSticky(fUser);
                    FrApp.getInstance().mActivityManager.finishAllActivityExcept(MainActivity.class);
                    LoginActivity2.start(ChangePasswordActivity.this);
                }, (OnError) error -> TipDialog.show(this, error.getErrorMsg(), TipDialog.TYPE.ERROR));
    }
}