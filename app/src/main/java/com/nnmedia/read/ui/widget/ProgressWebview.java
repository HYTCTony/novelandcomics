package com.nnmedia.read.ui.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.http.SslError;
import android.util.AttributeSet;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.nnmedia.novel.R;

import androidx.core.content.ContextCompat;

public class ProgressWebview extends WebView {

    private ProgressBar progressbar;  //进度条

    private int progressHeight = 8;  //进度条的高度，默认8px
    private Context context;


    public ProgressWebview(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public ProgressWebview(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
        initView();
    }


    private void initView() {

        WebSettings settings = getSettings();
        //开启js脚本支持
        settings.setJavaScriptEnabled(true);

        //创建进度条
        progressbar = new ProgressBar(context, null,
                android.R.attr.progressBarStyleHorizontal);
        //设置加载进度条的高度
        progressbar.setLayoutParams(new LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, progressHeight, 0, 0));

        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.webview_progress_bar_states);
        progressbar.setProgressDrawable(drawable);

        //添加进度到WebView
        addView(progressbar);

        //适配手机大小
        settings.setUseWideViewPort(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        settings.setLoadWithOverviewMode(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);

        setWebChromeClient(new WVChromeClient());
        setWebViewClient(new WVClient());
    }


    //进度显示
    private class WVChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                progressbar.setVisibility(GONE);
            } else {
                if (progressbar.getVisibility() == GONE)
                    progressbar.setVisibility(VISIBLE);
                progressbar.setProgress(newProgress);
            }

            if (mListener != null) {
                mListener.onProgressChange(view, newProgress);
            }

            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (mListener != null) {
                mListener.onTitleReceived(view, title);
            }
        }

    }

    private class WVClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

//            //在当前Activity打开
//            view.loadUrl(url);
//            return true;

            if (url == null) return false;
            try {
                if (url.startsWith("http:") || url.startsWith("https:")) {
                    view.loadUrl(url);
                    return true;
                } else {         //加载网页出现("找不到网页net:err_unknown_url_scheme")
                    //用浏览器打开
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    context.startActivity(intent);
                    return true;
                }
            } catch (Exception e) { //防止crash (如果手机上没有安装处理某个scheme开头的url的APP, 会导致crash)
                return false;
            }
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            //https忽略证书问题
            handler.proceed();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            progressbar.setVisibility(GONE);
            if (mListener != null) {
                mListener.onPageFinish(view);
            }

            super.onPageFinished(view, url);

        }

    }

    private onWebViewListener mListener;

    public void setOnWebViewListener(onWebViewListener listener) {
        this.mListener = listener;
    }

    //进度回调接口
    public interface onWebViewListener {
        void onProgressChange(WebView view, int newProgress);

        void onPageFinish(WebView view);

        void onTitleReceived(WebView view, String title);
    }

}