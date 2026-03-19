package com.nnmedia.read.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.nnmedia.novel.R;
import com.nnmedia.read.contact.Common;
import com.nnmedia.read.ebsevent.WebPayEvent;
import com.nnmedia.read.ui.base.BaseActivity;
import com.nnmedia.read.ui.widget.ProgressWebview;

import org.greenrobot.eventbus.EventBus;

import androidx.appcompat.widget.Toolbar;

public class CommonWebActivity extends BaseActivity {

    private Toolbar mToolbar;
    private ProgressWebview webView;

    private String url;
    private String payOrderId;
    private boolean isPay;

    @Override
    public void initParms(Bundle parms) {
        if (parms != null) {
            url = parms.getString(Common.KEY_URL);
            isPay = parms.getBoolean(Common.KEY_PAY);
            payOrderId = parms.getString(Common.KEY_PAY_ID);
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
//        if (webView.canGoBack()) {
//            webView.goBack();
//            return;
//        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isPay) {
            EventBus.getDefault().post(new WebPayEvent(payOrderId));
        }
    }
}