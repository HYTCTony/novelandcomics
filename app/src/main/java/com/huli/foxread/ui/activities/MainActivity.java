package com.huli.foxread.ui.activities;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.azhon.appupdate.config.UpdateConfiguration;
import com.azhon.appupdate.dialog.NumberProgressBar;
import com.azhon.appupdate.listener.OnDownloadListener;
import com.azhon.appupdate.manager.DownloadManager;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.huli.foxread.FrApp;
import com.huli.foxread.R;
import com.huli.foxread.RxHttp;
import com.huli.foxread.cache.TokenCache;
import com.huli.foxread.cache.UserInfoCache;
import com.huli.foxread.contact.Common;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.ebsevent.UnReadMsgEvent;
import com.huli.foxread.entity.CapitalEntity;
import com.huli.foxread.entity.FUser;
import com.huli.foxread.entity.LoginRpsEntity;
import com.huli.foxread.entity.UpdateInfo;
import com.huli.foxread.entity.tab.TabEntity;
import com.huli.foxread.rxhttp.OnError;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.ui.fragments.BookStoreBoyFragment;
import com.huli.foxread.ui.fragments.BookStoreSelectionFragment;
import com.huli.foxread.ui.fragments.MainBookrackFragment;
import com.huli.foxread.ui.fragments.MainBookstoreFragment;
import com.huli.foxread.ui.fragments.MainClassifyFragment;
import com.huli.foxread.ui.fragments.MainMineFragment;
import com.huli.foxread.ui.fragments.MainWelfareFragment2;
import com.huli.foxread.utils.DensityUtils;
import com.huli.foxread.utils.NetworkUtil;
import com.huli.foxread.utils.PackageUtils;
import com.huli.foxread.utils.SPFUtils;
import com.huli.foxread.utils.StatusBarUtils;
import com.huli.foxread.utils.Tos;
import com.huli.foxread.utils.UniqueIdManager;
import com.kongzue.dialog.v3.CustomDialog;
import com.kongzue.dialog.v3.TipDialog;
import com.rxjava.rxlife.RxLife;
import com.sh.sdk.shareinstall.ShareInstall;
import com.sh.sdk.shareinstall.autologin.AutoLoginManager;
import com.sh.sdk.shareinstall.autologin.listener.AvoidPwdLoginListener;
import com.sh.sdk.shareinstall.autologin.listener.PreGetNumberListener;
import com.sh.sdk.shareinstall.listener.AppGetWakeUpListener;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import me.leolin.shortcutbadger.ShortcutBadger;

public class MainActivity extends BaseActivity implements OnTabSelectListener, View.OnClickListener {
    private static final long INTERVAL = 2000;  //按两次返回键退出间隔的时间
    private long mExitFirstTime;  //用于暂存第一次按返回键的时间

    public CommonTabLayout mTabLayout;
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private FragmentManager fragmentManager;

    private MainBookstoreFragment bookstoreFragment;
    private MainBookrackFragment bookrackFragment;
    private MainClassifyFragment classifyFragment;
    private MainWelfareFragment2 welfareFragment;
    private MainMineFragment mineFragment;

    private LinearLayout bookRackBottomBar;
    private TextView btnBrSelect, btnBrDelete;
    private ObjectAnimator objectAnimatorY;


    private boolean isInit = true;

    @Override
    protected void setStatusBar() {
        StatusBarUtils.setColor(this, ContextCompat.getColor(this, R.color.transparent), 0);
        StatusBarUtils.setAndroidNativeLightStatusBar(this, true);
    }

    @Override
    public void initParms(Bundle parms) {
    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void initView(View view) {
        bookRackBottomBar = $(R.id.ll_bottom_bar_bookrack);
        bookRackBottomBar.setVisibility(View.GONE);
        btnBrSelect = $(R.id.tv_asBtn_select_all_or_cancel);
        btnBrDelete = $(R.id.tv_asBtn_delete);
        setBrDelNum(0);
        setSelectBtnText(false);

        mTabLayout = $(R.id.cTabLayout_main);
        String[] bottomBarTitles = getResources().getStringArray(R.array.bottom_bar_main);
        mTabEntities.add(new TabEntity(bottomBarTitles[0], R.drawable.tab_bookstore_selected, R.drawable.tab_bookstore_unselected));
        mTabEntities.add(new TabEntity(bottomBarTitles[1], R.drawable.tab_bookrack_selected, R.drawable.tab_bookrack_unselected));
        mTabEntities.add(new TabEntity(bottomBarTitles[2], R.mipmap.tab_classify_selected, R.mipmap.tab_classify_unselected));
        mTabEntities.add(new TabEntity(bottomBarTitles[3], R.drawable.tab_welfare_selected, R.drawable.tab_welfare_unselected));
        mTabEntities.add(new TabEntity(bottomBarTitles[4], R.drawable.tab_mine_selected, R.drawable.tab_mine_unselected));
        mTabLayout.setTabData(mTabEntities);
        fragmentManager = getSupportFragmentManager();
    }

    @Override
    public void setListener() {
        mTabLayout.setOnTabSelectListener(this);
        btnBrSelect.setOnClickListener(this);
        btnBrDelete.setOnClickListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {
        if (NetworkUtil.isNetworkAvailable(mContext)) {
            mTabLayout.postDelayed(this::switch2Bookstore, 100);        //延迟初始化，MainActivity启动时间由2225ms变成676ms
        } else {
            mTabLayout.postDelayed(this::switch2Bookrack, 100);
        }

        // 获取唤醒参数
        ShareInstall.getInstance().getWakeUpParams(getIntent(), wakeUpListener);
        if (ShareInstall.getInstance().isFirstInstall()) {
            ShareInstall.getInstance().getInstallParams(info -> {
                // 客户端获取到的参数是json字符串格式
                Log.d("ShareInstall", "info = " + info);
                try {
                    org.json.JSONObject object = new org.json.JSONObject(info);
                    // 通过该方法拿到设置的渠道值，剩余值为自定义的其他参数
                    String channel = object.optString("channel");
                    String invateCode = object.optString("my_invite_code");
                    SPFUtils.put(MainActivity.this, Common.INVITE_CODE, invateCode);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        }
        /*一键登录预取号*/
        preAvoidPwd1ClickLogin();

        /*友盟推送消息*/
        /*PushAgent.getInstance(this).setMessageHandler(new UmengMessageHandler() {
            @Override
            public void dealWithCustomMessage(Context context, UMessage uMessage) {
                Log.e(TAG, "CustomMessage===" + uMessage.custom);
                //TODO 判断当前Activity显示 然后do something
//                UTrack.getInstance(context).trackMsgArrival(uMessage);
            }
        });*/

       /* InAppMessageManager.getInstance(this).showCardMessage(this, "MainActivity", new IUmengInAppMsgCloseCallback() {
            @Override
            public void onClose() {

            }
        });*/

        reqUserInfo();

        checkNewVersion();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // 此处要调用，否则App在后台运行时，会无法截获
        ShareInstall.getInstance().getWakeUpParams(intent, wakeUpListener);

        //提现页面余额不足，提示去做任务跳转至此
        boolean showWelfare = intent.getBooleanExtra(Common.WITHDRAWAL_DO_TASKS, false);
        if (showWelfare && mTabLayout.getCurrentTab() != 3) {
            switch2Welfare();
        }
    }

    /**
     * 一键登录预取号
     */
    private void preAvoidPwd1ClickLogin() {
        if (!UserInfoCache.getIsTourist(this)) {
            return;
        }
        AutoLoginManager.getInstance().preAvoidPwdLogin(new PreGetNumberListener() {
            @Override
            public void onPreGetNumberSuccess(String secureMobile) {
                Log.e(TAG, "预取号成功：" + secureMobile);
            }

            @Override
            public void onPreGetNumberError(String msg) {
                Log.e(TAG, "预取号失败：" + msg);
            }
        });
    }

    // 注意：SDK调用getWakeUpParams方法获取参数是异步操作，请确保在onGetWakeUpFinish回调中拿到参数后才去处理自己的业务逻辑
    private AppGetWakeUpListener wakeUpListener = info -> {
        // 客户端获取到的参数是json字符串格式
        Log.d("ShareInstall", "info = " + info);
        try {
            org.json.JSONObject object = new org.json.JSONObject(info);
            // 通过该方法拿到设置的渠道值，剩余值为自定义的其他参数
            String channel = object.optString("channel");
            String invateCode = object.optString("my_invite_code");
            if (!ShareInstall.getInstance().isFirstInstall())
                SPFUtils.put(MainActivity.this, Common.INVITE_CODE, invateCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        reqMyCapitalDetail();

        if (!isInit) {
            reqUnReadMsgCount();
        }
        isInit = false;
       /* Stack<Activity> activityStack = FrApp.getInstance().mActivityManager.getActivityStack();
        Log.e(TAG, "activityStack.size===" + activityStack.size());
        boolean mainActExist = false;//栈中是否存在MainActivity
        for (Activity act : activityStack) {
            if (act instanceof BookDetailsActivity) {
                Log.e(TAG, "存在***********");
            }
        }*/

        //清除应用角标
        if ((int) SPFUtils.get(this, Common.SPF_KEY_BADGECOUNT, 0) > 0) {
            ShortcutBadger.removeCount(this);
            SPFUtils.remove(this, Common.SPF_KEY_BADGECOUNT);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wakeUpListener = null;
    }

    @Override
    public void onTabSelect(int position) {
        changeFragment(position);
    }

    @Override
    public void onTabReselect(int position) {
        if (position == 0) {
            try {
                MainBookstoreFragment bookStoreFrag = (MainBookstoreFragment) getSupportFragmentManager().getFragments().get(position);
                Fragment fragment = bookStoreFrag.getCurrentFragment();
                if (fragment != null) {
                    if (fragment instanceof BookStoreSelectionFragment) {
                        ((BookStoreSelectionFragment) fragment).back2Top();
                    } else if (fragment instanceof BookStoreBoyFragment) {
                        ((BookStoreBoyFragment) fragment).back2Top();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 切换Fragment
     */
    private void changeFragment(int position) {
        // 开启一个Fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        switch (position) {
            case 0:
                if (bookstoreFragment == null) {
                    bookstoreFragment = new MainBookstoreFragment();
                    transaction.add(R.id.fl_frag_content_main, bookstoreFragment);
                } else {
                    transaction.show(bookstoreFragment);
                }
                break;

            case 1:
                if (bookrackFragment == null) {
                    bookrackFragment = new MainBookrackFragment();
                    transaction.add(R.id.fl_frag_content_main, bookrackFragment);
                } else {
                    transaction.show(bookrackFragment);
                }
                break;
            case 2:
                if (classifyFragment == null) {
                    classifyFragment = new MainClassifyFragment();
                    transaction.add(R.id.fl_frag_content_main, classifyFragment);
                } else {
                    transaction.show(classifyFragment);
                }
                break;

            case 3:
                if (welfareFragment == null) {
                    welfareFragment = new MainWelfareFragment2();
                    transaction.add(R.id.fl_frag_content_main, welfareFragment);
                    invokeOneClickLogin();
                } else {
                    transaction.show(welfareFragment);
                }
                break;
            case 4:
                if (mineFragment == null) {
                    mineFragment = new MainMineFragment();
                    transaction.add(R.id.fl_frag_content_main, mineFragment);
                    invokeOneClickLogin();
                } else {
                    transaction.show(mineFragment);
                }
                break;
            default:
                break;
        }
//        transaction.commit();   //java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState问题解决
        transaction.commitAllowingStateLoss();   //记得提交事务
    }


    /**
     * 将所有Fragment设置为隐藏
     */
    private void hideFragments(FragmentTransaction transaction) {
        if (bookstoreFragment != null) {
            transaction.hide(bookstoreFragment);
        }
        if (bookrackFragment != null) {
            transaction.hide(bookrackFragment);
        }
        if (classifyFragment != null) {
            transaction.hide(classifyFragment);
        }
        if (welfareFragment != null) {
            transaction.hide(welfareFragment);
        }
        if (mineFragment != null) {
            transaction.hide(mineFragment);
        }
    }

    /**
     * 跳到书城
     */
    public void switch2Bookstore() {
        mTabLayout.setCurrentTab(0);
        changeFragment(0);
    }

    /**
     * 跳到书架
     */
    public void switch2Bookrack() {
        mTabLayout.setCurrentTab(1);
        changeFragment(1);
    }

    /**
     * 跳到福利
     */
    public void switch2Welfare() {
        mTabLayout.setCurrentTab(3);
        changeFragment(3);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_asBtn_select_all_or_cancel:
                if (bookrackFragment != null) {
                    int count = bookrackFragment.funCheckAll(!bookrackFragment.isSelectAll());
                    setBrDelNum(count);
                    setSelectBtnText(bookrackFragment.isSelectAll());
                }
                break;
            case R.id.tv_asBtn_delete:
                if (bookrackFragment != null) {
                    bookrackFragment.delBookShelfData();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 设置删除按钮文字
     *
     * @param num 选中个数
     */
    public void setBrDelNum(int num) {
        if (num > 0) {
            btnBrDelete.setText(String.format(getString(R.string.txt_delete_x), num));
            btnBrDelete.setEnabled(true);
            btnBrDelete.setTextColor(ContextCompat.getColor(this, R.color.txt_orange_ff6600));
        } else {
            btnBrDelete.setText(String.format(getString(R.string.txt_delete_x), 0));
            btnBrDelete.setEnabled(false);
            btnBrDelete.setTextColor(ContextCompat.getColor(this, R.color.txt_orange_FBBF9D));
        }
    }

    /**
     * 设置“全选”或者“取消全选”
     *
     * @param selectAllState 全选状态
     */
    public void setSelectBtnText(boolean selectAllState) {
        if (selectAllState) {
            btnBrSelect.setText(R.string.txt_select_all_cancel);
        } else {
            btnBrSelect.setText(R.string.txt_select_all);
        }
    }

    /**
     * 显示底部书架管理栏
     */
    public void showBrBottomBar() {
        if (objectAnimatorY == null) {
            float[] x = {0f, -DensityUtils.dp2px(this, 56)};
            objectAnimatorY = ObjectAnimator.ofFloat(bookRackBottomBar, "translationY", x);
            objectAnimatorY.setDuration(200);
        }
        objectAnimatorY.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                bookRackBottomBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        objectAnimatorY.start();
    }

    /**
     * 隐藏底部书架管理栏
     */
    public void dismissBrBottomBar() {
        if (objectAnimatorY != null) {
            objectAnimatorY.cancel();
        }
        bookRackBottomBar.animate().translationY(0).setDuration(200).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                bookRackBottomBar.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        if (mTabLayout.getCurrentTab() == 1 && bookrackFragment != null) {
            if (bookrackFragment.isManagerMode()) {
                bookrackFragment.setManagerMode(false);
                return;
            }
        }
        if ((System.currentTimeMillis() - mExitFirstTime) > INTERVAL) {
            Tos.showShort(this, R.string.text_point_out_once_again);
            mExitFirstTime = System.currentTimeMillis();
        } else {
            FrApp.getInstance().exitApp();
        }
//        super.onBackPressed();
    }

    @Override
    protected void onNetWorkResume() {
        super.onNetWorkResume();
        reqMyCapitalDetail();
    }

    private boolean ignoreOneClickLogin;    //忽略唤起一键登录

    /**
     * 调起一键登录
     */
    private void invokeOneClickLogin() {
        if (!ignoreOneClickLogin && UserInfoCache.getIsTourist(this)) {
            obtainOperatorsToken();
            ignoreOneClickLogin = true;
        }
    }

    /**
     * 调起一键登录获取运营商提供的token
     */
    private void obtainOperatorsToken() {
        AutoLoginManager.getInstance().doAvoidPwdLogin(this, new AvoidPwdLoginListener() {
            @Override
            public void onGetLoginTokenSuccess(String operatorType, String token, String secureMobile) {
//                Log.e(TAG, "operatorType>>" + operatorType + ">>token>>" + token + ">>secureMobile>>" + secureMobile);
                /*
                 * operatorType 运营商类型  1电信 2移动 3联通
                 * token 移动联通为登录token    电信为accessCode
                 * secureMobile 移动联通为带星手机号  电信为authCode
                 */
                oneClickLogin(operatorType, token, secureMobile);
                AutoLoginManager.getInstance().closeOperatorActivity();
            }

            @Override
            public void onGetLoginTokenFaild(String operatorType, String code, final String errorMsg) {
                /*
                 *operatorType 运营商类型  1电信 2移动 3联通
                 * code  1001用户关闭授权页取消授权 1002其他错误
                 * errorMsg 错误消息
                 * errorResultCode 运营商返回的错误码
                 */
//                Log.e(TAG, "获取授权码失败：" + errorMsg);
                AutoLoginManager.getInstance().closeOperatorActivity();
            }

            @Override
            public void onOtherWayLogin() {
//                Log.e(TAG, "点击其他登录方式");
                LoginActivity.start(MainActivity.this);
                AutoLoginManager.getInstance().closeOperatorActivity();
            }
        });
    }

    /**
     * 通过运营商一键登录返回的token 调用自身登录
     *
     * @param operatorType #
     * @param uToken       电信运营商的token
     * @param authCode     #
     */
    private void oneClickLogin(String operatorType, String uToken, String authCode) {
        String uniqueID = UniqueIdManager.getUniqueID(this);
        RxHttp.postForm(Consts.USE_PHONE_ONEKEY_LOGIN)
                .add(Consts.TYPE, operatorType)
                .add("authCode", operatorType.equals("1") ? authCode : "")
                .add(Consts.TOKEN, uToken)
                .add("plantFrom", "1")
                .add(Consts.UNIQUE_ID, uniqueID)
                .asResponse(LoginRpsEntity.class)
                .to(RxLife.toMain(this))  //感知生命周期，并在主线程回调
                .subscribe(data -> {
                    TokenCache.saveToken(MainActivity.this, data.getToken());
                    onResume();
                    reqUserInfo();
                }, (OnError) error -> TipDialog.show(MainActivity.this, error.getErrorMsg(), TipDialog.TYPE.ERROR));
    }

    /**
     * 我的资金详情
     */
    public void reqMyCapitalDetail() {
        RxHttp.get(Consts.USER_CAPITAL_API)
                .asResponse(CapitalEntity.class)
                .to(RxLife.toMain(this))  //感知生命周期，并在主线程回调
                .subscribe(data -> {
                    EventBus.getDefault().postSticky(data);
                });
    }

    /**
     * 获取用户信息
     */
    public void reqUserInfo() {
        RxHttp.get(Consts.USERS_INFO_API)
                .asResponse(FUser.class)
                .to(RxLife.toMain(this))  //感知生命周期，并在主线程回调
                .subscribe(fUser -> {
                    UserInfoCache.saveUserInfo(MainActivity.this, fUser);

                    //是否已经填写邀请码
                    boolean isInvited = fUser.isIs_invited();
                    String inviteCode = (String) SPFUtils.get(MainActivity.this, Common.INVITE_CODE, "");
                    if (!isInvited && !TextUtils.isEmpty(inviteCode)) {
                        reqInviteCodeSubmit(inviteCode);
                    }
                    EventBus.getDefault().postSticky(fUser);

                    reqUnReadMsgCount();
                });
    }

    /**
     * 获取未读消息
     */
    private void reqUnReadMsgCount() {
        RxHttp.get(Consts.MSG_UNREAD_API)
                .asResponse(UnReadMsgEvent.class)
                .to(RxLife.toMain(this))  //感知生命周期，并在主线程回调
                .subscribe(unread -> {
                    if (unread.getMessage() > 0) {
                        mTabLayout.showDot(4);
                    } else {
                        mTabLayout.hideMsg(4);
                    }
                    EventBus.getDefault().postSticky(unread);
                });
    }


    /**
     * 提交邀请码
     * token在CallBack中统一加到header
     *
     * @param inviteCode #
     */
    private void reqInviteCodeSubmit(String inviteCode) {
        RxHttp.postForm(Consts.FILLIN_INVITE_CODE_API)
                .add(Consts.CODE, inviteCode)
                                    .asResponse(String.class)
                                    .to(RxLife.toMain(this))  //感知生命周期，并在主线程回调
                                    .subscribe(s -> {
                                        SPFUtils.remove(MainActivity.this, Common.INVITE_CODE);
                                        UserInfoCache.saveIsInvited(MainActivity.this, true);
                });
    }


    /*private void ssss() {
        Observable<FUser> bannerObservable = RxHttp.get("http://...")
                .asClass(FUser.class)
                .onErrorReturn(throwable -> {
                    if (throwable instanceof TimeoutCancellationException) {
                        return UserInfoCache.getUserInfo(this);
                    } else {
                        throw throwable;
                    }
                });
//                .onErrorReturnItem(UserInfoCache.getUserInfo(this));

        //学生的Observable对象
        Observable<UpdateInfo> studentObservable = RxHttp.get("http://...")
                .asClass(UpdateInfo.class);

        //这里使用RxJava组合符中的merge操作符，将两个被观察者合并为一个
        Observable.merge(bannerObservable, studentObservable)
                .to(RxLife.toMain(this)) //感知生命周期，自动关闭请求
                .subscribe(o -> {
                    //请求成功，回调2次，一次是Banner数据，一次Student列表
                    if (o instanceof FUser) {
                        //获取到banner数据
                    } else if (o instanceof UpdateInfo) {
                        //获取到学生列表数据
                    }
                }, throwable -> {
                    //出现异常
                }, () -> {
                    //2个请求执行完毕，开始更新UI
                });
    }*/

    /**
     * 检测更新
     */
    private void checkNewVersion() {
        String channelName = getChannel();
        RxHttp.postForm(Consts.VERSION_CHECK_API)
                .add(Consts.FACILITY, Consts.DEVICE_ANDROID)
                .add(Consts.APK_CHANNEL, channelName)
                .add(Consts.VERSION_CODE, PackageUtils.getVersionCode(this))
                .asResponse(UpdateInfo.class)
                .to(RxLife.toMain(this))  //感知生命周期，并在主线程回调
                .subscribe(updateInfo -> {
                    boolean isForce = updateInfo.getEnforce() == 1;
                    CustomDialog.show(MainActivity.this, R.layout.layout_custom_dialog_version_check, (dialog, v) -> {
                        ImageView btnClose = v.findViewById(R.id.iv_asBtn_close_update);
                        btnClose.setVisibility(isForce ? View.GONE : View.VISIBLE);
                        NumberProgressBar progressBar = v.findViewById((R.id.numberProgressBar_download_apk));
                        progressBar.setVisibility(isForce ? View.VISIBLE : View.GONE);
                        TextView tvVerName = v.findViewById(R.id.tv_new_version_name);
                        tvVerName.setText(("v_" + updateInfo.getVersionName()));
                        TextView tvContent = v.findViewById(R.id.tv_update_info_content);
                        tvContent.setText(updateInfo.getContent());

                        btnClose.setOnClickListener(v1 -> dialog.doDismiss());
                        v.findViewById(R.id.versionchecklib_version_dialog_commit).setOnClickListener(v11 -> {
                            downloadApkTask(updateInfo, progressBar, dialog);
                            if (!isForce) {
                                dialog.doDismiss();
                            }
                        });
                    });
                });
    }

    /**
     * 下载APK
     *
     * @param updateInfo  更新信息
     * @param progressBar 进度条
     * @param dialog      更新提示框
     */
    private void downloadApkTask(UpdateInfo updateInfo, NumberProgressBar progressBar, CustomDialog dialog) {
        if (updateInfo != null) {
            DownloadManager manager = DownloadManager.getInstance(MainActivity.this);
            if (updateInfo.getEnforce() == 1) {
                UpdateConfiguration configuration = new UpdateConfiguration()
                        .setForcedUpgrade(true)
                        .setShowBgdToast(false)
                        //设置下载过程的监听
                        .setOnDownloadListener(new OnDownloadListener() {
                            @Override
                            public void start() {
//                                Log.e("sssssssssss", "start");
                            }

                            @Override
                            public void downloading(int max, int progress) {
                                int curr = (int) (progress / (double) max * 100.0);
                                progressBar.setProgress(curr);
                            }

                            @Override
                            public void done(File apk) {
                                dialog.doDismiss();
                                FrApp.getInstance().exitApp();
                            }

                            @Override
                            public void cancel() {

                            }

                            @Override
                            public void error(Exception e) {

                            }
                        });
                manager.setConfiguration(configuration);
            }
            manager.setApkName("FoxRead.apk")
                    .setApkUrl(updateInfo.getDownloadurl())
                    .setSmallIcon(R.mipmap.app_huli_logo_small)
                    .download();
        }
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

}
