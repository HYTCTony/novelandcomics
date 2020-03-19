package com.huli.foxread.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.huli.foxread.R;
import com.huli.foxread.ui.base.BaseActivity;
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.v3.MessageDialog;

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
                .setOnOkButtonClickListener(new OnDialogButtonClickListener() {
                    @Override
                    public boolean onClick(BaseDialog baseDialog, View v) {
                        Intent intent = new Intent(TransparencyActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                        return false;
                    }
                })
                .setCancelable(false).setOnBackClickListener(() -> true).show();
    }

    @Override
    public void onBackPressed() {

    }
}
