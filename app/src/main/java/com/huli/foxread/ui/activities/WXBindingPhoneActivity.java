package com.huli.foxread.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
import com.huli.foxread.utils.SomeMonitorEditText;
import com.huli.foxread.utils.Tos;
import com.huli.foxread.utils.UniqueIdManager;
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialog.interfaces.OnDismissListener;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.v3.MessageDialog;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;

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
        OkGo.<String>post(Consts.SMS_SEND_API)
                .params(Consts.MOBILE, tel)
                .params(Consts.EVENT, Consts.SMS_BIND)
                .execute(new LtbCallback(this) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<String> entity = JSONObject.parseObject(response.body(), new TypeReference<LzyResponse<String>>() {
                        });
                        if (entity.error_code != 0) {
                            tbtnGetVcode.reset();
                        }
                        Tos.showShort(WXBindingPhoneActivity.this, entity.msg);
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        tbtnGetVcode.reset();
                    }
                });
    }

    /**
     * 手机号绑定
     * token 在LtbCallback中统一添加
     *
     * @param tel
     * @param authCode
     */
    private void bindingPhone(String tel, String authCode) {
        OkGo.<String>post(Consts.BIND_MOBILE_API)
                .params(Consts.MOBILE, tel)
                .params(Consts.CAPTCHA, authCode)
                .execute(new LtbCallback(this) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<String> entity = JSONObject.parseObject(response.body(), new TypeReference<LzyResponse<String>>() {
                        });
                        if (entity.error_code == 0) {
                            UserInfoCache2.saveMobile(WXBindingPhoneActivity.this, tel);
                            TipDialog.show(WXBindingPhoneActivity.this, entity.msg, TipDialog.TYPE.SUCCESS)
                                    .setOnDismissListener(() -> {
                                        setResult(RESULT_OK);
                                        finish();
                                    });
                        } else {
                            TipDialog.show(WXBindingPhoneActivity.this, entity.msg, TipDialog.TYPE.ERROR);
                        }
                    }
                });

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
