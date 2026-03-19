package com.nnmedia.read.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.nnmedia.novel.R;
import com.nnmedia.read.ui.base.BaseActivity;
import com.nnmedia.read.ui.fragments.MainClassifyFragment;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class ClassifyActivity2 extends BaseActivity {

    public static void start(Context context) {
        Intent starter = new Intent(context, ClassifyActivity2.class);
        context.startActivity(starter);
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_classify2;
    }

    @Override
    public void initView(View view) {
        Toolbar toolbar = $(R.id.toolbar_normal);
        initToolBar(toolbar, R.string.txt_all_classify);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        MainClassifyFragment classifyFragment = new MainClassifyFragment();
        ft.add(R.id.fragment, classifyFragment);
        ft.commit();
    }

    @Override
    public void setListener() {

    }

    @Override
    public void doBusiness(Context mContext) {

    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public void initParms(Bundle parms) {

    }
}
