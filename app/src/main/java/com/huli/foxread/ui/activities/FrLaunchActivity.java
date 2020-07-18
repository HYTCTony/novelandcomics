package com.huli.foxread.ui.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.huli.foxread.FrApp;
import com.huli.foxread.R;
import com.huli.foxread.RxHttp;
import com.huli.foxread.cache.TokenCache;
import com.huli.foxread.cache.UserInfoCache;
import com.huli.foxread.config.AdConfig;
import com.huli.foxread.config.TogetherAdConst;
import com.huli.foxread.contact.Common;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.entity.AdConfigBean;
import com.huli.foxread.entity.FUser;
import com.huli.foxread.entity.LoginRpsEntity;
import com.huli.foxread.listeners.OnClickEvent;
import com.huli.foxread.notchtools.NotchTools;
import com.huli.foxread.rxhttp.OnError;
import com.huli.foxread.rxhttp.Tip;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.utils.NetworkUtil;
import com.huli.foxread.utils.SPFUtils;
import com.huli.foxread.utils.UniqueIdManager;
import com.hytc.ads.helper.splash.TogetherAdSplash;
import com.kongzue.dialog.v3.CustomDialog;
import com.qq.gdt.action.ActionType;
import com.qq.gdt.action.GDTAction;
import com.rxjava.rxlife.RxLife;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 启动页面
 */
public class FrLaunchActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {

    private FrameLayout mSplashContainer;

    @Override
    protected void setStatusBar() {
//        StatusBarUtils.setTransparent(this);
        NotchTools.getFullScreenTools().fullScreenUseStatus(this, notchProperty -> {
           /* int marginTop = notchProperty.getMarginTop();
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mBackView.getLayoutParams();
            layoutParams.topMargin += marginTop;
            mBackView.setLayoutParams(layoutParams);*/
        });
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
        return R.layout.activity_fr_launch;
    }

    @Override
    public void initView(View view) {
        mSplashContainer = findViewById(R.id.splash_container);
    }

    @Override
    public void setListener() {
        $(R.id.tv_asBtn_network_setting).setOnClickListener(new OnClickEvent() {
            @Override
            public void singleClick(View v) {
                startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
            }
        });
        $(R.id.tv_asBtn_reconnect).setOnClickListener(new OnClickEvent() {
            @Override
            public void singleClick(View v) {
                if (NetworkUtil.isNetworkAvailable(FrLaunchActivity.this)) {
                    reqUniqueIDLogin(true);
                    $(R.id.ctl_no_network_show).setVisibility(View.GONE);
                } else {
                    Tip.show(R.string.network_error);
                }
            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {
        boolean isFirstRun = (boolean) SPFUtils.get(this, Common.SPF_KEY_FIRST_RUN, true);
        if (isFirstRun) {   //安装后首次运行（其实是有没有同意使用协议）
            showAgreementDialog();
        } else {
            statrInitTask();
        }

        reqAdsProbabilityConfig();
    }


    private Handler handler = new Handler(msg -> {
        if (msg.what == 9) {
            goMain();
        }
        return false;
    });

    /**
     * 加载开屏广告
     */
    private void loadSplashAd() {
        TogetherAdSplash.showAdFull(this, AdConfig.splashAdConfig(this), TogetherAdConst.AD_SPLASH, mSplashContainer, null, null, new TogetherAdSplash.AdListenerSplashFull() {
            @Override
            public void onAdPrepared(@NotNull String channel) {
            }

            @Override
            public void onAdDismissed() {
                handler.sendEmptyMessageDelayed(9, 100);
            }

            @Override
            public void onAdFailed(@Nullable String failedMsg) {
                handler.sendEmptyMessageDelayed(9, 1200);
            }

            @Override
            public void onAdClick(@NotNull String channel) {
            }

            @Override
            public void onStartRequest(@NotNull String channel) {
            }
        });
    }


    /**
     * 启动
     */
    private void startInit() {
        //token为空 判定为APP安装后第一次登录，反之。
        String token = TokenCache.getToken(this);

        //判断为首次登录
        if ("".equals(token) || TextUtils.isEmpty(token)) {
            if (NetworkUtil.isNetworkAvailable(this)) {
                reqUniqueIDLogin(false);
            } else {
                $(R.id.ctl_no_network_show).setVisibility(View.VISIBLE);    //-->无网络
            }
        } else {
            int gender = UserInfoCache.getGender(this);
            //  根据性别进行下一步
            handleByGender(gender);
        }
    }

    /**
     * 根据性别进行下一步
     *
     * @param gender 性别
     */
    private void handleByGender(int gender) {
        //  是否有性别---> 无：  startActivity(new Intent(mContext, GenderChoiceActivity.class));
        //  是否有性别---> 有：   reqAdsFromNet();
        if (gender == 0) {
            startActivity(new Intent(this, GenderChoiceActivity.class));
            finish();
        } else {
            //加载开屏广告
            loadSplashAd();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        GDTAction.logAction(ActionType.START_APP);      //上报广点通启动
    }

    @Override
    protected void onPause() {
        overridePendingTransition(0, 0);
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void goMain() {
        Intent intent = new Intent(FrLaunchActivity.this, MainActivity.class);
        startActivity(intent);
//      mSplashContainer.removeAllViews();
        finish();
    }

    /**
     * 游客登录
     */
    private void reqUniqueIDLogin(boolean showDialog) {
        String uniqueID = UniqueIdManager.getUniqueID(FrLaunchActivity.this);
        RxHttp.postForm(Consts.USE_UNIQUE_ID_LOGIN_OR_REG_API)
                .setAssemblyEnabled(false)
                .add(Consts.UNIQUE_ID, uniqueID)
                .asResponse(LoginRpsEntity.class)
                .flatMap(loginRpsEntity -> {
                    String token = loginRpsEntity.getToken();
                    TokenCache.saveToken(FrLaunchActivity.this, token);
                    //获取用户信息
                    return RxHttp.get(Consts.USERS_INFO_API)
                            .addHeader(Consts.TOKEN, token)
                            .subscribeOnCurrent() //当前线程发送登录请求(RxHttp默认在IO线程执行请求，也默认在IO线程回调)
                            .asResponse(FUser.class);
                })
                .doOnSubscribe(disposable -> {
                    if (showDialog) {
                        showLoadingDialog();
                    }
                })
                .doFinally(() -> {
                    if (showDialog) {
                        dismissLoadingDialog();
                    }
                })
                .to(RxLife.toMain(this))  //感知生命周期，并在主线程回调
                .subscribe(fUser -> {
                    UserInfoCache.saveUserInfo(FrLaunchActivity.this, fUser);
                    int gender = fUser.getGender();

                    //根据性别处理
                    handleByGender(gender);
                }, (OnError) error -> {
                    int code = error.getErrorCode();
                    if (code == 10001 || code == 10010) {
                        //加载开屏广告
                        loadSplashAd();
                    } else {
                        goMain();
                    }
                });
    }

    /**
     * 获取三个平台广告出现几率配置
     */
    private void reqAdsProbabilityConfig() {
        RxHttp.get(Consts.ADS_ADVERT_TAIL_API)
                .setAssemblyEnabled(false)
                .add(Consts.APK_CHANNEL, getChannel())
                .asResponse(AdConfigBean.class)
                .to(RxLife.toMain(this))  //感知生命周期，并在主线程回调
                .subscribe(bean -> AdConfig.saveAdConfig(this, bean));
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
     * 展示用户协议、隐私政策提示框
     */
    private void showAgreementDialog() {
        //对于未实例化的布局：
        CustomDialog.build(FrLaunchActivity.this, R.layout.layout_dialog_user_agreement_and_privacy_policy, (dialog, view) -> {
            TextView btnDisAgree = view.findViewById(R.id.tv_disAgree);
            TextView btnDisAgreeAndExit = view.findViewById(R.id.tv_disAgree_and_exit);
            Button btnAgree = view.findViewById(R.id.btn_agree_and_continue);
            btnDisAgree.setOnClickListener(new OnClickEvent() {
                @Override
                public void singleClick(View v) {
                    view.findViewById(R.id.scrollView_agreement_and_policy_abstract).setVisibility(View.GONE);
                    view.findViewById(R.id.tv_service_unavailable).setVisibility(View.VISIBLE);
                    v.setVisibility(View.GONE);
                    btnDisAgreeAndExit.setVisibility(View.VISIBLE);
                }
            });
            btnDisAgreeAndExit.setOnClickListener(new OnClickEvent() {
                @Override
                public void singleClick(View v) {
                    dialog.doDismiss();
                    FrApp.getInstance().exitApp();
                }
            });
            btnAgree.setOnClickListener(new OnClickEvent() {
                @Override
                public void singleClick(View v) {
                    SPFUtils.put(FrLaunchActivity.this, Common.SPF_KEY_FIRST_RUN, false);
                    dialog.doDismiss();
//                    mHandler.sendEmptyMessage(9);
                    statrInitTask();
                }
            });

            TextView tvPolicyEntrance = view.findViewById(R.id.tv_agreement_and_policy_entrance);
            SpannableString spannableString = new SpannableString(getString(R.string.txt_agreement_policy_entrance));
            spannableString.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View view) {
                    //用户协议
//                    Uri uri = Uri.parse(Consts.USER_AGREEMENT_URL);
//                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    Intent intent = new Intent(FrLaunchActivity.this, CommonWebActivity.class);
                    intent.putExtra(Common.KEY_URL, Consts.USER_AGREEMENT_URL);
                    startActivity(intent);
                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    ds.setColor(ContextCompat.getColor(FrLaunchActivity.this, R.color.col_blue_0a8ecc));
                    ds.setUnderlineText(false);
                }
            }, 8, 20, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View view) {
                    //隐私政策
//                    Uri uri = Uri.parse(Consts.PRIVACY_POLICY_URL);
//                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    Intent intent = new Intent(FrLaunchActivity.this, CommonWebActivity.class);
                    intent.putExtra(Common.KEY_URL, Consts.PRIVACY_POLICY_URL);
                    startActivity(intent);
                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    ds.setColor(ContextCompat.getColor(FrLaunchActivity.this, R.color.col_blue_0a8ecc));
                    ds.setUnderlineText(false);
                }
            }, 21, 31, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            tvPolicyEntrance.setMovementMethod(LinkMovementMethod.getInstance());//不设置 没有点击事件
            tvPolicyEntrance.setHighlightColor(ContextCompat.getColor(FrLaunchActivity.this, R.color.transparent));
            tvPolicyEntrance.setText(spannableString);
        }).setCancelable(false).show();
    }


    private static final int RC_PHONE_STATE_PERM = 124;
    private static final String READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE;
//    private static final String[] READ_PHONE_STATE = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private boolean hasPhoneStatePermissions() {
        return EasyPermissions.hasPermissions(this, READ_PHONE_STATE);
    }

    @AfterPermissionGranted(RC_PHONE_STATE_PERM)
    public void statrInitTask() {
        if (hasPhoneStatePermissions()) {
            startInit();
        } else {
            EasyPermissions.requestPermissions(this,
                    getString(R.string.rationale_phone_state),
                    RC_PHONE_STATE_PERM,
                    READ_PHONE_STATE);
        }
    }


    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
//        Log.e(TAG, "onPermissionsGranted");
        //如果有注解AfterPermissionGranted --- 有些权限不授予，有些不授予会调用这个方法；全部授予权限这回调用AfterPermissionGrantedd注解的方法
        /*if (!EasyPermissions.somePermissionPermanentlyDenied(this, Arrays.asList(READ_PHONE_STATE))) {
        Log.e(TAG, "dsdsds");
            startInit();
        }*/
//        startInit();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        startInit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

}
