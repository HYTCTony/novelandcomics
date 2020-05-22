package com.huli.foxread.ui.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTSplashAd;
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
import com.huli.foxread.entity.AdEntity;
import com.huli.foxread.entity.FUser;
import com.huli.foxread.entity.LoginRpsEntity;
import com.huli.foxread.listeners.OnClickEvent;
import com.huli.foxread.notchtools.NotchTools;
import com.huli.foxread.notchtools.core.NotchProperty;
import com.huli.foxread.notchtools.core.OnNotchCallBack;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.utils.NetworkUtil;
import com.huli.foxread.utils.SPFUtils;
import com.huli.foxread.utils.Tos;
import com.huli.foxread.utils.UIUtils;
import com.huli.foxread.utils.UniqueIdManager;
import com.kongzue.dialog.v3.CustomDialog;
import com.kongzue.dialog.v3.TipDialog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.net.URL;
import java.util.List;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class FrLaunchActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {

    /*广告时间*/
    private int count = 5;

    private ConstraintLayout layoutAdvertising;
    private Button btnSkip;
    private AdEntity adEntity;
    private ImageView ivAdPic;


    private TTAdNative mTTAdNative;
    private FrameLayout mSplashContainer;
    //是否强制跳转到主页面
    private boolean mForceGoMain;
    /*（是不是）去查阅条款跳转*/
    private boolean isGo2ViewTerms;

    //开屏广告加载超时时间,建议大于3000,这里为了冷启动第一次加载到广告并且展示,示例设置了3000ms
    private static final int AD_TIME_OUT = 5000;
    private String mCodeId = "887319954";
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
        layoutAdvertising = $(R.id.ctl_advertising);
        btnSkip = $(R.id.btn_skip_ad);
        ivAdPic = $(R.id.iv_advertising_picture);

        mSplashContainer = findViewById(R.id.splash_container);
    }

    @Override
    public void setListener() {
        btnSkip.setOnClickListener(new OnClickEvent() {
            @Override
            public void singleClick(View v) {
                goMain();
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

        boolean isFirstRun = (boolean) SPFUtils.get(this, "isFirstRun", true);
        if (isFirstRun) {
            showAgreementDialog();
            return;
        }
        //启动页延长显示时间   500毫秒 防止一闪而过
        mHandler.sendEmptyMessageDelayed(9, 500);
    }


    @Override
    protected void onResume() {
        //判断是否该跳转到主页面
        if (mForceGoMain && !isGo2ViewTerms) {
            goMain();

        }
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mForceGoMain = true;
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


    /**
     * 加载开屏广告
     */
    private void loadSplashAd() {
        //step3:创建开屏广告请求参数AdSlot,具体参数含义参考文档
        AdSlot adSlot = null;
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
//                showToast(message);
                goMain();
            }

            @Override
            @MainThread
            public void onTimeout() {
//                showToast("开屏广告加载超时");
                goMain();
            }

            @Override
            @MainThread
            public void onSplashAdLoad(TTSplashAd ad) {
//                Log.d(TAG, "开屏广告请求成功");
                if (ad == null) {
                    return;
                }
                //获取SplashView
//                mSplashContainer.setVisibility(View.VISIBLE);
                View view = ad.getSplashView();
                if (view != null && mSplashContainer != null && !FrLaunchActivity.this.isFinishing()) {
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
//                        Log.d(TAG, "onAdClicked");
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
        }, AD_TIME_OUT);

    }


    /**
     * 启动
     */
    private void startInit() {
        //token为空 判定为APP安装后第一次登录，反之。
        String token = TokenCache.getToken(this);

        if (!TextUtils.isEmpty(token)) {
            if (NetworkUtil.isNetworkAvailable(FrLaunchActivity.this)) {
                reqInitUserInfo();
            } else {
                //广告有okgo缓存
                reqAdsFromNet();
            }
        } else {
            if (NetworkUtil.isNetworkAvailable(FrLaunchActivity.this)) {
                reqUniqueIDLogin();
            } else {
                $(R.id.ctl_no_network_show).setVisibility(View.VISIBLE);
                $(R.id.tv_asBtn_network_setting).setOnClickListener(new OnClickEvent() {
                    @Override
                    public void singleClick(View v) {
                        startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                    }
                });
                $(R.id.tv_asBtn_reconnect).setOnClickListener(new OnClickEvent() {
                    @Override
                    public void singleClick(View v) {
                        reqUniqueIDLogin();
                    }
                });
            }
        }
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 0) {
                btnSkip.setText(String.format(getString(R.string.txt_skip_x), getCount()));
                if (count > 0) {
                    mHandler.sendEmptyMessageDelayed(0, 1000);
                }
            } else if (msg.what == 9) {
                statrInitTask();
            }
            return false;
        }
    });

    private boolean autoSkip = true;

    private int getCount() {
        count--;
        if (count == 0 && autoSkip) {
            goMain();
        }
        return count;
    }


    public void goMain() {
        mHandler.removeMessages(0);
        if (NetworkUtil.isNetworkAvailable(this)) {
            Intent intent = new Intent(FrLaunchActivity.this, MainActivity.class);
            intent.putExtra(Common.EXTRA_HAS_GET_USERINFO, true);
            startActivity(intent);

//            mSplashContainer.removeAllViews();
            finish();
        } else {
            Tos.showShort(this, R.string.txt_no_network_try_again_later);
        }
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
                            // 游客登录 是否有性别---> 无：  startActivity(new Intent(mContext, GenderChoiceActivity.class));
                            // 游客登录 是否有性别---> 有：   reqAdsFromNet();
                            // 正式用户登录(肯定有性别)---> reqAdsFromNet();
                            int gender = data.getGender();
//                            if (gender == 0 && data.isTourist()) {
                            if (gender == 0) {
                                startActivity(new Intent(mContext, GenderChoiceActivity.class));
                                finish();
                                return;
                            } else {
                                reqAdsFromNet();
                            }
                        } else if (errorCode == 10001 || errorCode == 10010) {
                            reqAdsFromNet();
                        }
                    }

                    @Override
                    public void onError(Response<LzyResponse<FUser>> response) {
                        super.onError(response);
                        TipDialog.show(FrLaunchActivity.this, R.string.txt_network_maybe_exceptions, TipDialog.TYPE.ERROR)
                                .setOnDismissListener(() -> finish());
                    }
                });
    }

    /**
     * 游客登录
     */
    private void reqUniqueIDLogin() {
        String uniqueID = UniqueIdManager.getUniqueID(FrLaunchActivity.this);
        OkGo.<String>post(Consts.USE_UNIQUE_ID_LOGIN_OR_REG_API)
                .params(Consts.UNIQUE_ID, uniqueID)
                .execute(new LtbCallback(this, false) {
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
                });
    }


    /**
     * 获取广告
     */
    private void reqAdsFromNet() {
        //加载开屏广告
        loadSplashAd();

        /*OkGo.<LzyResponse<AdEntity>>get(Consts.ADS_TAIL_API)
                .cacheMode(CacheMode.REQUEST_FAILED_READ_CACHE)
                .execute(new LtbJsonCallback<LzyResponse<AdEntity>>(this, false,
                        new TypeReference<LzyResponse<AdEntity>>() {
                        }) {
                    @Override
                    public void onSuccess(Response<LzyResponse<AdEntity>> response) {
                        int code = response.body().error_code;
                        if (code == 0) {
                            adEntity = response.body().getData();
                            String imageUrl = adEntity.getImageText();
                            if (imageUrl.endsWith(".gif")) {
                                GlideApp.with(FrLaunchActivity.this)
                                        .asGif()
                                        .load(imageUrl)
                                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                        .into(ivAdPic);

                                adsCountDownStart(adEntity.getLink());
                            } else {
                                GlideApp.with(FrLaunchActivity.this)
                                        .load(imageUrl)
                                        .into(new CustomTarget<Drawable>() {
                                            @Override
                                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                                showAdsLayout(resource, adEntity.getLink());
                                            }

                                            @Override
                                            public void onLoadCleared(@Nullable Drawable placeholder) {
                                            }

                                            @Override
                                            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                                super.onLoadFailed(errorDrawable);
                                                showAdsLayout(errorDrawable, adEntity.getLink());
                                            }
                                        });
                            }
                        }
                    }

                    @Override
                    public void onCacheSuccess(Response<LzyResponse<AdEntity>> response) {
                        super.onCacheSuccess(response);
                        onSuccess(response);
                    }
                });*/

    }

    /**
     * 显示广告
     *
     * @param resource
     * @param adUrl
     */
    private void showAdsLayout(Drawable resource, String adUrl) {
        ivAdPic.setImageDrawable(resource);
        adsCountDownStart(adUrl);
    }

    private void adsCountDownStart(String adUrl) {
        ivAdPic.setOnClickListener(new OnClickEvent() {
            @Override
            public void singleClick(View v) {
                // 打开浏览器
//                autoSkip = false;
                if (isUrl(adUrl)) {
                    Uri uri = Uri.parse(adUrl);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            }
        });

        layoutAdvertising.setVisibility(View.VISIBLE);
        btnSkip.setText(String.format(getString(R.string.txt_skip_x), count));
        mHandler.sendEmptyMessageDelayed(0, 1000);
    }


    private boolean isUrl(String link) {
        try {
            URL url = new URL(link);
            if (url.getHost() != null) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
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
                    SPFUtils.put(FrLaunchActivity.this, "isFirstRun", false);
                    dialog.doDismiss();
                    mHandler.sendEmptyMessage(9);
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
                    isGo2ViewTerms = true;
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
                    isGo2ViewTerms = true;
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
//        Log.e(TAG, "onPermissionsDenied");
        startInit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

}
