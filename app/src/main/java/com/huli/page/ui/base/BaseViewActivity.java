package com.huli.page.ui.base;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
import com.huli.foxread.R;
import com.huli.page.utils.KeyBoardUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.TintTypedArray;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class BaseViewActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;
    @BindColor(R.color.white)
    protected int white;
    @BindColor(R.color.black)
    protected int black;
    public FragmentActivity mContext;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewResId());
        ButterKnife.bind(this);
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(this,
                null, R.styleable.ActionBar, R.attr.actionBarStyle, 0);
        Drawable drawable = a.getDrawable(R.styleable.ActionBar_homeAsUpIndicator);
        drawable.setColorFilter(black, PorterDuff.Mode.MULTIPLY);
        toolbar.setNavigationIcon(drawable);
        toolbar.setTitleTextColor(black);
        toolbar.setSubtitleTextColor(black);
        mContext = this;
        initP();
        initView();
        initToolbar(toolbar);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            KeyBoardUtils.closeKeyboard(mContext, toolbar);
        }
        return super.onOptionsItemSelected(item);
    }

    protected void initToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onResume();
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    protected void showNotice(String text) {
        Snackbar.make(toolbar, text, Snackbar.LENGTH_SHORT).show();
    }

    protected void showNotice(String text, String clickText, View.OnClickListener listener) {
        Snackbar snackbar = Snackbar.make(toolbar, text, Snackbar.LENGTH_SHORT);
        snackbar.setAction(clickText, listener);
        snackbar.show();
    }

    protected abstract int getContentViewResId();

    protected abstract void initView();

    protected void initP() {

    }

}
