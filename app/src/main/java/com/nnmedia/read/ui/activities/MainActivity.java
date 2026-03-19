package com.nnmedia.read.ui.activities;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.azhon.appupdate.config.UpdateConfiguration;
import com.azhon.appupdate.dialog.NumberProgressBar;
import com.azhon.appupdate.listener.OnDownloadListener;
import com.azhon.appupdate.manager.DownloadManager;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.kongzue.dialog.v3.CustomDialog;
import com.kongzue.dialog.v3.MessageDialog;
import com.kongzue.dialog.v3.TipDialog;
import com.nnmedia.novel.R;
import com.nnmedia.novel.RxHttp;
import com.nnmedia.read.FrApp;
import com.nnmedia.read.cache.UserInfoCache;
import com.nnmedia.read.contact.Common;
import com.nnmedia.read.contact.Consts;
import com.nnmedia.read.ebsevent.UnReadMsgEvent;
import com.nnmedia.read.entity.AppVersionInfo;
import com.nnmedia.read.entity.CapitalEntity;
import com.nnmedia.read.entity.FUser;
import com.nnmedia.read.entity.UpdateInfo;
import com.nnmedia.read.entity.tab.TabEntity;
import com.nnmedia.read.rxhttp.OnError;
import com.nnmedia.read.ui.base.BaseActivity;
import com.nnmedia.read.ui.fragments.BookStoreMainFragment;
import com.nnmedia.read.ui.fragments.BookStoreSelectionFragment;
import com.nnmedia.read.ui.fragments.MainBookrackFragment;
import com.nnmedia.read.ui.fragments.MainBookstoreFragment;
import com.nnmedia.read.ui.fragments.MainClassifyFragment;
import com.nnmedia.read.ui.fragments.MainMineFragment;
import com.nnmedia.read.ui.fragments.MainWelfareFragment2;
import com.nnmedia.read.utils.DateTimeUtil;
import com.nnmedia.read.utils.DensityUtils;
import com.nnmedia.read.utils.NetworkUtil;
import com.nnmedia.read.utils.PackageUtils;
import com.nnmedia.read.utils.SPFUtils;
import com.nnmedia.read.utils.StatusBarUtils;
import com.nnmedia.read.utils.Tos;
import com.nnmedia.read.utils.UniqueIdManager;
import com.nnmedia.read.utils.VerifyDevice;
import com.rxjava.rxlife.RxLife;
import com.tencent.bugly.crashreport.CrashReport;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import me.leolin.shortcutbadger.ShortcutBadger;

//import com.lahm.library.EasyProtectorLib;
//import com.lahm.library.VirtualApkCheckUtil;
//import com.lahm.library.VirtualCheckCallback;

public class MainActivity extends BaseActivity implements OnTabSelectListener, View.OnClickListener {
    private static final long INTERVAL = 2000;  //按两次返回键退出间隔的时间
    private long mExitFirstTime;  //用于暂存第一次按返回键的时间

    public CommonTabLayout mTabLayout;
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private FragmentManager fragmentManager;

    private MainBookstoreFragment bookstoreFragment1;
    private MainBookstoreFragment bookstoreFragment2;
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
        testSimulator();

        bookRackBottomBar = $(R.id.ll_bottom_bar_bookrack);
        bookRackBottomBar.setVisibility(View.GONE);
        btnBrSelect = $(R.id.tv_asBtn_select_all_or_cancel);
        btnBrDelete = $(R.id.tv_asBtn_delete);
        setBrDelNum(0);
        setSelectBtnText(false);

        mTabLayout = $(R.id.cTabLayout_main);
        String[] bottomBarTitles = getResources().getStringArray(R.array.bottom_bar_main);

        mTabEntities.add(new TabEntity(bottomBarTitles[0], R.drawable.ic_tab_nice_selected, R.drawable.ic_tab_nice_unselected));
        mTabEntities.add(new TabEntity(bottomBarTitles[1], R.drawable.ic_tab_book_selected, R.drawable.ic_tab_book_unselected));
        mTabEntities.add(new TabEntity(bottomBarTitles[2], R.drawable.ic_tab_bookrack_selected, R.drawable.ic_tab_bookrack_unselected));
//        mTabEntities.add(new TabEntity(bottomBarTitles[3], R.drawable.tab_welfare_selected, R.drawable.tab_welfare_unselected));
        mTabEntities.add(new TabEntity(bottomBarTitles[4], R.drawable.ic_tab_mine_selected, R.drawable.ic_tab_mine_unselected));
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

        reqUserInfo();

        getCurVersionInfo();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        //提现页面余额不足，提示去做任务跳转至此
        boolean showWelfare = intent.getBooleanExtra(Common.WITHDRAWAL_DO_TASKS, false);
        if (showWelfare && mTabLayout.getCurrentTab() != 3) {
            switch2Welfare();
        }
    }

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
//        wakeUpListener = null;
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
                    } else if (fragment instanceof BookStoreMainFragment) {
                        ((BookStoreMainFragment) fragment).back2Top();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (position == 1) {
            try {
                MainBookstoreFragment bookStoreFrag = (MainBookstoreFragment) getSupportFragmentManager().getFragments().get(position);
                Fragment fragment = bookStoreFrag.getCurrentFragment();
                if (fragment != null) {
                    if (fragment instanceof BookStoreMainFragment) {
                        ((BookStoreMainFragment) fragment).back2Top();
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
                if (bookstoreFragment2 == null) {
                    bookstoreFragment2 = new MainBookstoreFragment(1);
                    transaction.add(R.id.fl_frag_content_main, bookstoreFragment2);
                } else {
                    transaction.show(bookstoreFragment2);
                }
//                if (classifyFragment == null) {
//                    classifyFragment = new MainClassifyFragment();
//                    transaction.add(R.id.fl_frag_content_main, classifyFragment);
//                } else {
//                    transaction.show(classifyFragment);
//                }
                break;
            case 1:
                if (bookstoreFragment1 == null) {
                    bookstoreFragment1 = new MainBookstoreFragment(0);
                    transaction.add(R.id.fl_frag_content_main, bookstoreFragment1);
                } else {
                    transaction.show(bookstoreFragment1);
                }
                break;
            case 2:
                if (bookrackFragment == null) {
                    bookrackFragment = new MainBookrackFragment();
                    transaction.add(R.id.fl_frag_content_main, bookrackFragment);
                } else {
                    transaction.show(bookrackFragment);
                }
                break;
//            case 3:
//                if (welfareFragment == null) {
//                    welfareFragment = new MainWelfareFragment2();
//                    transaction.add(R.id.fl_frag_content_main, welfareFragment);
////                    invokeOneClickLogin();
//                } else {
//                    transaction.show(welfareFragment);
//                }
//                break;
            case 3:
                if (mineFragment == null) {
                    mineFragment = new MainMineFragment();
                    transaction.add(R.id.fl_frag_content_main, mineFragment);
//                    invokeOneClickLogin();
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
        if (bookstoreFragment1 != null) {
            transaction.hide(bookstoreFragment1);
        }
        if (bookstoreFragment2 != null) {
            transaction.hide(bookstoreFragment2);
        }
        if (bookrackFragment != null) {
            transaction.hide(bookrackFragment);
        }
        if (classifyFragment != null) {
            transaction.hide(classifyFragment);
        }
//        if (welfareFragment != null) {
//            transaction.hide(welfareFragment);
//        }
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
        String uniqueID = UniqueIdManager.getUniqueID(this);
        RxHttp.postForm(Consts.USERS_INFO_API)
                .add(Consts.UNIQUE_ID, uniqueID)
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

    /**
     * 获取版本详情
     */
    private void getCurVersionInfo() {
        RxHttp.postForm(Consts.VERSION_DETAIL_API)
                .setDomainToBaseUrlGXIfAbsent()
                .add(Consts.FACILITY, Consts.DEVICE_ANDROID)
                .add(Consts.APK_CHANNEL, getChannel())
                .add(Consts.VERSION_CODE, PackageUtils.getVersionCode(this))
                .asResponse(AppVersionInfo.class)
                .to(RxLife.toMain(this))
                .subscribe(this::loadUpgradeInfo, (OnError) error -> {
                        checkNewVersion();
                });
    }

    private void loadUpgradeInfo(AppVersionInfo appVersionInfo) {
        if (appVersionInfo.getNewVersion() != null) {
            checkNewVersion();
        }
    }

    /**
     * 检测更新
     */
    private void checkNewVersion() {
        String channelName = getChannel();
        RxHttp.postForm(Consts.VERSION_CHECK_API)
                .setDomainToBaseUrlGXIfAbsent()
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
                    }).setCancelable(false);
                }, (OnError) error -> {
                    int code = error.getErrorCode();
                    if (code == 1) {

                    } else {
                        goOfficialWebsite();
                    }
                });
    }

    private void goOfficialWebsite() {
        String title = "系统提示";
        String message = "APP更新功能已经损坏，请移步官网下载最新版本。";
        if (TextUtils.isEmpty(message)) {
            Toast.makeText(this, "找不到相关信息~", Toast.LENGTH_SHORT).show();
            return;
        }
        MessageDialog.show(MainActivity.this, title, message, "前往官网", "下次再说")
                .setCancelable(false)
                .setOnOkButtonClickListener((baseDialog, v) -> {
                    jumpOutSide("https://htxs.app/");
                    baseDialog.doDismiss();
                    return false;
                })
                .setOnCancelButtonClickListener((baseDialog, v) -> {
                    baseDialog.doDismiss();
                    return false;
                });
    }

    private void jumpOutSide(String link) {
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(link));
        // 注意此处的判断intent.resolveActivity()可以返回显示该Intent的Activity对应的组件名
        // 官方解释 : Name of the component implementing an activity that can display the intent
        if (intent.resolveActivity(getPackageManager()) != null) {
            final ComponentName componentName = intent.resolveActivity(getPackageManager());
            startActivity(Intent.createChooser(intent, "请选择浏览器"));
        } else {
            TipDialog.show(this, "链接错误或无浏览器", TipDialog.TYPE.ERROR);
        }
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
            manager.setApkName("dolphin_novel.apk")
                    .setApkUrl(updateInfo.getDownloadurl())
                    .setSmallIcon(R.mipmap.app_logo)
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

    private void tipNewManGetVip() {
        CustomDialog.show(MainActivity.this, R.layout.layout_custom_dialog_get_free_vip, (dialog, v) -> {
            ImageView btnClose = v.findViewById(R.id.iv_asBtn_close_update);
            btnClose.setOnClickListener(v1 -> dialog.doDismiss());

            v.findViewById(R.id.ctl_cloud_title).setOnClickListener(v11 -> {
                if (UserInfoCache.getIsTourist(MainActivity.this)) {
                    LoginActivity2.start(this);
                } else {
                    if (UserInfoCache.isGetFree(this)) {
                        FreeGetVipActivity.start(this);
                    } else {
                        Toast.makeText(this, "您已经领取过VIP，请继续阅读吧。", Toast.LENGTH_SHORT).show();
                        dialog.doDismiss();
                    }
                }
            });
        }).setCancelable(false);
    }

    private void testSimulator() {
        if (VerifyDevice.verify()) {
            MessageDialog.show(MainActivity.this, "提示", "检测到您使用模拟器登录app，您将无法参与邀请好友活动以及新人奖励活动。", "知道了")
                    .setCancelable(false)
                    .setOnOkButtonClickListener((baseDialog, v) -> {
                        baseDialog.doDismiss();
                        return false;
                    });
        }
//        else if (EasyProtectorLib.checkIsRoot() || EasyProtectorLib.checkIsXposedExist()) {
//            MessageDialog.show(MainActivity.this, "警告", "检测到您使用框架运行app，禁止实用hook操作！", "退出")
//                    .setCancelable(false)
//                    .setOnOkButtonClickListener((baseDialog, v) -> {
//                        finish();
//                        baseDialog.doDismiss();
//                        return false;
//                    });
//        } else if (VirtualApkCheckUtil.getSingleInstance().checkByPrivateFilePath(this, virtualCheckCallback)) {
//            MessageDialog.show(MainActivity.this, "提示", "检测到您使用分身或者多开登录app，您将无法参与邀请好友活动以及新人奖励活动。", "知道了")
//                    .setCancelable(false)
//                    .setOnOkButtonClickListener((baseDialog, v) -> {
//                        baseDialog.doDismiss();
//                        return false;
//                    });
//        } else if (VirtualApkCheckUtil.getSingleInstance().checkByOriginApkPackageName(this, virtualCheckCallback)) {
//            MessageDialog.show(MainActivity.this, "提示", "检测到您使用分身或者多开登录app，您将无法参与邀请好友活动以及新人奖励活动。", "知道了")
//                    .setCancelable(false)
//                    .setOnOkButtonClickListener((baseDialog, v) -> {
//                        baseDialog.doDismiss();
//                        return false;
//                    });
//        } else if (VirtualApkCheckUtil.getSingleInstance().checkByHasSameUid(virtualCheckCallback)) {
//            MessageDialog.show(MainActivity.this, "提示", "检测到您使用分身或者多开登录app，您将无法参与邀请好友活动以及新人奖励活动。", "知道了")
//                    .setCancelable(false)
//                    .setOnOkButtonClickListener((baseDialog, v) -> {
//                        baseDialog.doDismiss();
//                        return false;
//                    });
//        } else if (VirtualApkCheckUtil.getSingleInstance().checkByMultiApkPackageName(virtualCheckCallback)) {
//            MessageDialog.show(MainActivity.this, "提示", "检测到您使用分身或者多开登录app，您将无法参与邀请好友活动以及新人奖励活动。", "知道了")
//                    .setCancelable(false)
//                    .setOnOkButtonClickListener((baseDialog, v) -> {
//                        baseDialog.doDismiss();
//                        return false;
//                    });
//        }
        else {
//            if (UserInfoCache.isGetFree(MainActivity.this)) {
//                tipNewManGetVip();
//            }
//            EmuCheckUtil.checkEmulatorFromCache(getApplicationContext(),
//                    new EmuCheckUtil.CheckEmulatorCallBack() {
//                        @Override
//                        public void onCheckSuccess(boolean isEmulator) {
//                            if (isEmulator) {
//                                MessageDialog.show(MainActivity.this, "提示", "检测到您使用模拟器登录app，您将无法参与邀请好友活动以及新人奖励活动。", "知道了")
//                                        .setCancelable(false)
//                                        .setOnOkButtonClickListener((baseDialog, v) -> {
//                                            baseDialog.doDismiss();
//                                            return false;
//                                        });
//                            } else {
//                                if (UserInfoCache.isGetFree(MainActivity.this)) {
//                                    tipNewManGetVip();
//                                }
//                            }
//
//                        }
//
//                        @Override
//                        public void onCheckFaild() {
//                            MessageDialog.show(MainActivity.this, "提示", "检测到您使用模拟器登录app，您将无法参与邀请好友活动以及新人奖励活动。", "知道了")
//                                    .setCancelable(false)
//                                    .setOnOkButtonClickListener((baseDialog, v) -> {
//                                        baseDialog.doDismiss();
//                                        return false;
//                                    });
//                        }
//                    });
        }
    }

//    VirtualCheckCallback virtualCheckCallback = new VirtualCheckCallback() {
//        @Override
//        public void findSuspect() {
//            MessageDialog.show(MainActivity.this, "提示", "检测到您使用分身或者多开登录app，您将无法参与邀请好友活动以及新人奖励活动。", "知道了")
//                    .setCancelable(false)
//                    .setOnOkButtonClickListener((baseDialog, v) -> {
//                        baseDialog.doDismiss();
//                        return false;
//                    });
//        }
//    };
}