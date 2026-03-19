package com.nnmedia.read.ui.activities;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kongzue.dialog.v3.CustomDialog;
import com.kongzue.dialog.v3.TipDialog;
import com.nnmedia.novel.R;
import com.nnmedia.novel.RxHttp;
import com.nnmedia.read.FrApp;
import com.nnmedia.read.cache.TokenCache;
import com.nnmedia.read.cache.UserInfoCache;
import com.nnmedia.read.config.AdConfig;
import com.nnmedia.read.contact.Common;
import com.nnmedia.read.contact.Consts;
import com.nnmedia.read.contact.Url;
import com.nnmedia.read.entity.AdConfigBean;
import com.nnmedia.read.entity.AdEntity;
import com.nnmedia.read.entity.FUser;
import com.nnmedia.read.entity.LoginRpsEntity;
import com.nnmedia.read.listeners.OnClickEvent;
import com.nnmedia.read.notchtools.NotchTools;
import com.nnmedia.read.rxhttp.OnError;
import com.nnmedia.read.rxhttp.Tip;
import com.nnmedia.read.ui.base.BaseActivity;
import com.nnmedia.read.utils.GlideUtil;
import com.nnmedia.read.utils.NetworkUtil;
import com.nnmedia.read.utils.SPFUtils;
import com.nnmedia.read.utils.UniqueIdManager;
import com.rxjava.rxlife.RxLife;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 启动页面
 */
public class FrLaunchActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {

    private FrameLayout mSplashContainer;
    AppCompatTextView tvSkip;
    private ImageView ivAd;

    AdEntity data;

    private boolean cancelMsg;

    List<String> domain = new ArrayList<>();

    int pos = 0;

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
        ivAd = findViewById(R.id.iv_ad);
        tvSkip = findViewById(R.id.tv_skip);
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
        ivAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data == null) {
                    TipDialog.show(FrLaunchActivity.this, "正在请求服务器，请稍后...", TipDialog.TYPE.WARNING);
                } else {
                    cancelMsg = true;
                    if (data.getJump() == 2) {
                        jumpInSide(data.getLink());
                    } else {
                        jumpOutSide(data.getLink());
                    }
                }

            }
        });
        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvSkip.getText().equals("进入app")) {
                    cancelMsg = true;
                    goMain();
                }
            }
        });
    }

    private void jumpInSide(String url) {
        Intent intent = new Intent(FrLaunchActivity.this, CommonWebActivity.class);
        intent.putExtra(Common.KEY_URL, url);
        startActivity(intent);
    }

    private void jumpOutSide(String url) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        // 注意此处的判断intent.resolveActivity()可以返回显示该Intent的Activity对应的组件名
        // 官方解释 : Name of the component implementing an activity that can display the intent
        if (intent.resolveActivity(FrLaunchActivity.this.getPackageManager()) != null) {
            final ComponentName componentName = intent.resolveActivity(FrLaunchActivity.this.getPackageManager());
            startActivity(Intent.createChooser(intent, "请选择浏览器"));
        } else {
            TipDialog.show(FrLaunchActivity.this, "链接错误或无浏览器", TipDialog.TYPE.ERROR);
        }
    }

    Gson gson;

    @Override
    public void doBusiness(Context mContext) {
        gson = new Gson();
//        boolean isFirstRun = (boolean) SPFUtils.get(this, Common.SPF_KEY_FIRST_RUN, true);
//        if (isFirstRun) {   //安装后首次运行（其实是有没有同意使用协议）
//                    showAgreementDialog();
//        } else {
//
//        }
        String urlList = (String) SPFUtils.get(this, Common.SPF_KEY_DOMAIN_LIST, "");
        if (TextUtils.isEmpty(urlList)) {
            statrInitTask();
        } else {
            SPFUtils.put(FrLaunchActivity.this, Common.SPF_KEY_IS_NEW_LIST, false);
            Type listType = new TypeToken<List<String>>() {
            }.getType();
            domain = gson.fromJson(urlList, listType);
            startInit();
        }

//        reqAdsProbabilityConfig();
    }

    /**
     * 启动
     */
    private void startInit() {
        if (pos < domain.size()) {
            Url.baseurl = domain.get(pos);
//            Url.baseurl = "https://jjj.htxs.fun";
            pos++;
        } else {
            pos = 0;
            boolean isNewList = (boolean) SPFUtils.get(this, Common.SPF_KEY_IS_NEW_LIST, false);
            if (isNewList) {
                timeMank(1);
            } else {
                statrInitTask();
            }
        }
        //token为空 判定为APP安装后第一次登录，反之。
        String token = TokenCache.getToken(this);

        //首次登录
        if ("".equals(token) || TextUtils.isEmpty(token)) {
            if (NetworkUtil.isNetworkAvailable(this)) {
                reqUniqueIDLogin(false);
            } else {
                $(R.id.ctl_no_network_show).setVisibility(View.VISIBLE);    //-->无网络
            }
        } else {
            chackToken();
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
//        if (gender == 0) {
//            startActivity(new Intent(this, GenderChoiceActivity.class));
//            finish();
//        } else {
        UserInfoCache.saveGender(this, 1);
//            加载开屏广告
//        loadSplashAd();
//        }
    }


    @Override
    protected void onResume() {
        super.onResume();
//        GDTAction.logAction(ActionType.START_APP);      //上报广点通启动
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        cancelMsg = false;
        handler.sendEmptyMessageDelayed(1, 1000);
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

    public void chackToken() {
        int gender = UserInfoCache.getGender(this);
        //  根据性别进行下一步
        handleByGender(gender);
        RxHttp.get(Consts.USER_CHACKTOKEN_API)
                .asResponse(String.class)
                .to(RxLife.toMain(this))  //感知生命周期，并在主线程回调
                .subscribe(data -> {
                    if (data.equals("0")){
                        TokenCache.saveToken(FrLaunchActivity.this, "");
                        startInit();
                    }else{
                        //加载开屏广告3
                        loadSplashAd();
                    }
                }, (OnError) error -> {
                    TokenCache.saveToken(FrLaunchActivity.this, "");
                    startInit();
                });
    }

    private Handler handler = new Handler(msg -> {
        if (msg.what == 1 && !cancelMsg) {
            goMain();
        }
        return false;
    });

    private void timeMank(int time) {
        ValueAnimator valueAnimat = ValueAnimator.ofInt(time, 0);
        valueAnimat.setDuration(time * 1000);
        valueAnimat.setInterpolator(new LinearInterpolator());
        valueAnimat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int currentVaule = (int) ((animation.getDuration() - animation.getCurrentPlayTime()) / 1000);
                if (currentVaule == 0) {
                    tvSkip.setText("进入app");
                    handler.sendEmptyMessageDelayed(1, 1000);
                } else {
                    tvSkip.setVisibility(View.VISIBLE);
                    tvSkip.setText("" + currentVaule);
                }
            }
        });
        valueAnimat.start();
    }

    /**
     * 加载开屏广告
     */
    private void loadSplashAd() {
        RxHttp.postForm(Consts.AD_TAIL_URL)
                .asResponse(AdEntity.class)
                .to(RxLife.toMain(this))
                .subscribe(ad -> {
                    data = ad;
                    if (data != null) {
                        String url = data.getImageText();
                        GlideUtil.loadRoundRect(FrLaunchActivity.this, ivAd, url, -1);
                    }
                    timeMank(6);
                }, (OnError) error -> {
                    timeMank(1);
                });
    }

    /**
     * 游客登录
     */
    private void reqUniqueIDLogin(boolean showDialog) {
        String uniqueID = UniqueIdManager.getUniqueID(FrLaunchActivity.this);
        Log.e("uniqueID", uniqueID);
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
                    //加载开屏广告
                    loadSplashAd();
                }, (OnError) error -> {
                    int code = error.getErrorCode();
                    if (code == 10001 || code == 10010) {
                        //加载开屏广告
                        loadSplashAd();
                    } else {
                        startInit();
                    }
                });
    }

    private void reqDynamicDomain() {
        SPFUtils.put(FrLaunchActivity.this, Common.SPF_KEY_IS_NEW_LIST, true);
        RxHttp.postForm(Consts.DYNAMIC_DOMAIN)
                .setDomainToBaseUrlYMIfAbsent()
                .setAssemblyEnabled(false)
                .asResponseList(String.class)
                .doOnSubscribe(disposable -> {
                    showLoadingDialog();
                })
                .doFinally(this::dismissLoadingDialog)
                .to(RxLife.toMain(this))  //感知生命周期，并在主线程回调
                .subscribe(list -> {
                    String data = gson.toJson(list);
                    SPFUtils.put(FrLaunchActivity.this, Common.SPF_KEY_DOMAIN_LIST, data);
                    domain = list;
                    startInit();
                }, (OnError) error -> {
                    reqDynamicDomainAgain();
//                    goMain();
                });
    }

    private void reqDynamicDomainAgain() {
        RxHttp.postForm(Consts.DYNAMIC_DOMAIN)
                .setDomainToBaseUrlYM2IfAbsent()
                .setAssemblyEnabled(false)
                .asResponseList(String.class)
                .doOnSubscribe(disposable -> {
                    showLoadingDialog();
                })
                .doFinally(this::dismissLoadingDialog)
                .to(RxLife.toMain(this))  //感知生命周期，并在主线程回调
                .subscribe(list -> {
                    String data = gson.toJson(list);
                    SPFUtils.put(FrLaunchActivity.this, Common.SPF_KEY_DOMAIN_LIST, data);
                    domain = list;
                    startInit();
                }, (OnError) error -> {
                    reqDynamicDomainAgain2();
                });
    }

    private void reqDynamicDomainAgain2() {
        RxHttp.postForm(Consts.DYNAMIC_DOMAIN)
                .setDomainToBaseUrlYM3IfAbsent()
                .setAssemblyEnabled(false)
                .asResponseList(String.class)
                .doOnSubscribe(disposable -> {
                    showLoadingDialog();
                })
                .doFinally(this::dismissLoadingDialog)
                .to(RxLife.toMain(this))  //感知生命周期，并在主线程回调
                .subscribe(list -> {
                    String data = gson.toJson(list);
                    SPFUtils.put(FrLaunchActivity.this, Common.SPF_KEY_DOMAIN_LIST, data);
                    domain = list;
                    startInit();
                }, (OnError) error -> {
                    reqDynamicDomainAgain3();
                });
    }

    private void reqDynamicDomainAgain3() {
        RxHttp.postForm(Consts.DYNAMIC_DOMAIN)
                .setDomainToBaseUrlYM4IfAbsent()
                .setAssemblyEnabled(false)
                .asResponseList(String.class)
                .doOnSubscribe(disposable -> {
                    showLoadingDialog();
                })
                .doFinally(this::dismissLoadingDialog)
                .to(RxLife.toMain(this))  //感知生命周期，并在主线程回调
                .subscribe(list -> {
                    String data = gson.toJson(list);
                    SPFUtils.put(FrLaunchActivity.this, Common.SPF_KEY_DOMAIN_LIST, data);
                    domain = list;
                    startInit();
                }, (OnError) error -> {
                    reqDynamicDomainAgain4();
                });
    }


    private void reqDynamicDomainAgain4() {
        RxHttp.postForm(Consts.DYNAMIC_DOMAIN)
                .setDomainToBaseUrlYM5IfAbsent()
                .setAssemblyEnabled(false)
                .asResponseList(String.class)
                .doOnSubscribe(disposable -> {
                    showLoadingDialog();
                })
                .doFinally(this::dismissLoadingDialog)
                .to(RxLife.toMain(this))  //感知生命周期，并在主线程回调
                .subscribe(list -> {
                    String data = gson.toJson(list);
                    SPFUtils.put(FrLaunchActivity.this, Common.SPF_KEY_DOMAIN_LIST, data);
                    domain = list;
                    startInit();
                }, (OnError) error -> {
                    goMain();
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
//                    Intent intent = new Intent(FrLaunchActivity.this, CommonWebActivity.class);
//                    intent.putExtra(Common.KEY_URL, Consts.USER_AGREEMENT_URL);
//                    startActivity(intent);
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
//                    Intent intent = new Intent(FrLaunchActivity.this, CommonWebActivity.class);
//                    intent.putExtra(Common.KEY_URL, Consts.PRIVACY_POLICY_URL);
//                    startActivity(intent);
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
    //    private static final String READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE;
    private static final String[] READ_PHONE_STATE = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    private boolean hasPhoneStatePermissions() {
        return EasyPermissions.hasPermissions(this, READ_PHONE_STATE);
    }

    @AfterPermissionGranted(RC_PHONE_STATE_PERM)
    public void statrInitTask() {
        if (hasPhoneStatePermissions()) {
            reqDynamicDomain();
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
        reqDynamicDomain();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

}