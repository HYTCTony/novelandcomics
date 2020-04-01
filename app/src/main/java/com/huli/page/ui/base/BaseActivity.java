package com.huli.page.ui.base;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.umeng.analytics.MobclickAgent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {
    public FragmentActivity mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewResId());
        ButterKnife.bind(this);
        mContext = this;
        initP();
        initView();
    }

    @Override
    protected void onResume() {
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        MobclickAgent.onResume(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        MobclickAgent.onPause(this);
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected abstract void initView();

    protected abstract int getContentViewResId();

    protected void initP() {

    }
}