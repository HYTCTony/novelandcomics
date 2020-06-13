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
import com.huli.foxread.cache.UserInfoCache;
import com.huli.foxread.callbacks.ookkggoo.LtbCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.listeners.OnClickEvent;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.utils.Tos;
import com.kongzue.dialog.interfaces.OnDismissListener;
import com.kongzue.dialog.v3.TipDialog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import androidx.appcompat.widget.Toolbar;

public class InvitationCodeActivity extends BaseActivity {

    private EditText etInvitationCode;
    private Button btnSubmit;

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_invitation_code;
    }

    @Override
    public void initView(View view) {
        Toolbar toolbar = $(R.id.toolbar_normal);
        initToolBar(toolbar, R.string.txt_invitation_code);

        etInvitationCode = $(R.id.editText_invitation_code);
        btnSubmit = $(R.id.btn_confirm_2_submit);
    }

    @Override
    public void setListener() {
        btnSubmit.setOnClickListener(new OnClickEvent() {
            @Override
            public void singleClick(View v) {
                String iCode = etInvitationCode.getText().toString();
                if (TextUtils.isEmpty(iCode)) {
                    Tos.showShort(InvitationCodeActivity.this, R.string.txt_plz_input_invitation_code);
                    return;
                }
                reqInviteCodeSubmit(iCode);
            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {
        //需要登录
        if (UserInfoCache.getIsTourist(mContext)) {
            LoginActivity.start4Result(this, LoginActivity.REQCODE_LOGIN);
            return;
        }

        if(UserInfoCache.getIsInvited(mContext)){
            TipDialog.show(this,"你已填写过邀请码", TipDialog.TYPE.WARNING).setOnDismissListener(this::onBackPressed);
        }
    }


    /**
     * 提交邀请码
     * token在CallBack中统一加到header
     *
     * @param inviteCode
     */
    private void reqInviteCodeSubmit(String inviteCode) {
        OkGo.<String>post(Consts.FILLIN_INVITE_CODE_API)
                .params(Consts.CODE, inviteCode)
                .execute(new LtbCallback(this) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<String> entity = JSONObject.parseObject(response.body(), new TypeReference<LzyResponse<String>>() {
                        });
                        if (entity.error_code == 0) {
                            UserInfoCache.saveIsInvited(InvitationCodeActivity.this, true);
                            TipDialog.show(InvitationCodeActivity.this, entity.msg, TipDialog.TYPE.SUCCESS)
                                    .setOnDismissListener(() -> {
                                        setResult(RESULT_OK);
                                        finish();
                                    });
                        } else {
                            Tos.showShort(InvitationCodeActivity.this, entity.msg);
                        }
                    }
                });
    }
}
