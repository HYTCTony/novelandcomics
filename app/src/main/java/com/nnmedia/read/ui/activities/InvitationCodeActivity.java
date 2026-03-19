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
import com.nnmedia.read.listeners.OnClickEvent;
import com.nnmedia.read.rxhttp.OnError;
import com.nnmedia.read.ui.base.BaseActivity;
import com.nnmedia.read.utils.Tos;
import com.rxjava.rxlife.RxLife;

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
            LoginActivity2.start4Result(this, LoginActivity2.REQCODE_LOGIN);
            return;
        }

        if (UserInfoCache.getIsInvited(mContext)) {
            TipDialog.show(this, "你已填写过邀请码", TipDialog.TYPE.WARNING).setOnDismissListener(this::onBackPressed);
        }
    }


    /**
     * 提交邀请码
     * token在CallBack中统一加到header
     *
     * @param inviteCode
     */
    private void reqInviteCodeSubmit(String inviteCode) {
        RxHttp.postForm(Consts.FILLIN_INVITE_CODE_API)
                .add(Consts.CODE, inviteCode)
                .asResponse(String.class)
                .doOnSubscribe(disposable -> showLoadingDialog())
                .doFinally(this::dismissLoadingDialog)
                .to(RxLife.toMain(this))
                .subscribe(s -> {
                    UserInfoCache.saveIsInvited(InvitationCodeActivity.this, true);
                    TipDialog.show(InvitationCodeActivity.this, "邀请码提交成功", TipDialog.TYPE.SUCCESS)
                            .setOnDismissListener(() -> {
                                setResult(RESULT_OK);
                                finish();
                            });
                }, (OnError) error -> Tos.showShort(InvitationCodeActivity.this, error.getErrorMsg()));
    }
}
