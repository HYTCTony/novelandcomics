package com.nnmedia.read.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.nnmedia.novel.R;
import com.nnmedia.novel.RxHttp;
import com.nnmedia.read.contact.Consts;
import com.nnmedia.read.rxhttp.OnError;
import com.nnmedia.read.ui.base.BaseActivity;
import com.nnmedia.read.utils.Tos;
import com.nnmedia.read.utils.UniqueIdManager;
import com.rxjava.rxlife.RxLife;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    private ImageView ivClose;
    private Button btnRegister;

    private EditText etPhoneNumber, etPassword_1, etPassword_2, etDistribution;

    public static void start(Context context) {
        Intent starter = new Intent(context, RegisterActivity.class);
        context.startActivity(starter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_asBtn_close:
                onBackPressed();
                break;
            case R.id.btn_register:
                String phoneNum = etPhoneNumber.getText().toString();
                String pwd1 = etPassword_1.getText().toString();
                String pwd2 = etPassword_2.getText().toString();
                String distribution = etDistribution.getText().toString();
                if (TextUtils.isEmpty(phoneNum)) {
                    Tos.showShort(this, R.string.txt_plz_input_phone_number);
                    return;
                }
                if (TextUtils.isEmpty(pwd1)) {
                    Tos.showShort(this, R.string.txt_plz_input_password);
                    return;
                }
                if (TextUtils.isEmpty(pwd2)) {
                    Tos.showShort(this, R.string.txt_plz_comfirm_password);
                    return;
                }
                if (!pwd2.equals(pwd1)) {
                    Tos.showShort(this, R.string.txt_plz_verification_password);
                    return;
                }
                if (phoneNum.length() < 6 || phoneNum.length() > 11) {
                    Tos.showShort(this, "请注意您的用户名长度");
                    return;
                }
                if (pwd1.length() < 6) {
                    Tos.showShort(this, "请注意您的密码长度");
                    return;
                }
                register(phoneNum, pwd1, pwd2, distribution);
                break;
        }
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
        return R.layout.activity_register;
    }

    @Override
    public void initView(View view) {
        ivClose = $(R.id.iv_asBtn_close);
        btnRegister = $(R.id.btn_register);
        etPhoneNumber = $(R.id.et_phone_number);
        etPassword_1 = $(R.id.et_password_1);
        etPassword_2 = $(R.id.et_password_2);
        etDistribution = $(R.id.et_distribution);
    }

    @Override
    public void setListener() {
        ivClose.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {

    }

    /**
     * 用户注册
     *
     * @param tel          手机号
     * @param password     密码
     * @param re_password  确认密码
     * @param distribution 邀请码
     */
    private void register(String tel, String password, String re_password, String distribution) {
        WaitDialog.show(RegisterActivity.this, "注册中，请稍后！");
        String uniqueID = UniqueIdManager.getUniqueID(this);
        RxHttp.postForm(Consts.USER_REGISTER_API)
                .add(Consts.MOBILE, tel)
                .add(Consts.PASSWORD, password)
                .add(Consts.RE_PASSWORD, re_password)
                .add(Consts.DISTRIBUTION, distribution)
                .add(Consts.UNIQUE_ID, uniqueID)
                .asResponse(String.class)
                .to(RxLife.toMain(this))
                .subscribe(string -> {
                    TipDialog.show(RegisterActivity.this, "注册成功！", TipDialog.TYPE.SUCCESS)
                            .setOnDismissListener(() -> {
                                setResult(RESULT_OK);
                                finish();
                            });
                }, (OnError) error -> TipDialog.show(RegisterActivity.this, error.getErrorMsg(), TipDialog.TYPE.ERROR));
    }
}