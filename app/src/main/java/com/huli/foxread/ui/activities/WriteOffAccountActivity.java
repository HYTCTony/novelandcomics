package com.huli.foxread.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;

import com.huli.foxread.R;
import com.huli.foxread.listeners.OnClickEvent;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.ui.widget.ProgressWebview;

import androidx.appcompat.widget.Toolbar;

public class WriteOffAccountActivity extends BaseActivity {

    private ProgressWebview webview;
    private FrameLayout layoutBottomBar;
    private Button btnConfirm;

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_write_off_account;
    }

    @Override
    public void initView(View view) {
        Toolbar toolbar = $(R.id.toolbar_normal);
        initToolBar(toolbar, R.string.txt_apply_2_write_off_account);

        webview = $(R.id.progressWebView_write_off);

        layoutBottomBar = $(R.id.fl_bottom_bar_wo);
        layoutBottomBar.setVisibility(View.GONE);
        btnConfirm = $(R.id.btn_confirm);
    }

    @Override
    public void setListener() {
        btnConfirm.setOnClickListener(new OnClickEvent() {
            @Override
            public void singleClick(View v) {

            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {
        webview.loadUrl("https://www.sina.com.cn/");
        webview.setOnWebViewListener(new ProgressWebview.onWebViewListener() {
            @Override
            public void onProgressChange(WebView view, int newProgress) {

            }

            @Override
            public void onPageFinish(WebView view) {
                layoutBottomBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTitleReceived(WebView view, String title) {

            }
        });
    }
}
