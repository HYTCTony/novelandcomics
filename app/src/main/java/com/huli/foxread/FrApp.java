package com.huli.foxread;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.chad.library.adapter.base.module.LoadMoreModuleConfig;
import com.huli.foxread.callbacks.ActivityState;
import com.huli.foxread.callbacks.MyActivityManager;
import com.huli.foxread.config.TogetherAdConst;
import com.huli.foxread.interceptors.TokenInterceptor;
import com.huli.foxread.rxhttp.RxHttpManager;
import com.huli.foxread.ui.activities.MainActivity;
import com.huli.foxread.ui.views.MyLoadMoreView;
import com.huli.foxread.utils.AutoLoginUtils;
import com.hytc.ads.TogetherAd;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.util.DialogSettings;
import com.kongzue.dialog.util.TextInfo;
import com.lzy.okgo.OkGo;
import com.qq.gdt.action.GDTAction;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.FalsifyFooter;
import com.sh.sdk.shareinstall.ShareInstall;
import com.sh.sdk.shareinstall.autologin.AutoLoginManager;
import com.sh.sdk.shareinstall.autologin.listener.AvoidPwdLoginInitListener;
import com.sh.sdk.shareinstall.listener.SDKInitListener;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.socialize.PlatformConfig;

import org.android.agoo.xiaomi.MiPushRegistar;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.multidex.MultiDex;
import okhttp3.OkHttpClient;

/*切换分支*/
public class FrApp extends Application implements ActivityState {

    public static final String WECHAT_APP_ID = "wx53ed3b26af319dd0";

    private static FrApp sInstance;

    public static FrApp getInstance() {
        return sInstance;
    }

    public MyActivityManager mActivityManager = MyActivityManager.getInstance(this);

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

        RxHttpManager.init(this);
        initOkgo();  //okgo

        registerActivityLifecycleCallbacks(mActivityManager);
//        registerActivityLifecycleCallbacks(ParallaxHelper.getInstance());

        initKzDialog();      //空祖家的对话框

        initBugly();

        initUMeng();     //友盟

        // BaseRecyclerViewAdapterHelper 配置全局自定义的 LoadMoreView
        LoadMoreModuleConfig.setDefLoadMoreView(new MyLoadMoreView());

        if (isMainProcess()) {
            ShareInstall.getInstance().init(getApplicationContext(), new SDKInitListener() {
                @Override
                public void onSuccess() {
//                    Log.e("Application", "onInitSuccess");
                }

                @Override
                public void onError(String s) {
//                    Log.e("Application", "onInitError:" + s);
                }
            });
            AutoLoginManager.getInstance().initAvoidPwd(sInstance, AutoLoginUtils.getCmccConfig(), AutoLoginUtils.getAuthViewDynamicConfig(sInstance),
                    AutoLoginUtils.getUnicomConfig(), new AvoidPwdLoginInitListener() {
                        @Override
                        public void onInitSuccess() {
//                            Log.e("Application", " AutoLogin onInitSuccess");
                        }

                        @Override
                        public void onInitError(String s) {
//                            Log.e("Application", " AutoLogin onInitError = " + s);
                        }
                    });
        }

        //穿山甲SDK初始化
        //强烈建议在应用对应的Application#onCreate()方法中调用，避免出现content为null的异常
//        TTAdManagerHolder.init(this);
        Map<String, String> csjIdMap = new HashMap<>();
        csjIdMap.put(TogetherAdConst.AD_SPLASH, "887319954");
        csjIdMap.put(TogetherAdConst.AD_FLOW_BOOKRACK, "945165433");
        csjIdMap.put(TogetherAdConst.AD_WELFARE_STIMULATE_COIN, "945166035");
        csjIdMap.put(TogetherAdConst.AD_WELFARE_STIMULATE_2, "945192284");
        csjIdMap.put(TogetherAdConst.AD_BTM_BANNER_DARK_BLUE, "945160023");
        csjIdMap.put(TogetherAdConst.AD_BTM_BANNER_POOL_BLUE, "945160023");
        csjIdMap.put(TogetherAdConst.AD_BTM_BANNER_GREEN, "945160023");
        csjIdMap.put(TogetherAdConst.AD_BTM_BANNER_ASHEN, "945160023");
        csjIdMap.put(TogetherAdConst.AD_BTM_BANNER_PINK, "945160023");
        csjIdMap.put(TogetherAdConst.AD_BTM_BANNER_NIGHT, "945160023");
        csjIdMap.put(TogetherAdConst.AD_BTM_BANNER_YELLOW_PAPER, "945160023");
        csjIdMap.put(TogetherAdConst.AD_CENTER_DARK_BLUE, "945245837");
        csjIdMap.put(TogetherAdConst.AD_CENTER_POOL_BLUE, "945245836");
        csjIdMap.put(TogetherAdConst.AD_CENTER_GREEN, "945245831");
        csjIdMap.put(TogetherAdConst.AD_CENTER_ASHEN, "945245835");
        csjIdMap.put(TogetherAdConst.AD_CENTER_PINK, "945191678");
        csjIdMap.put(TogetherAdConst.AD_CENTER_NIGHT, "945245839");
        csjIdMap.put(TogetherAdConst.AD_CENTER_YELLOW_PAPER, "945191706");
        TogetherAd.initCsjAd(sInstance, "5063649", sInstance.getString(R.string.app_name), csjIdMap, true);

        //腾讯广告初始化
        Map<String, String> gdtIdMap = new HashMap<>();
        gdtIdMap.put(TogetherAdConst.AD_SPLASH, "7061113827587147");
        gdtIdMap.put(TogetherAdConst.AD_FLOW_BOOKRACK, "6061817848543342");
        gdtIdMap.put(TogetherAdConst.AD_WELFARE_STIMULATE_COIN, "4071315827299728");
        gdtIdMap.put(TogetherAdConst.AD_WELFARE_STIMULATE_2, "8071116807691652");
        gdtIdMap.put(TogetherAdConst.AD_BTM_BANNER_DARK_BLUE, "6031314807992506");
        gdtIdMap.put(TogetherAdConst.AD_BTM_BANNER_POOL_BLUE, "1051616857493878");
        gdtIdMap.put(TogetherAdConst.AD_BTM_BANNER_GREEN, "9091012838409041");
        gdtIdMap.put(TogetherAdConst.AD_BTM_BANNER_ASHEN, "4021613888626047");
        gdtIdMap.put(TogetherAdConst.AD_BTM_BANNER_PINK, "1051910888820190");
        gdtIdMap.put(TogetherAdConst.AD_BTM_BANNER_NIGHT, "7031518858722295");
        gdtIdMap.put(TogetherAdConst.AD_BTM_BANNER_YELLOW_PAPER, "9071118858437887");
        gdtIdMap.put(TogetherAdConst.AD_CENTER_DARK_BLUE, "9001217858920807");
        gdtIdMap.put(TogetherAdConst.AD_CENTER_POOL_BLUE, "9071118858437887");
        gdtIdMap.put(TogetherAdConst.AD_CENTER_GREEN, "2041811858431090");
        gdtIdMap.put(TogetherAdConst.AD_CENTER_ASHEN, "2031817858638698");
        gdtIdMap.put(TogetherAdConst.AD_CENTER_PINK, "1061415838541032");
        gdtIdMap.put(TogetherAdConst.AD_CENTER_NIGHT, "6001219898794913");
        gdtIdMap.put(TogetherAdConst.AD_CENTER_YELLOW_PAPER, "4011019898634903");
        TogetherAd.initGDTAd(sInstance, "207010113294", gdtIdMap);

        //广点通数据上报
        GDTAction.init(this, "1110534603", "d1522e4f9d76fb2f910b15527a82efd4", getChannel());
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
//        Log.e("FrApp", ">>>>>>>>>>>>>>>>>>>App切到前台");
    }

    @Override
    public void isBack() {
//        Log.e("FrApp", ">>>>>>>>>>>>>>>>>>>App切到后台");
    }


    /**
     * 空祖家的对话框
     */
    private void initKzDialog() {
        DialogSettings.init();
        DialogSettings.DEBUGMODE = true;
//        DialogSettings.backgroundColor = Color.BLUE;
//        DialogSettings.titleTextInfo = new TextInfo().setFontSize(50);
        DialogSettings.buttonTextInfo = new TextInfo().setFontColor(ContextCompat.getColor(this, R.color.txt_gray));
        DialogSettings.buttonPositiveTextInfo = new TextInfo().setFontColor(ContextCompat.getColor(this, R.color.txt_red));
        DialogSettings.style = DialogSettings.STYLE.STYLE_IOS;
        DialogSettings.theme = DialogSettings.THEME.LIGHT;
    }

    /**
     * 统计---获取渠道名
     */
    private String getChannel() {
        try {
            PackageManager pm = getPackageManager();
            ApplicationInfo appInfo = pm.getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            return appInfo.metaData.getString("UMENG_CHANNEL");
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        return "";
    }

    /**
     * 友盟
     */
    private void initUMeng() {
//        UMConfigure.init(this, "5e7dce16570df35f91000159", "ceshi", UMConfigure.DEVICE_TYPE_PHONE, "f8601f634c3ec7668da5a856bbd9a9fe");
        // 注意：如果您已经在AndroidManifest.xml中配置过appkey和channel值，可以调用此版本初始化函数。
        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, "f8601f634c3ec7668da5a856bbd9a9fe");
        //友盟---推送
        PushAgent pushAgent = PushAgent.getInstance(this);
        pushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回deviceToken deviceToken是推送消息的唯一标志
                Log.e("FrApp", "注册成功：deviceToken：-------->  " + deviceToken);
            }

            @Override
            public void onFailure(String s, String s1) {

            }
        });
        //小米
        MiPushRegistar.register(getApplicationContext(), "2882303761518355168", "5471835523168");

        UMConfigure.setLogEnabled(false);
        //debug模式
//        InAppMessageManager.getInstance(getApplicationContext()).setInAppMsgDebugMode(true);

        // 选用AUTO页面采集模式
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);

        //设置微信
        PlatformConfig.setWeixin(WECHAT_APP_ID, "4f58d7d2894fe8631831d18ed1d6d4be");
        //设置QQ
        PlatformConfig.setQQZone("1110348959", "flSP26RdIEM63XkC");
    }

    /**
     * okgo网络框架
     */
    private void initOkgo() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new TokenInterceptor(sInstance));
        builder.connectTimeout(15, TimeUnit.SECONDS);

//        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkGo");
//        //log打印级别，决定了log显示的详细程度
//        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
//        //log颜色级别，决定了log在控制台显示的颜色
//        loggingInterceptor.setColorLevel(Level.SEVERE);
//        builder.addInterceptor(loggingInterceptor);

        OkGo.getInstance()
                .init(this)
                .setOkHttpClient(builder.build());
    }

    private void initBugly() {
        /**
         * 设置升级检查周期为60s(默认检查周期为0s)，60s内SDK不重复向后台请求策略);
         */
        Beta.upgradeCheckPeriod = 600 * 1000L;
        /**
         * 只允许在MainActivity上显示更新弹窗，其他activity上不显示弹窗;
         * 不设置会默认所有activity都可以显示弹窗;
         */
        Beta.canShowUpgradeActs.add(MainActivity.class);

        Bugly.init(getApplicationContext(), "a5471c79fd", false);

    }

    /**
     * 判断当前进程是否是应用的主进程
     *
     * @return *
     */
    public boolean isMainProcess() {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return getApplicationInfo().packageName.equals(appProcess.processName);
            }
        }
        return false;
    }
}
