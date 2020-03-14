package com.huli.foxread.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.huli.foxread.R;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.listeners.OnClickEvent;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.ui.widget.ProgressWebview;

import androidx.appcompat.widget.Toolbar;

public class HelpAndFeedbackActivity extends BaseActivity {

    private TextView btnGo2Feedback;
    private ProgressWebview webview;

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_help_and_feedback;
    }

    @Override
    public void initView(View view) {
        Toolbar toolbar = $(R.id.toolbar_normal);
        initToolBar(toolbar, R.string.txt_help_and_feedback);

        btnGo2Feedback = $(R.id.tv_asBtn_go2_feedback);
        webview = $(R.id.progressWebview_help_and_feedback);
    }

    @Override
    public void setListener() {
        btnGo2Feedback.setOnClickListener(new OnClickEvent() {
            @Override
            public void singleClick(View v) {
                startActivity(new Intent(HelpAndFeedbackActivity.this, GoToFeedBackActivity.class));
            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {
        webview.loadUrl(Consts.FEEDBACK_URL);
    }


    @Override
    public void onBackPressed() {
        if (webview.canGoBack()) {
            webview.goBack();
            return;
        }
        super.onBackPressed();
    }
}
