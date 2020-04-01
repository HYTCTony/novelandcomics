package com.huli.foxread;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.huli.foxread.callbacks.ActivityManager;
import com.huli.foxread.callbacks.ActivityState;
import com.huli.foxread.contact.Common;
import com.huli.foxread.interceptors.TokenInterceptor;
import com.huli.foxread.ui.activities.MainActivity;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.util.DialogSettings;
import com.kongzue.dialog.util.TextInfo;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.FalsifyFooter;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.multidex.MultiDex;
import okhttp3.OkHttpClient;

public class FrApp extends Application implements ActivityState {

    public static final String WECHAT_APP_ID = "wx53ed3b26af319dd0";

    private static FrApp sInstance;

    public static FrApp getInstance() {
        return sInstance;
    }

    public ActivityManager mActivityManager = ActivityManager.getInstance(this);

    //static 代码段可以防止内存泄露
    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @NonNull
            @Override
            public RefreshHeader createRefreshHeader(@NonNull Context context, @NonNull RefreshLayout layout) {
                layout.setPrimaryColorsId(R.color.transparent, R.color.col_theme_blue);//全局设置主题颜色
                return new MaterialHeader(context);
//                return new ClassicsHeader(context).setSpinnerStyle(SpinnerStyle.Scale);
//                        .setPrimaryColorId(R.color.colorPrimary)
//                        .setAccentColorId(android.R.color.white);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @NonNull
            @Override
            public RefreshFooter createRefreshFooter(@NonNull Context context, @NonNull RefreshLayout layout) {
                return new FalsifyFooter(context);
                //指定为经典Footer，默认是 BallPulseFooter
//                return new ClassicsFooter(context).setDrawableSize(20);
            }
        });
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        //放在其他库初始化前
//        SpiderMan.init(this);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new TokenInterceptor(sInstance));
        builder.connectTimeout(15, TimeUnit.SECONDS);

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkGo");
        //log打印级别，决定了log显示的详细程度
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
        //log颜色级别，决定了log在控制台显示的颜色
        loggingInterceptor.setColorLevel(Level.SEVERE);
        builder.addInterceptor(loggingInterceptor);

        OkGo.getInstance()
                .init(this)
                .setOkHttpClient(builder.build());

        registerActivityLifecycleCallbacks(mActivityManager);
//        registerActivityLifecycleCallbacks(ParallaxHelper.getInstance());

        DialogSettings.init();
        DialogSettings.DEBUGMODE = true;
//        DialogSettings.backgroundColor = Color.BLUE;
//        DialogSettings.titleTextInfo = new TextInfo().setFontSize(50);
        DialogSettings.buttonTextInfo = new TextInfo().setFontColor(ContextCompat.getColor(this, R.color.txt_gray));
        DialogSettings.buttonPositiveTextInfo = new TextInfo().setFontColor(ContextCompat.getColor(this, R.color.txt_red));
        DialogSettings.style = DialogSettings.STYLE.STYLE_IOS;
        DialogSettings.theme = DialogSettings.THEME.LIGHT;

        initBugly();

        //友盟
        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, "f8601f634c3ec7668da5a856bbd9a9fe");
        // 选用AUTO页面采集模式
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
        PlatformConfig.setWeixin(WECHAT_APP_ID,"4f58d7d2894fe8631831d18ed1d6d4be");
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onTerminate() {
        BaseDialog.unload();
        super.onTerminate();
    }


    /**
     * 退出应用程序
     */
    public void exitApp() {
        mActivityManager.finishAll();
    }

    @Override
    public void isFront() {
        Log.e("FrApp", ">>>>>>>>>>>>>>>>>>>App切到前台");
    }

    @Override
    public void isBack() {
        Log.e("FrApp", ">>>>>>>>>>>>>>>>>>>App切到后台");
    }

    private void initBugly() {
        /**
         * 设置升级检查周期为60s(默认检查周期为0s)，60s内SDK不重复向后台请求策略);
         */
        Beta.upgradeCheckPeriod = 60 * 1000;
        /**
         * 只允许在MainActivity上显示更新弹窗，其他activity上不显示弹窗;
         * 不设置会默认所有activity都可以显示弹窗;
         */
        Beta.canShowUpgradeActs.add(MainActivity.class);

        Bugly.init(getApplicationContext(), "a5471c79fd", false);

    }
}
