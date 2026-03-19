package com.nnmedia.read.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.kongzue.dialog.v3.TipDialog;
import com.nnmedia.novel.R;
import com.nnmedia.novel.RxHttp;
import com.nnmedia.read.cache.UserInfoCache;
import com.nnmedia.read.contact.Consts;
import com.nnmedia.read.rxhttp.OnError;
import com.nnmedia.read.ui.base.BaseActivity;
import com.nnmedia.read.ui.widget.TimingButton;
import com.nnmedia.read.utils.SomeMonitorEditText;
import com.nnmedia.read.utils.Tos;
import com.rxjava.rxlife.RxLife;

import androidx.appcompat.widget.Toolbar;

public class WXBindingPhoneActivity extends BaseActivity implements View.OnClickListener {

    private TimingButton tbtnGetVcode;
    private Button btnBinding;

    private EditText etPhoneNum, etAuthCode;

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_binding_cellphone_wx;
    }

    @Override
    public void initView(View view) {
        Toolbar toolbar = $(R.id.toolbar_normal);
        initToolBar(toolbar, R.string.txt_binding_cellphone);

        tbtnGetVcode = $(R.id.timingButton_get_vCode);
        btnBinding = $(R.id.btn_confirm_bind_newPhone);
        etPhoneNum = $(R.id.et_cellphone_number);
        etAuthCode = $(R.id.et_auth_code);
        new SomeMonitorEditText(btnBinding, etPhoneNum, etAuthCode);
    }

    @Override
    public void setListener() {
        tbtnGetVcode.setOnClickListener(this);
        btnBinding.setOnClickListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {
        if (!TextUtils.isEmpty(UserInfoCache.getMobile(mContext))) {
            TipDialog.show(this, "您已绑定手机号！", TipDialog.TYPE.WARNING)
                    .setOnDismissListener(this::finish);
        }
    }

    @Override
    public void onClick(View view) {
        String phoneNum = "";
        switch (view.getId()) {
            case R.id.timingButton_get_vCode:
                phoneNum = etPhoneNum.getText().toString();
                if (TextUtils.isEmpty(phoneNum)) {
                    Tos.showShort(this, R.string.txt_plz_input_phone_number);
                    return;
                }
                tbtnGetVcode.start();
                reqAuthCode(phoneNum);
                break;
            case R.id.btn_confirm_bind_newPhone:
                phoneNum = etPhoneNum.getText().toString();
                String authCode = etAuthCode.getText().toString();
                if (TextUtils.isEmpty(phoneNum)) {
                    Tos.showShort(this, R.string.txt_plz_input_new_phone_number);
                    return;
                }
                if (TextUtils.isEmpty(authCode)) {
                    Tos.showShort(this, R.string.txt_plz_input_verification_code);
                    return;
                }
                bindingPhone(phoneNum, authCode);
                break;
            default:
                break;
        }
    }


    /**
     * 手机号获取验证码
     * token 在LtbCallback中统一添加
     *
     * @param tel
     */
    private void reqAuthCode(String tel) {
        RxHttp.postForm(Consts.SMS_SEND_API)
                .add(Consts.MOBILE, tel)
                .add(Consts.EVENT, Consts.SMS_BIND)
                .asResponse(String.class)
                .doOnSubscribe(disposable -> showLoadingDialog())
                .doFinally(this::dismissLoadingDialog)
                .to(RxLife.toMain(this))
                .subscribe(s -> Tos.showShort(WXBindingPhoneActivity.this, R.string.txt_sms_verification_code_has_been_issued),
                        (OnError) error -> {
                            tbtnGetVcode.reset();
                            Tos.showShort(WXBindingPhoneActivity.this, error.getErrorMsg());
                        });
    }

    /**
     * 手机号绑定
     *
     * @param tel      手机号
     * @param authCode 验证码
     */
    private void bindingPhone(String tel, String authCode) {
        RxHttp.postForm(Consts.BINDING_PHONE_API)
                .add(Consts.MOBILE, tel)
                .add(Consts.MOBILE_CAPTCHA, authCode)
                .asResponse(String.class)
                .doOnSubscribe(disposable -> showLoadingDialog())
                .doFinally(this::dismissLoadingDialog)
                .to(RxLife.toMain(this))
                .subscribe(s -> {
                    UserInfoCache.saveMobile(WXBindingPhoneActivity.this, tel);
                    TipDialog.show(WXBindingPhoneActivity.this, R.string.txt_binding_success, TipDialog.TYPE.SUCCESS)
                            .setOnDismissListener(() -> {
                                setResult(RESULT_OK);
                                finish();
                            });
                }, (OnError) error -> TipDialog.show(WXBindingPhoneActivity.this, error.getErrorMsg(), TipDialog.TYPE.ERROR));
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
       /* MessageDialog.show(this, "取消绑定", "现在退出将会取消微信绑定哦~", "继续绑定", "放弃")
                .setOnCancelButtonClickListener((baseDialog, v) -> {
                    finish();
                    return false;
                });*/
    }
}
