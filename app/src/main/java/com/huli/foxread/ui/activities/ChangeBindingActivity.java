package com.huli.foxread.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.huli.foxread.R;
import com.huli.foxread.cache.UserInfoCache;
import com.huli.foxread.callbacks.ookkggoo.LtbCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.ui.widget.TimingButton;
import com.huli.foxread.utils.SomeMonitorEditText;
import com.huli.foxread.utils.Tos;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

/**
 * 更换绑定手机---第一步
 */
public class ChangeBindingActivity extends BaseActivity implements View.OnClickListener {

    private TimingButton tbtnGetVcode;
    private Button btnBinding;
    private EditText etPhoneNum, etAuthCode;

    private String phoneNum = "";

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_change_binding_cellphone;
    }

    @Override
    public void initView(View view) {
        Toolbar toolbar = $(R.id.toolbar_normal);
        initToolBar(toolbar, R.string.txt_change_binding_cellphone);

        tbtnGetVcode = $(R.id.timingButton_get_vCode);
        btnBinding = $(R.id.btn_unbind_next_step);
        etPhoneNum = $(R.id.et_cellphone_number);
        etAuthCode = $(R.id.et_auth_code);
        new SomeMonitorEditText(btnBinding, etAuthCode);
    }

    @Override
    public void setListener() {
        tbtnGetVcode.setOnClickListener(this);
        btnBinding.setOnClickListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {
        phoneNum = UserInfoCache.getMobile(this);
        etPhoneNum.setText(phoneNum.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2"));
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.timingButton_get_vCode:
                if (TextUtils.isEmpty(phoneNum)) {
                    Tos.showShort(this, R.string.txt_plz_input_phone_number);
                    return;
                }
                tbtnGetVcode.start();
                reqAuthCode(phoneNum);
                break;
            case R.id.btn_unbind_next_step:
                String authCode = etAuthCode.getText().toString();
                if (TextUtils.isEmpty(authCode)) {
                    Tos.showShort(this, R.string.txt_plz_input_verification_code);
                    return;
                }
                unbindPhone(phoneNum, authCode);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == AccountSecurityActivity.REQCODE_ATTR_MODIFY) {
                setResult(RESULT_OK);
                finish();
            }
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
                .params(Consts.EVENT, Consts.SMS_UNTYING)
                .execute(new LtbCallback(this) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<String> entity = JSONObject.parseObject(response.body(), new TypeReference<LzyResponse<String>>() {
                        });
                        if (entity.error_code != 0) {
                            tbtnGetVcode.reset();
                        }
                        Tos.showShort(ChangeBindingActivity.this, entity.msg);
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        tbtnGetVcode.reset();
                    }
                });
    }

    /**
     * 手机号解除绑定
     * token 在LtbCallback中统一添加
     *
     * @param tel      现手机号
     * @param authCode
     */
    private void unbindPhone(String tel, String authCode) {
        OkGo.<String>post(Consts.UNBIND_MOBILE_API)
                .params(Consts.MOBILE, tel)
                .params(Consts.CAPTCHA, authCode)
                .execute(new LtbCallback(this) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<String> entity = JSONObject.parseObject(response.body(), new TypeReference<LzyResponse<String>>() {
                        });
                        if (entity.error_code == 0) {
                            startActivityForResult(new Intent(ChangeBindingActivity.this, BindingCellphoneActivity.class), AccountSecurityActivity.REQCODE_ATTR_MODIFY);
                        } else {
                            Tos.showShort(ChangeBindingActivity.this, entity.msg);
                        }
                    }
                });
    }
}
