package com.huli.foxread.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

import com.huli.foxread.R;
import com.huli.foxread.contact.Common;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.ui.widget.ProgressWebview;

import androidx.appcompat.widget.Toolbar;

public class CommonWebActivity extends BaseActivity {

    private Toolbar mToolbar;
    private ProgressWebview webView;

    private String url;

    @Override
    public void initParms(Bundle parms) {
        if (parms != null) {
            url = parms.getString(Common.KEY_URL);
        }
    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_common_webview;
    }

    @Override
    public void initView(View view) {
        mToolbar = $(R.id.toolbar_normal_wb);
        mToolbar.setTitle(R.string.txt_opening);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(v -> onBackPressed());

        webView = $(R.id.progressWebview_common);
    }

    @Override
    public void setListener() {
        webView.setOnWebViewListener(new ProgressWebview.onWebViewListener() {
            @Override
            public void onProgressChange(WebView view, int newProgress) {

            }

            @Override
            public void onPageFinish(WebView view) {

            }

            @Override
            public void onTitleReceived(WebView view, String title) {
                mToolbar.setTitle(title);
            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {
        webView.loadUrl(url);
    }


    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
            return;
        }
        super.onBackPressed();
    }

}
