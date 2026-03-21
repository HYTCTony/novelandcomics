package com.nnmedia.read;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;

import com.appsflyer.AppsFlyerLib;
import com.chad.library.adapter.base.module.LoadMoreModuleConfig;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.util.DialogSettings;
import com.kongzue.dialog.util.TextInfo;
import com.nnmedia.comics.core.Storage;
import com.nnmedia.comics.helper.DBOpenHelper;
import com.nnmedia.comics.manager.PreferenceManager;
import com.nnmedia.comics.saf.DocumentFile;
import com.nnmedia.novel.BuildConfig;
import com.nnmedia.novel.R;
import com.nnmedia.page.model.dao.DaoMaster;
import com.nnmedia.page.model.dao.DaoSession;
import com.nnmedia.read.callbacks.ActivityState;
import com.nnmedia.read.callbacks.MyActivityManager;
import com.nnmedia.read.rxhttp.RxHttpManager;
import com.nnmedia.read.ui.views.MyLoadMoreView;
import com.nnmedia.read.utils.PackageUtils;
import com.nnmedia.read.utils.UniqueIdManager;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.FalsifyFooter;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.commonsdk.UMConfigure;

import androidx.core.content.ContextCompat;
import androidx.multidex.MultiDex;

import org.greenrobot.greendao.identityscope.IdentityScopeType;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

public class FrApp extends Application implements ActivityState {

    private static FrApp sInstance;

    public static FrApp getInstance() {
        return sInstance;
    }

    public MyActivityManager mActivityManager = MyActivityManager.getInstance(this);

    private DocumentFile mDocumentFile;
    private static PreferenceManager mPreferenceManager;
    private static OkHttpClient mHttpClient;
    private static WifiManager manager_wifi;

    private DaoSession mDaoSession;

    public static int mWidthPixels;
    public static int mHeightPixels;
    public static int mCoverWidthPixels;
    public static int mCoverHeightPixels;
    public static int mLargePixels;

    //static 代码段可以防止内存泄露
    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> {
            layout.setPrimaryColorsId(R.color.transparent, R.color.col_theme_blue);//全局设置主题颜色
            return new MaterialHeader(context);
//                return new ClassicsHeader(context).setSpinnerStyle(SpinnerStyle.Scale);
//                        .setPrimaryColorId(R.color.colorPrimary)
//                        .setAccentColorId(android.R.color.white);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) -> {
            return new FalsifyFooter(context);
            //指定为经典Footer，默认是 BallPulseFooter
//                return new ClassicsFooter(context).setDrawableSize(20);
        });
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        // 初始化 PreferenceManager
        mPreferenceManager = new PreferenceManager(this);

        RxHttpManager.init(this, BuildConfig.LOG_DEBUG);

        registerActivityLifecycleCallbacks(mActivityManager);
//        registerActivityLifecycleCallbacks(ParallaxHelper.getInstance());

        initKzDialog();      //空祖家的对话框
        LoadMoreModuleConfig.setDefLoadMoreView(new MyLoadMoreView());

        initLog();

        CrashReport.initCrashReport(getApplicationContext());

        AppsFlyerLib.getInstance().init("ST4g6TGX5o3NgFKJhdisEU", null, this);
        AppsFlyerLib.getInstance().start(this);
//        AppsFlyerLib.getInstance().setCustomerUserId(<MY_CUID>);


        UMConfigure.preInit(this,"67042ad480464b33f6d54119","WebChannel");

        //初始化组件化基础库, 所有友盟业务SDK都必须调用此初始化接口。
        UMConfigure.init(this, "67042ad480464b33f6d54119", "WebChannel", UMConfigure.DEVICE_TYPE_PHONE, "");

        manager_wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        initPixels();

        DBOpenHelper helper = new DBOpenHelper(this, "comics.db");
        mDaoSession = new DaoMaster(helper.getWritableDatabase()).newSession(IdentityScopeType.None);
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

    public static PreferenceManager getPreferenceManager() {
        return mPreferenceManager;
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

    protected void initLog() {
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)
                .methodCount(1)
                .tag("haitunxiaoshuo")
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy) {
            @Override
            public boolean isLoggable(int priority, String tag) {
                return BuildConfig.LOG_DEBUG;
            }
        });
    }

    /**
     * 空祖家的对话框
     */
    private void initKzDialog() {
        DialogSettings.init();
        DialogSettings.DEBUGMODE = BuildConfig.LOG_DEBUG;
//        DialogSettings.backgroundColor = Color.BLUE;
//        DialogSettings.titleTextInfo = new TextInfo().setFontSize(50);
        DialogSettings.buttonTextInfo = new TextInfo().setFontColor(ContextCompat.getColor(this, R.color.txt_gray));
        DialogSettings.buttonPositiveTextInfo = new TextInfo().setFontColor(ContextCompat.getColor(this, R.color.txt_red));
        DialogSettings.contentTextInfo = new TextInfo().setGravity(Gravity.START);
        DialogSettings.style = DialogSettings.STYLE.STYLE_MATERIAL;
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

    private void initPixels() {
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
        mWidthPixels = metrics.widthPixels;
        mHeightPixels = metrics.heightPixels;
        mCoverWidthPixels = mWidthPixels / 3;
        mCoverHeightPixels = mHeightPixels * mCoverWidthPixels / mWidthPixels;
        mLargePixels = 3 * metrics.widthPixels * metrics.heightPixels;
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

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public static OkHttpClient getHttpClient() {

        //OkHttpClient返回null实现"仅WiFi联网"，后面要注意空指针处理
        if (!manager_wifi.isWifiEnabled() && mPreferenceManager.getBoolean(PreferenceManager.PREF_OTHER_CONNECT_ONLY_WIFI, false)) {
            return null;
        }

        if (mHttpClient == null) {

            // 3.OkHttp访问https的Client实例
            mHttpClient = new OkHttpClient().newBuilder()
                    .sslSocketFactory(createSSLSocketFactory())
                    .hostnameVerifier(new TrustAllHostnameVerifier())
                    .followRedirects(true)
                    .followSslRedirects(true)
                    .retryOnConnectionFailure(true)
                    .build();
        }

        return mHttpClient;
    }

    // 1.实现X509TrustManager接口
    private static class TrustAllCerts implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    // 2.实现HostnameVerifier接口
    private static class TrustAllHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());

            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }

        return ssfFactory;
    }

    public DocumentFile getDocumentFile() {
        if (mDocumentFile == null) {
            initRootDocumentFile();
        }
        return mDocumentFile;
    }

    public void initRootDocumentFile() {
        String uri = mPreferenceManager.getString(PreferenceManager.PREF_OTHER_STORAGE);
        mDocumentFile = Storage.initRoot(this, uri);
    }
}