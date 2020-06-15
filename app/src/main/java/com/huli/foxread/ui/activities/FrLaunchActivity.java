package com.huli.foxread.ui.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
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

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.huli.foxread.FrApp;
import com.huli.foxread.R;
import com.huli.foxread.cache.TokenCache;
import com.huli.foxread.cache.UserInfoCache;
import com.huli.foxread.callbacks.ookkggoo.LtbCallback;
import com.huli.foxread.callbacks.ookkggoo.LtbJsonCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.config.TTAdManagerHolder;
import com.huli.foxread.contact.Common;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.contact.CsjAdsCode;
import com.huli.foxread.entity.FUser;
import com.huli.foxread.entity.LoginRpsEntity;
import com.huli.foxread.listeners.OnClickEvent;
import com.huli.foxread.notchtools.NotchTools;
import com.huli.foxread.notchtools.core.NotchProperty;
import com.huli.foxread.notchtools.core.OnNotchCallBack;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.utils.SPFUtils;
import com.huli.foxread.utils.UniqueIdManager;
import com.kongzue.dialog.v3.CustomDialog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 启动页面
 */
public class FrLaunchActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {

    /*广告时间*/
    private int count = 5;

    private TTAdNative mTTAdNative;
    private FrameLayout mSplashContainer;

    //开屏广告加载超时时间,建议大于3000,这里为了冷启动第一次加载到广告并且展示,示例设置了3000ms
    private static final int AD_TIME_OUT = 5000;
    private String mCodeId = CsjAdsCode.SPLASH_CODE_ID;
    private boolean mIsExpress = false; //是否请求模板广告

    @Override
    protected void setStatusBar() {
//        StatusBarUtils.setTransparent(this);
        NotchTools.getFullScreenTools().fullScreenUseStatus(this, new OnNotchCallBack() {
            @Override
            public void onNotchPropertyCallback(NotchProperty notchProperty) {
               /* int marginTop = notchProperty.getMarginTop();
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mBackView.getLayoutParams();
                layoutParams.topMargin += marginTop;
                mBackView.setLayoutParams(layoutParams);*/
            }
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
                reqUniqueIDLogin(true);
            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {
        //step2:创建TTAdNative对象
        mTTAdNative = TTAdManagerHolder.get().createAdNative(this);
        getExtraInfo();
        //在合适的时机申请权限，如read_phone_state,防止获取不了imei时候，下载类广告没有填充的问题
        //在开屏时候申请不太合适，因为该页面倒计时结束或者请求超时会跳转，在该页面申请权限，体验不好
        // TTAdManagerHolder.getInstance(this).requestPermissionIfNecessary(this);

        boolean isFirstRun = (boolean) SPFUtils.get(this, Common.SPF_KEY_FIRST_RUN, true);
        if (isFirstRun) {   //安装后首次运行（其实是有没有同意使用协议）
            showAgreementDialog();
        } else {
            statrInitTask();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void getExtraInfo() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        String codeId = intent.getStringExtra("splash_rit");
        if (!TextUtils.isEmpty(codeId)) {
            mCodeId = codeId;
        }
        mIsExpress = intent.getBooleanExtra("is_express", false);
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
        handler.sendEmptyMessageDelayed(9, 1200);


       /* //step3:创建开屏广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot;
        if (mIsExpress) {
            //个性化模板广告需要传入期望广告view的宽、高，单位dp，请传入实际需要的大小，
            //比如：广告下方拼接logo、适配刘海屏等，需要考虑实际广告大小
            float expressViewWidth = UIUtils.getScreenWidthDp(this);
            float expressViewHeight = UIUtils.getHeight(this);
            adSlot = new AdSlot.Builder()
                    .setCodeId(mCodeId)
                    .setSupportDeepLink(true)
                    .setImageAcceptedSize(1080, 1920)
                    //模板广告需要设置期望个性化模板广告的大小,单位dp,代码位是否属于个性化模板广告，请在穿山甲平台查看
                    .setExpressViewAcceptedSize(expressViewWidth, expressViewHeight)
                    .build();
        } else {
            adSlot = new AdSlot.Builder()
                    .setCodeId(mCodeId)
                    .setSupportDeepLink(true)
                    .setImageAcceptedSize(1080, 1920)
                    .build();
        }
        //step4:请求广告，调用开屏广告异步请求接口，对请求回调的广告作渲染处理
        mTTAdNative.loadSplashAd(adSlot, new TTAdNative.SplashAdListener() {
            @Override
            @MainThread
            public void onError(int code, String message) {
//                Log.e(TAG, "onError===" + String.valueOf(message));
                goMain();
            }

            @Override
            @MainThread
            public void onTimeout() {
//               Log.e(TAG, "开屏广告加载超时");
                goMain();
            }

            @Override
            @MainThread
            public void onSplashAdLoad(TTSplashAd ad) {
//                Log.d(TAG, "开屏广告请求成功");
                if (ad == null) {
                    goMain();
                    return;
                }
                //获取SplashView
//                mSplashContainer.setVisibility(View.VISIBLE);
                View view = ad.getSplashView();
                if (mSplashContainer != null && !FrLaunchActivity.this.isFinishing()) {
                    mSplashContainer.removeAllViews();
                    //把SplashView 添加到ViewGroup中,注意开屏广告view：width >=70%屏幕宽；height >=50%屏幕高
                    mSplashContainer.addView(view);
                    //设置不开启开屏广告倒计时功能以及不显示跳过按钮,如果这么设置，您需要自定义倒计时逻辑
                    //ad.setNotAllowSdkCountdown();
                } else {
                    goMain();
                }

                //设置SplashView的交互监听器
                ad.setSplashInteractionListener(new TTSplashAd.AdInteractionListener() {
                    @Override
                    public void onAdClicked(View view, int type) {
//                        Log.d(TAG, "开屏广告点击");
//                        showToast("开屏广告点击");
                    }

                    @Override
                    public void onAdShow(View view, int type) {
//                        Log.d(TAG, "onAdShow");
//                        showToast("开屏广告展示");
                    }

                    @Override
                    public void onAdSkip() {
//                        Log.d(TAG, "onAdSkip");
//                        showToast("开屏广告跳过");
                        goMain();
                    }

                    @Override
                    public void onAdTimeOver() {
//                        Log.d(TAG, "onAdTimeOver");
//                        showToast("开屏广告倒计时结束");
                        goMain();
                    }
                });
                if (ad.getInteractionType() == TTAdConstant.INTERACTION_TYPE_DOWNLOAD) {
                    ad.setDownloadListener(new TTAppDownloadListener() {
                        boolean hasShow = false;

                        @Override
                        public void onIdle() {
                        }

                        @Override
                        public void onDownloadActive(long totalBytes, long currBytes, String fileName, String appName) {
                            if (!hasShow) {
//                                showToast("下载中...");
                                hasShow = true;
                            }
                        }

                        @Override
                        public void onDownloadPaused(long totalBytes, long currBytes, String fileName, String appName) {
//                            showToast("下载暂停...");

                        }

                        @Override
                        public void onDownloadFailed(long totalBytes, long currBytes, String fileName, String appName) {
//                            showToast("下载失败...");

                        }

                        @Override
                        public void onDownloadFinished(long totalBytes, String fileName, String appName) {
//                            showToast("下载完成...");

                        }

                        @Override
                        public void onInstalled(String fileName, String appName) {
//                            showToast("安装完成...");

                        }
                    });
                }
            }
        }, AD_TIME_OUT);*/

    }


    /**
     * 启动
     */
    private void startInit() {
        //token为空 判定为APP安装后第一次登录，反之。
        String token = TokenCache.getToken(this);

        if (TextUtils.isEmpty(token)) {
            reqUniqueIDLogin(false);
        } else {
            //  是否有性别---> 无：  startActivity(new Intent(mContext, GenderChoiceActivity.class));
            //  是否有性别---> 有：   reqAdsFromNet();
            int gender = UserInfoCache.getGender(this);
            if (gender == 0) {
                startActivity(new Intent(this, GenderChoiceActivity.class));
                finish();
            } else {
                //加载开屏广告
                loadSplashAd();
            }
        }
    }

    @Override
    protected void onPause() {
        overridePendingTransition(0, 0);
        super.onPause();
    }

    public void goMain() {
        Intent intent = new Intent(FrLaunchActivity.this, MainActivity.class);
        startActivity(intent);

//      mSplashContainer.removeAllViews();
        finish();
    }


    /**
     * 获取用户信息
     */
    private void reqInitUserInfo() {
        OkGo.<LzyResponse<FUser>>get(Consts.USERS_INFO_API)
                .tag(Consts.USERS_INFO_API + "_launch")
                .execute(new LtbJsonCallback<LzyResponse<FUser>>(this, false,
                        new TypeReference<LzyResponse<FUser>>() {
                        }) {
                    @Override
                    public void onSuccess(Response<LzyResponse<FUser>> response) {
                        int errorCode = response.body().error_code;
                        if (errorCode == 0) {
                            FUser data = response.body().getData();
                            UserInfoCache.saveUserInfo(FrLaunchActivity.this, data);
                            //  是否有性别---> 无：  startActivity(new Intent(mContext, GenderChoiceActivity.class));
                            //  是否有性别---> 有：   reqAdsFromNet();
                            int gender = data.getGender();
                            if (gender == 0) {
                                startActivity(new Intent(mContext, GenderChoiceActivity.class));
                                finish();
                            } else {
                                //加载开屏广告
                                loadSplashAd();
                            }
                        } else if (errorCode == 10001 || errorCode == 10010) {
                            //加载开屏广告
                            loadSplashAd();
                        }
                    }

                    @Override
                    public void onError(Response<LzyResponse<FUser>> response) {
                        super.onError(response);

                        goMain();
                    }
                });
    }

    /**
     * 游客登录
     */
    private void reqUniqueIDLogin(boolean showDialog) {
        String uniqueID = UniqueIdManager.getUniqueID(FrLaunchActivity.this);
        OkGo.<String>post(Consts.USE_UNIQUE_ID_LOGIN_OR_REG_API)
                .params(Consts.UNIQUE_ID, uniqueID)
                .execute(new LtbCallback(this, showDialog) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<LoginRpsEntity> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<LoginRpsEntity>>() {
                                });
                        if (entity.error_code == 0) {
                            $(R.id.ctl_no_network_show).setVisibility(View.GONE);
                            String token = entity.getData().getToken();
                            TokenCache.saveToken(FrLaunchActivity.this, token);

                            reqInitUserInfo();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        $(R.id.ctl_no_network_show).setVisibility(View.VISIBLE);
                    }
                });
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
