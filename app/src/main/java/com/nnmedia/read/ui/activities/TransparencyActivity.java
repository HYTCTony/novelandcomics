package com.nnmedia.read.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.kongzue.dialog.v3.MessageDialog;
import com.nnmedia.novel.R;
import com.nnmedia.read.ui.base.BaseActivity;

/**
 * 用于全局弹窗
 */
public class TransparencyActivity extends BaseActivity {

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_transparency;
    }

    @Override
    public void initView(View view) {

    }

    @Override
    public void setListener() {

    }

    @Override
    public void doBusiness(Context mContext) {
        MessageDialog.build(this)
                .setTitle(R.string.txt_login_expired)
                .setMessage(R.string.txt_plz_log_in_again)
                .setOkButton("去登陆")
                .setCancelButton("算了")
                .setOnOkButtonClickListener((baseDialog, v) -> {
                    LoginActivity2.start(TransparencyActivity.this);
                    finish();
                    return false;
                })
                .setOnCancelButtonClickListener((baseDialog, v) -> {
                    finish();
                    return false;
                })
                .setCancelable(false)
                .setOnBackClickListener(() -> true)
                .show();
    }

}
