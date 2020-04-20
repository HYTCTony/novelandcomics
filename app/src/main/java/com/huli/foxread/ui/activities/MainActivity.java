package com.huli.foxread.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.allenliu.versionchecklib.core.http.HttpParams;
import com.allenliu.versionchecklib.v2.AllenVersionChecker;
import com.allenliu.versionchecklib.v2.builder.DownloadBuilder;
import com.allenliu.versionchecklib.v2.builder.UIData;
import com.allenliu.versionchecklib.v2.callback.RequestVersionListener;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.huli.foxread.FrApp;
import com.huli.foxread.R;
import com.huli.foxread.cache.TokenCache;
import com.huli.foxread.cache.UserInfoCache;
import com.huli.foxread.callbacks.ookkggoo.LtbCallback;
import com.huli.foxread.callbacks.ookkggoo.LtbJsonCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Common;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.entity.CapitalEntity;
import com.huli.foxread.entity.FUser;
import com.huli.foxread.entity.LoginRpsEntity;
import com.huli.foxread.entity.UpdateInfo;
import com.huli.foxread.entity.eventbus.ReadingTimeEvent;
import com.huli.foxread.entity.tab.TabEntity;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.ui.fragments.BookStoreBoyFragment;
import com.huli.foxread.ui.fragments.BookStoreSelectionFragment;
import com.huli.foxread.ui.fragments.MainBookrackFragment;
import com.huli.foxread.ui.fragments.MainBookstoreFragment;
import com.huli.foxread.ui.fragments.MainMineFragment;
import com.huli.foxread.ui.fragments.MainWelfareFragment;
import com.huli.foxread.utils.SPFUtils;
import com.huli.foxread.utils.StatusBarUtils;
import com.huli.foxread.utils.Tos;
import com.huli.foxread.utils.UniqueIdManager;
import com.kongzue.dialog.v3.FullScreenDialog;
import com.kongzue.dialog.v3.TipDialog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.sh.sdk.shareinstall.ShareInstall;
import com.sh.sdk.shareinstall.autologin.AutoLoginManager;
import com.sh.sdk.shareinstall.autologin.listener.AvoidPwdLoginListener;
import com.sh.sdk.shareinstall.autologin.listener.PreGetNumberListener;
import com.sh.sdk.shareinstall.listener.AppGetWakeUpListener;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends BaseActivity implements OnTabSelectListener {
    private static final long INTERVAL = 2000;  //按两次返回键退出间隔的时间
    private long mExitFirstTime;  //用于暂存第一次按返回键的时间

    private CommonTabLayout mTabLayout;
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private FragmentManager fragmentManager;

    private MainBookstoreFragment bookstoreFragment;
    private MainBookrackFragment bookrackFragment;
    private MainWelfareFragment welfareFragment;
    private MainMineFragment mineFragment;

    private boolean hasGetUserInfo = false;

    //预取号成功标记
    private boolean flagPreGetSuccess;

    @Override
    protected void setStatusBar() {
        StatusBarUtils.setColor(this, ContextCompat.getColor(this, R.color.transparent), 0);
        StatusBarUtils.setAndroidNativeLightStatusBar(this, true);
    }

    @Override
    public void initParms(Bundle parms) {
        if (parms != null) {
            hasGetUserInfo = parms.getBoolean(Common.EXTRA_HAS_GET_USERINFO, false);
        }
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
        mTabLayout = $(R.id.cTabLayout_main);
        String[] bottomBarTitles = getResources().getStringArray(R.array.bottom_bar_main);
        mTabEntities.add(new TabEntity(bottomBarTitles[0], R.drawable.tab_bookstore_selected, R.drawable.tab_bookstore_unselected));
        mTabEntities.add(new TabEntity(bottomBarTitles[1], R.drawable.tab_bookrack_selected, R.drawable.tab_bookrack_unselected));
        mTabEntities.add(new TabEntity(bottomBarTitles[2], R.drawable.tab_welfare_selected, R.drawable.tab_welfare_unselected));
        mTabEntities.add(new TabEntity(bottomBarTitles[3], R.drawable.tab_mine_selected, R.drawable.tab_mine_unselected));
        mTabLayout.setTabData(mTabEntities);
        fragmentManager = getSupportFragmentManager();
    }

    @Override
    public void setListener() {
        mTabLayout.setOnTabSelectListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {
        switch2Bookstore();
        if (!hasGetUserInfo) {
            reqUserInfo();
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
       /* PushAgent pushAgent = PushAgent.getInstance(this);
        pushAgent.setMessageHandler(new UmengMessageHandler(){
            @Override
            public void dealWithCustomMessage(Context context, UMessage uMessage) {
                Log.e(TAG, "CustomMessage===" + uMessage.custom);
                //TODO 判断当前Activity显示 然后do something
//                UTrack.getInstance(context).trackMsgArrival(uMessage);
            }
        });

        InAppMessageManager.getInstance(this).showCardMessage(this, "MainActivity", new IUmengInAppMsgCloseCallback() {
            @Override
            public void onClose() {

            }
        });*/


        //获取唤醒参数
//      OpenInstall.getWakeUp(getIntent(), wakeUpAdapter);
//
//      OpenInstall.getInstall(new AppInstallAdapter() {
//          @Override
//          public void onInstall(AppData appData) {
//              //获取渠道数据
//              String channelCode = appData.getChannel();
//              //获取自定义数据
//              String bindData = appData.getData();
//              Log.d("OpenInstall", "OpenInstall : installData = " + appData.toString());
//              Toast.makeText(mContext, "OpenInstall : installData_install = " + appData.toString(), Toast.LENGTH_LONG).show();
//          }
//       });

//        checkNewVersion();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // 此处要调用，否则App在后台运行时，会无法截获
//        OpenInstall.getWakeUp(intent, wakeUpAdapter);
        ShareInstall.getInstance().getWakeUpParams(intent, wakeUpListener);
    }

//    AppWakeUpAdapter wakeUpAdapter = new AppWakeUpAdapter() {
//        @Override
//        public void onWakeUp(AppData appData) {
//            //获取渠道数据
//            String channelCode = appData.getChannel();
//            //获取绑定数据
//            String bindData = appData.getData();
//            Log.d("OpenInstall", "getWakeUp : wakeupData = " + appData.toString());
//            Toast.makeText(MainActivity.this, "OpenInstall_wake : installData = " + appData.toString(), Toast.LENGTH_LONG).show();
//        }
//    };

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
                flagPreGetSuccess = true;
            }

            @Override
            public void onPreGetNumberError(String msg) {
                Log.e(TAG, "预取号失败：" + msg);
            }
        });
    }

    // 注意：SDK调用getWakeUpParams方法获取参数是异步操作，请确保在onGetWakeUpFinish回调中拿到参数后才去处理自己的业务逻辑
    private AppGetWakeUpListener wakeUpListener = new AppGetWakeUpListener() {
        @Override
        public void onGetWakeUpFinish(String info) {
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
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        reqMyCapitalDetail();

        getUserReadTime();

       /* Stack<Activity> activityStack = FrApp.getInstance().mActivityManager.getActivityStack();
        Log.e(TAG, "activityStack.size===" + activityStack.size());
        boolean mainActExist = false;//栈中是否存在MainActivity
        for (Activity act : activityStack) {
            if (act instanceof BookDetailsActivity) {
                Log.e(TAG, "存在***********");
            }
        }*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        wakeUpAdapter = null;
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
                int currentTab = bookStoreFrag.slidingTabLayout.getCurrentTab();
                Fragment fragment = bookStoreFrag.getChildFragmentManager().getFragments().get(currentTab);
                if (fragment instanceof BookStoreSelectionFragment) {
                    BookStoreSelectionFragment selectionBookFrag = (BookStoreSelectionFragment) fragment;
                    if (selectionBookFrag.recyclerView.canScrollVertically(-1)) {           //判断RecyclerView是否在顶部
                        selectionBookFrag.recyclerView.smoothScrollToPosition(0);
                    }
                } else if (fragment instanceof BookStoreBoyFragment) {
                    BookStoreBoyFragment bsbFrag = (BookStoreBoyFragment) fragment;
                    if (bsbFrag.recyclerView.canScrollVertically(-1)) {                     //判断RecyclerView是否在顶部
                        bsbFrag.recyclerView.smoothScrollToPosition(0);
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
                if (welfareFragment == null) {
                    welfareFragment = new MainWelfareFragment();
                    transaction.add(R.id.fl_frag_content_main, welfareFragment);
                    sendShowLoginDialogMsg();
                } else {
                    transaction.show(welfareFragment);
                }
                break;

            case 3:
                if (mineFragment == null) {
                    mineFragment = new MainMineFragment();
                    transaction.add(R.id.fl_frag_content_main, mineFragment);
                    sendShowLoginDialogMsg();
                } else {
                    transaction.show(mineFragment);
                }
                break;
            default:
                break;
        }
        transaction.commit();   //记得提交事务
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
     * 跳到福利
     */
    public void switch2Welfare() {
        mTabLayout.setCurrentTab(2);
        changeFragment(2);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitFirstTime) > INTERVAL) {
                Tos.showShort(this, R.string.text_point_out_once_again);
                mExitFirstTime = System.currentTimeMillis();
            } else {
                FrApp.getInstance().exitApp();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private boolean ignoreLoginDialog;

    private void sendShowLoginDialogMsg() {
        if (!ignoreLoginDialog) {
            mHandler.sendEmptyMessageDelayed(121, 500);
            ignoreLoginDialog = true;
        }
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == 121) {
                /* 一键登陆弹窗 */
                if (UserInfoCache.getIsTourist(MainActivity.this) && flagPreGetSuccess) {
                    FullScreenDialog.build(MainActivity.this)
                            .setCustomView(R.layout.dialog_full_screen_one_click_login, (dialog, rootView) -> {
                                Button btnGo2Login = rootView.findViewById(R.id.btn_one_click_go2_login);
                                TextView btnOtherWays = rootView.findViewById(R.id.tv_asBtn_other_ways_2_login);
                                btnGo2Login.setOnClickListener(v -> {
                                    oneClickLogin();
                                    dialog.doDismiss();
                                });
                                btnOtherWays.setOnClickListener(v -> {
                                    LoginActivity.start(MainActivity.this);
                                    dialog.doDismiss();
                                });
                            })
                            .setOkButton("")
                            .setCancelButton(R.string.txt_cancel)
                            .setTitle(R.string.txt_one_click_login).show();
                }
            }
            return false;
        }
    });

    /**
     * 调起一键登录获取运营商提供的token
     */
    private void oneClickLogin() {
        AutoLoginManager.getInstance().doAvoidPwdLogin(this, new AvoidPwdLoginListener() {
            @Override
            public void onGetLoginTokenSuccess(String operatorType, String token, String secureMobile) {
//                Log.e(TAG, "operatorType>>" + operatorType + ">>token>>" + token + ">>secureMobile>>" + secureMobile);
                /*
                 * operatorType 运营商类型  1电信 2移动 3联通
                 * token 移动联通为登录token    电信为accessCode
                 * secureMobile 移动联通为带星手机号  电信为authCode
                 */
                getPhoneNum(operatorType, token, secureMobile);
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
                Log.e(TAG, "点击其他登录方式");
                AutoLoginManager.getInstance().closeOperatorActivity();
            }
        });
    }

    /**
     * 通过运营商一键登录返回的token 调用自身登录
     *
     * @param operatorType
     * @param uToken       电信运营商的token
     * @param authCode
     */
    private void getPhoneNum(String operatorType, String uToken, String authCode) {
        String uniqueID = UniqueIdManager.getUniqueID(this);
        OkGo.<LzyResponse<LoginRpsEntity>>post(Consts.USE_PHONE_ONEKEY_LOGIN)
                .params(Consts.TYPE, operatorType)
                .params("authCode", operatorType.equals("1") ? authCode : "")
                .params(Consts.TOKEN, uToken)
                .params("plantFrom", "1")
                .params(Consts.UNIQUE_ID, uniqueID)
                .execute(new LtbJsonCallback<LzyResponse<LoginRpsEntity>>(this, false,
                        new TypeReference<LzyResponse<LoginRpsEntity>>() {
                        }) {
                    @Override
                    public void onSuccess(Response<LzyResponse<LoginRpsEntity>> response) {
                        if (response.body().error_code == 0) {
                            LoginRpsEntity data = response.body().getData();
                            TokenCache.saveToken(MainActivity.this, data.getToken());

                            onResume();

                            reqUserInfo();
                        } else {
                            TipDialog.show(MainActivity.this, response.body().msg, TipDialog.TYPE.ERROR);
                        }
                    }
                });
    }

    /**
     * 我的资金详情
     */
    public void reqMyCapitalDetail() {
        OkGo.<String>get(Consts.USER_CAPITAL_API)
                .execute(new LtbCallback(this, false) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<CapitalEntity> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<CapitalEntity>>() {
                                });
                        if (entity.error_code == 0) {
                            CapitalEntity data = entity.getData();
                            EventBus.getDefault().postSticky(data);
                        }
                    }
                });
    }

    /**
     * 获取用户信息
     */
    public void reqUserInfo() {
        OkGo.<LzyResponse<FUser>>get(Consts.USERS_INFO_API)
                .execute(new LtbJsonCallback<LzyResponse<FUser>>(this, false,
                        new TypeReference<LzyResponse<FUser>>() {
                        }) {
                    @Override
                    public void onSuccess(Response<LzyResponse<FUser>> response) {
                        if (response.body().error_code == 0) {
                            FUser data = response.body().getData();
                            UserInfoCache.saveUserInfo(MainActivity.this, data);

                            //是否已经填写邀请码
                            boolean isInvited = data.isIs_invited();
                            String inviteCode = (String) SPFUtils.get(MainActivity.this, Common.INVITE_CODE, "");
                            if (!isInvited) {
                                reqInviteCodeSubmit(inviteCode);
                            }
                            EventBus.getDefault().postSticky(data);
                        }
                    }
                });
    }

    /**
     * 提交邀请码
     * token在CallBack中统一加到header
     *
     * @param inviteCode
     */
    private void reqInviteCodeSubmit(String inviteCode) {
        OkGo.<String>post(Consts.FILLIN_INVITE_CODE_API)
                .params(Consts.CODE, inviteCode)
                .execute(new LtbCallback(MainActivity.this, false) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<String> entity = JSONObject.parseObject(response.body(), new TypeReference<LzyResponse<String>>() {
                        });
                        if (entity.error_code == 0) {
                            SPFUtils.remove(MainActivity.this, Common.INVITE_CODE);
                            UserInfoCache.saveIsInvited(MainActivity.this, true);
                        }
                    }
                });
    }

    /**
     * 获取用户阅读时间
     */
    public void getUserReadTime() {
        OkGo.<String>get(Consts.USER_READ_TIME_API)
                .execute(new LtbCallback(this, false) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<String> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<String>>() {
                                });
                        if (entity.error_code == 0) {
                            EventBus.getDefault().postSticky(new ReadingTimeEvent(entity.getData()));
                        }
                    }
                });
    }


    private void checkNewVersion() {
        HttpParams httpParams = new HttpParams();
        httpParams.put(Consts.FACILITY, Consts.DEVICE_ANDROID);
        httpParams.put(Consts.VERSION_CODE, 2);
//        httpParams.put(Consts.VERSION_CODE, PackageUtils.getVersionCode(this));
        AllenVersionChecker
                .getInstance()
                .requestVersion()
                .setRequestUrl(Consts.VERSION_CHECK_API)
                .setRequestParams(httpParams)
                .request(new RequestVersionListener() {
                    @Nullable
                    @Override
                    public UIData onRequestVersionSuccess(DownloadBuilder downloadBuilder, String result) {
//                        Log.e("ssssssssssssss", "result===" + result);
                        LzyResponse<UpdateInfo> entity = JSONObject.parseObject(result,
                                new TypeReference<LzyResponse<UpdateInfo>>() {
                                });
                        if (entity.error_code == 0) {
                            UpdateInfo updateInfo = entity.getData();
                            if (updateInfo.getEnforce() == 1) {
                                downloadBuilder.setForceUpdateListener(() -> FrApp.getInstance().exitApp());
                            }
                            downloadBuilder.setOnCancelListener(() -> Toast.makeText(MainActivity.this, "cancel", Toast.LENGTH_SHORT).show());
                            downloadBuilder.setReadyDownloadCommitClickListener(() -> {
                                Toast.makeText(MainActivity.this, "commit click", Toast.LENGTH_SHORT).show();

                            });

                            return crateUIData(updateInfo);
                        }
                        return null;
                    }

                    @Override
                    public void onRequestVersionFailure(String message) {

                    }
                })
                .executeMission(this);
    }

    /**
     * @return
     * @important 使用请求版本功能，可以在这里设置downloadUrl
     * 这里可以构造UI需要显示的数据
     * UIData 内部是一个Bundle
     */
    private UIData crateUIData(UpdateInfo info) {
        UIData uiData = UIData.create();
        uiData.setTitle("版本更新");
        uiData.setDownloadUrl(info.getDownloadurl());
        uiData.setContent(info.getUpgradetext());
        return uiData;
    }

}
