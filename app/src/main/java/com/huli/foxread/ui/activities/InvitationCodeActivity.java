package com.huli.foxread.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.huli.foxread.R;
import com.huli.foxread.listeners.OnClickEvent;
import com.huli.foxread.ui.base.BaseActivity;

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
        Toolbar toolbar = $(R.id.editText_invitation_code);
        initToolBar(toolbar, R.string.txt_invitation_code);

        etInvitationCode = $(R.id.editText_invitation_code);
        btnSubmit = $(R.id.btn_confirm_2_submit);
    }

    @Override
    public void setListener() {
        btnSubmit.setOnClickListener(new OnClickEvent() {
            @Override
            public void singleClick(View v) {

            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {

    }
}
