package com.huli.foxread.ui.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.huli.foxread.FrApp;
import com.huli.foxread.R;
import com.huli.foxread.ebsevent.NetworkChangeEvent;
import com.huli.foxread.receivers.NetworkConnectChangedReceiver;
import com.huli.foxread.ui.activities.LoginActivity;
import com.huli.foxread.utils.StatusBarUtils;
import com.huli.foxread.utils.Tos;
import com.kongzue.dialog.v3.CustomDialog;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;


public abstract class BaseActivity extends AppCompatActivity {

    //忽略网络状态提示
    protected boolean ignoreHint = true;
    //网络状态监听库
    private NetworkConnectChangedReceiver netWorkStateReceiver;

    /**
     * 当前Activity渲染的视图View
     */
    private View mContextView = null;
    /**
     * 日志输出标志
     */
    protected final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
        } catch (Exception e) {
            e.printStackTrace();
            //处置应用后台化后系统对内存回收导致的应用崩溃
            Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            //LogUtils.d("重启");
            finish();
        }

        setStatusBar();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            initParms(bundle);
        }

        View mView = bindView();
        if (null == mView) {
            mContextView = LayoutInflater.from(this).inflate(bindLayout(), null);
        } else mContextView = mView;

        setContentView(mContextView);

        initView(mContextView);
        setListener();
        doBusiness(this);

        EventBus.getDefault().register(this);

        //在所有的Activity 的onCreate 方法或在应用的BaseActivity的onCreate方法中添加
        PushAgent.getInstance(this).onAppStart();
    }

    @Override
    protected void onResume() {
        if (netWorkStateReceiver == null) {
            netWorkStateReceiver = new NetworkConnectChangedReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkStateReceiver, filter);


        MobclickAgent.onResume(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(netWorkStateReceiver);

        MobclickAgent.onPause(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
//        OkGo.getInstance().cancelAll();
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetworkChangeEvent(NetworkChangeEvent event) {      //继承BaseActivity的每个页面都会接到网络状态变化通知
        if (isTopActivity()) {      //判断处于栈顶的Activity才处理
            handleNetWorkChange(event.isConnected);
        }
    }

    //处理网络变化提示信息
    protected void handleNetWorkChange(boolean has) {
        if (has) {
            //一开始就有网络，忽略提示
            if (!ignoreHint) {
                Tos.showShort(this, R.string.txt_welcome_back_net);
                onNetWorkResume();
            }
            ignoreHint = true;
        } else {
//            Tos.showShort(this, R.string.txt_radio_wave_connection_interruption);
            ignoreHint = false;
        }
    }

    //没网络-->有网络
    protected void onNetWorkResume() {
    }


    /**
     * [初始化参数] *
     *
     * @param parms *
     */
    public abstract void initParms(Bundle parms);

    /**
     * [绑定视图] *
     *
     * @return *
     */
    public abstract View bindView();

    /**
     * [绑定布局] * * @return
     */
    public abstract int bindLayout();

    /**
     * [初始化控件] *
     *
     * @param view *
     */
    public abstract void initView(final View view);

    /**
     * [绑定控件] *
     *
     * @param resId *
     * @return *
     */
    @SuppressWarnings("unchecked")
    protected <T extends View> T $(@IdRes int resId) {
        return (T) super.findViewById(resId);
    }

    /**
     * [设置监听]
     */
    public abstract void setListener();


    /**
     * [业务操作]
     *
     * @param mContext 上下文
     */
    public abstract void doBusiness(Context mContext);

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //处理打开的Activity需要登录的情况
        if (requestCode == LoginActivity.REQCODE_LOGIN) {
            if (resultCode == RESULT_OK) {
                doBusiness(this);
            } else {
                finish();
            }
        }
    }

    protected void setStatusBar() {
        StatusBarUtils.setColor(this, ContextCompat.getColor(this, R.color.white), 0);
        StatusBarUtils.setAndroidNativeLightStatusBar(this, true);
    }

    /**
     * 为子类提供设置标题的方法
     *
     * @param title 标题名
     */
    @SuppressWarnings("ConstantConditions")
    protected void initToolBar(Toolbar mToolbar, String title) {
        if (mToolbar == null) {
            throw new IllegalArgumentException("Toolbar must not be null");
        }
        mToolbar.setTitle("");
        TextView tvTitle = $(R.id.tv_toolbar_center_title);
        tvTitle.setText(title);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    @SuppressWarnings("ConstantConditions")
    protected void initToolBar(Toolbar mToolbar, @StringRes int resId) {
        if (mToolbar == null) {
            throw new IllegalArgumentException("Toolbar must not be null");
        }
        mToolbar.setTitle("");
        TextView tvTitle = $(R.id.tv_toolbar_center_title);
        tvTitle.setText(resId);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(v -> onBackPressed());
    }


    private boolean isTopActivity() {
        boolean isTop = false;
        Activity current = FrApp.getInstance().mActivityManager.getCurrentActivity();
        if (current.getClass().getSimpleName().equals(TAG)) {
            isTop = true;
        }
//        Log.e("ssssss", "顶层===" + current.getClass().getSimpleName() + "----" + isTop);
        return isTop;
    }


    /*
     * 设置 app 字体不随系统字体设置改变
     */
    /*@Override
    public Resources getResources() {
        Resources res = super.getResources();
        if (res != null) {
            Configuration config = res.getConfiguration();
            if (config != null && config.fontScale != 1.0f) {
                config.fontScale = 1.0f;
                res.updateConfiguration(config, res.getDisplayMetrics());
            }
        }
        return res;
    }*/


    /**
     * 设置 app 不随着系统字体的调整而变化
     */
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }


    private static long lastClickTime;                //最后一次点击的时间

    /**
     * 无效的连续点击会重置 间隔时间
     *
     * @return 是否点击过快
     */
    protected boolean onMoreClick() {
        boolean flag = false;
        long time = System.currentTimeMillis() - lastClickTime;
        if (time < 600) {
            flag = true;
        }
        lastClickTime = System.currentTimeMillis();
        return flag;
    }


    private CustomDialog loadingDialog;

    protected void showLoadingDialog() {
        loadingDialog = CustomDialog.show(this, R.layout.layout_loadingview, (dialog, v) -> {
        });
    }

    protected void dismissLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShow) {
            loadingDialog.doDismiss();
            loadingDialog = null;
        }
    }

}
