package com.nnmedia.read.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.nnmedia.novel.R;
import com.nnmedia.read.ui.base.BaseActivity;
import com.nnmedia.read.utils.Tos;

public class VerifyPasswordActivity extends BaseActivity implements View.OnClickListener {

    private ImageView ivClose;
    private Button btnNextStep;

    private EditText etPassword;

    public static void start(Context context) {
        Intent starter = new Intent(context, VerifyPasswordActivity.class);
        context.startActivity(starter);
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
        return R.layout.activity_verify_password;
    }

    @Override
    public void initView(View view) {
        ivClose = $(R.id.iv_asBtn_close);
        btnNextStep = $(R.id.btn_next_step);
        etPassword = $(R.id.et_password);
    }

    @Override
    public void setListener() {
        btnNextStep.setOnClickListener(this);
        ivClose.setOnClickListener(this);
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
            case R.id.btn_next_step:
                String pwd = etPassword.getText().toString();
                if (pwd.isEmpty() || pwd.length() < 6) {
                    Tos.showShort(this, "请输入正确的密码");
                    return;
                }
                ChangePasswordActivity.start(this, pwd);
                break;
        }
    }

}