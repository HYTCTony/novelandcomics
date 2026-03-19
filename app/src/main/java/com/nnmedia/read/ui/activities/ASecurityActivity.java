package com.nnmedia.read.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.nnmedia.novel.R;
import com.nnmedia.read.listeners.OnClickEvent;
import com.nnmedia.read.ui.base.BaseActivity;

import androidx.appcompat.widget.Toolbar;

public class ASecurityActivity extends BaseActivity {
    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_a_security;
    }

    @Override
    public void initView(View view) {
        Toolbar toolbar = $(R.id.toolbar_normal);
        initToolBar(toolbar, R.string.txt_account_security);
    }

    @Override
    public void setListener() {
        findViewById(R.id.rtl_asBtn_write_off_account).setOnClickListener(new OnClickEvent() {
            @Override
            public void singleClick(View v) {
                startActivity(new Intent(ASecurityActivity.this, WriteOffAccountActivity.class));
            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {

    }
}
