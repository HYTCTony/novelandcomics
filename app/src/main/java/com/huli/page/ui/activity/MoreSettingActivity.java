package com.huli.page.ui.activity;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.huli.foxread.R;
import com.huli.foxread.config.TTAdManagerHolder;
import com.huli.foxread.utils.StatusBarUtils;
import com.huli.page.model.local.ReadSettingManager;
import com.huli.page.ui.base.BaseViewActivity;
import com.huli.page.utils.ScreenUtils;

import java.util.List;

import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.TintTypedArray;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.OnClick;

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

    private TTAdNative mTTAdNative;
    private TTNativeExpressAd mTTAdPage;
    @BindView(R.id.express_container)
    FrameLayout mExpressContainer;

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

    @Override
    protected void initView() {
        mTTAdNative = TTAdManagerHolder.get().createAdNative(this);
        requestAdPage();
        StatusBarUtils.setTransparentForImageView(mContext, toolbar);
        StatusBarUtils.setColor(this, ContextCompat.getColor(this, R.color.white), 0);
        StatusBarUtils.setAndroidNativeLightStatusBar(this, true);

        mSettingManager = ReadSettingManager.getInstance();
        isVolumeTurnPage = mSettingManager.isVolumeTurnPage();
        isFullScreen = mSettingManager.isFullScreen();
        convertType = mSettingManager.getConvertType();

        mScVolume.setChecked(isVolumeTurnPage);
        mScFullScreen.setChecked(isFullScreen);

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

    }

    @OnClick(R.id.btn)
    void onClick() {
//        requestAdPage();
        ReadTestActivity.start(mContext);
    }

    private void requestAdPage() {
        //step4:创建广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlotPage = new AdSlot.Builder()
                .setCodeId("945191706") //广告位id  945191678*视频   945191706*图片
                .setSupportDeepLink(true)
                .setAdCount(1) //请求广告数量为1到3条
                .setExpressViewAcceptedSize(ScreenUtils.getScreenSize(mContext)[0], 0) //期望模板广告view的size,单位dp
                .build();
        //step5:请求广告，对请求回调的广告作渲染处理
        mTTAdNative.loadNativeExpressAd(adSlotPage, new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int code, String message) {
                Log.e("ExpressView", "load error : " + code + ", " + message);
            }

            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                if (ads == null || ads.size() == 0) {
                    return;
                }
                mTTAdPage = ads.get(0);
                bindAdListener1(mTTAdPage);
                mTTAdPage.render();
                Log.e("ExpressView", "onNativeExpressAdLoad");
            }
        });
    }

    private void bindAdListener1(TTNativeExpressAd ad) {
        ad.setExpressInteractionListener(new TTNativeExpressAd.ExpressAdInteractionListener() {
            @Override
            public void onAdClicked(View view, int type) {
                Log.e("ExpressView", "广告被点击");
            }

            @Override
            public void onAdShow(View view, int type) {
                Log.e("ExpressView", "广告展示");
            }

            @Override
            public void onRenderFail(View view, String msg, int code) {
                Log.e("ExpressView", msg + " code:" + code);
            }

            @Override
            public void onRenderSuccess(View view, float width, float height) {
                Log.e("ExpressView", "width:" + width);
                Log.e("ExpressView", "height:" + height);
                Log.e("ExpressView", "screen_width:" + ScreenUtils.getScreenSize(mContext)[0]);
                Log.e("ExpressView", "screen_height:" + ScreenUtils.getScreenSize(mContext)[1]);
//                返回view的宽高 单位 dp
                Log.e("ExpressView", "渲染成功");
                mExpressContainer.removeAllViews();
                mExpressContainer.addView(view);
            }
        });
    }
}