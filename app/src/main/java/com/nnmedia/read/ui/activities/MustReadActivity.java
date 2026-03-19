package com.nnmedia.read.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.nnmedia.novel.R;
import com.nnmedia.read.listeners.OnClickEvent;
import com.nnmedia.read.ui.base.BaseActivity;
import com.nnmedia.read.ui.fragments.MustReadFragment;
import com.nnmedia.read.utils.StatusBarUtils;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


/*
 * 必读榜
 * */
public class MustReadActivity extends BaseActivity {
    private ImageView btnSearch;
    private Toolbar toolbar;

    /*控制ctlTabLayout变色*/
    private boolean childForbid;        //child控制禁止恢复颜色
    public boolean isWhite;             //（现在）是白色


    public static void start(Context context) {
        Intent starter = new Intent(context, MustReadActivity.class);
        context.startActivity(starter);
    }

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    protected void setStatusBar() {
        StatusBarUtils.setColor(this, getColor(R.color.col_musts_red_FFDA33), 0);
        StatusBarUtils.offsetView(this, toolbar);
        StatusBarUtils.setAndroidNativeLightStatusBar(this, true);
    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_must_read;
    }

    @Override
    public void initView(View view) {
        toolbar = $(R.id.toolbar_normal);
        initToolBar(toolbar, R.string.txt_must_read);

        btnSearch = $(R.id.iv_asBtn_search);

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        MustReadFragment myFragment = new MustReadFragment();
        ft.add(R.id.fragment, myFragment);
        ft.commit();
    }

    @Override
    public void setListener() {
        btnSearch.setOnClickListener(new OnClickEvent() {
            @Override
            public void singleClick(View v) {
                Intent intent = new Intent(MustReadActivity.this, SearchBookActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {

    }

}