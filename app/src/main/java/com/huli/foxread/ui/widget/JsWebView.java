package com.huli.foxread.ui.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.huli.foxread.R;

import androidx.core.content.ContextCompat;

/**
 * JsBridge 实现js交互
 */
public class JsWebView extends BridgeWebView {

    private ProgressBar progressbar;  //进度条

    private int progressHeight = 8;  //进度条的高度，默认8px
    private Context context;


    public JsWebView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public JsWebView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
        initView();
    }


    private void initView() {
        //创建进度条
        progressbar = new ProgressBar(context, null,
                android.R.attr.progressBarStyleHorizontal);
        //设置加载进度条的高度
        progressbar.setLayoutParams(new LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, progressHeight, 0, 0));

        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.webview_progress_bar_states);
        progressbar.setProgressDrawable(drawable);

        //添加进度到WebView
        addView(progressbar);

        setWebChromeClient(new WVChromeClient());
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

    private onWebViewListener mListener;

    public void setOnWebViewListener(onWebViewListener listener) {
        this.mListener = listener;
    }

    //进度回调接口
    public interface onWebViewListener {
        void onProgressChange(WebView view, int newProgress);

        void onTitleReceived(WebView view, String title);
    }

}
