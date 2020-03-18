package com.huli.page.ui.activity;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.huli.foxread.R;
import com.huli.foxread.utils.StatusBarUtils;
import com.huli.page.model.local.ReadSettingManager;
import com.huli.page.ui.base.BaseViewActivity;

import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.TintTypedArray;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import butterknife.BindView;

public class MoreSettingActivity extends BaseViewActivity {
    @BindView(R.id.more_setting_rl_volume)
    RelativeLayout mRlVolume;
    @BindView(R.id.more_setting_sc_volume)
    SwitchCompat mScVolume;
    @BindView(R.id.more_setting_rl_full_screen)
    RelativeLayout mRlFullScreen;
    @BindView(R.id.more_setting_sc_full_screen)
    SwitchCompat mScFullScreen;
    @BindView(R.id.more_setting_rl_convert_type)
    LinearLayout mRlConvertType;
    @BindView(R.id.more_setting_sc_convert_type)
    Spinner mScConvertType;
    private ReadSettingManager mSettingManager;
    private boolean isVolumeTurnPage;
    private boolean isFullScreen;
    private int convertType;

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_more_setting;
    }

    @Override
    protected void initToolbar(Toolbar toolbar) {
        setTitle("阅读设置");
        TintTypedArray a = TintTypedArray.obtainStyledAttributes(this,
                null, R.styleable.ActionBar, R.attr.actionBarStyle, 0);
        Drawable drawable = a.getDrawable(R.styleable.ActionBar_homeAsUpIndicator);
        drawable.setColorFilter(black, PorterDuff.Mode.MULTIPLY);
        toolbar.setNavigationIcon(drawable);
        toolbar.setTitleTextColor(black);
        toolbar.setSubtitleTextColor(black);
        super.initToolbar(toolbar);
    }

    private void initSwitchStatus() {
        mScVolume.setChecked(isVolumeTurnPage);
        mScFullScreen.setChecked(isFullScreen);
    }

    @Override
    protected void initView() {
        StatusBarUtils.setTransparentForImageView(mContext, toolbar);
        StatusBarUtils.setColor(this, ContextCompat.getColor(this, R.color.white), 0);
        StatusBarUtils.setAndroidNativeLightStatusBar(this, true);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.conversion_type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mScConvertType.setAdapter(adapter);

        // initSwitchStatus() be called earlier than onCreate(), so setSelection() won't work
        mScConvertType.setSelection(convertType);

        mScConvertType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSettingManager.setConvertType(position);
                convertType = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mSettingManager = ReadSettingManager.getInstance();
        isVolumeTurnPage = mSettingManager.isVolumeTurnPage();
        isFullScreen = mSettingManager.isFullScreen();
        convertType = mSettingManager.getConvertType();

        mRlVolume.setOnClickListener(
                (v) -> {
                    if (isVolumeTurnPage) {
                        isVolumeTurnPage = false;
                    } else {
                        isVolumeTurnPage = true;
                    }
                    mScVolume.setChecked(isVolumeTurnPage);
                    mSettingManager.setVolumeTurnPage(isVolumeTurnPage);
                }
        );

        mRlFullScreen.setOnClickListener(
                (v) -> {
                    if (isFullScreen) {
                        isFullScreen = false;
                    } else {
                        isFullScreen = true;
                    }
                    mScFullScreen.setChecked(isFullScreen);
                    mSettingManager.setFullScreen(isFullScreen);
                }
        );
        initSwitchStatus();
    }

}