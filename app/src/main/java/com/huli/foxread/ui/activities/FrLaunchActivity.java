package com.huli.foxread.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.huli.foxread.GlideApp;
import com.huli.foxread.R;
import com.huli.foxread.cache.TokenCache;
import com.huli.foxread.cache.UserInfoCache;
import com.huli.foxread.callbacks.ookkggoo.LtbCallback;
import com.huli.foxread.callbacks.ookkggoo.LtbJsonCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
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
import com.huli.foxread.utils.UniqueIdManager;
import com.kongzue.dialog.v3.TipDialog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.Response;

import java.net.URL;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

public class FrLaunchActivity extends BaseActivity {

    private ConstraintLayout layoutAdvertising;
    private Button btnSkip;
    private AdEntity adEntity;
    private ImageView ivAdPic;

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

        //启动页延长显示时间   800毫秒 防止一闪而过
        mHandler.sendEmptyMessageDelayed(9, 800);
    }

    private void start() {
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

    private int count = 3;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 0) {
                btnSkip.setText(String.format(getString(R.string.txt_skip_x), getCount()));
                if (count > 0) {
                    mHandler.sendEmptyMessageDelayed(0, 1000);
                }
            } else if (msg.what == 9) {
                start();
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
        startActivity(new Intent(FrLaunchActivity.this, MainActivity.class));
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
                            UserInfoCache.saveCacheAll(FrLaunchActivity.this, data);
                            // 游客登录 是否有性别---> 无：  startActivity(new Intent(mContext, GenderChoiceActivity.class));
                            // 游客登录 是否有性别---> 有：   reqAdsFromNet();
                            // 正式用户登录(肯定有性别)---> reqAdsFromNet();
                            int gender = data.getGender();
                            if (gender == -1) {
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
        OkGo.<LzyResponse<AdEntity>>get(Consts.ADS_TAIL_API)
                .cacheMode(CacheMode.REQUEST_FAILED_READ_CACHE)
                .execute(new LtbJsonCallback<LzyResponse<AdEntity>>(this, false,
                        new TypeReference<LzyResponse<AdEntity>>() {
                        }) {
                    @Override
                    public void onSuccess(Response<LzyResponse<AdEntity>> response) {
                        int code = response.body().error_code;
                        if (code == 0) {
                            adEntity = response.body().getData();
                            GlideApp.with(FrLaunchActivity.this)
                                    .load(adEntity.getImageText())
                                    .into(new CustomTarget<Drawable>() {
                                        @Override
                                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                            showAdsLayout(resource, adEntity.getLinks());
                                        }

                                        @Override
                                        public void onLoadCleared(@Nullable Drawable placeholder) {
                                        }

                                        @Override
                                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                            super.onLoadFailed(errorDrawable);
                                            showAdsLayout(errorDrawable, adEntity.getLinks());
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCacheSuccess(Response<LzyResponse<AdEntity>> response) {
                        super.onCacheSuccess(response);
                        onSuccess(response);
                    }
                });

    }

    /**
     * 显示广告
     *
     * @param resource
     * @param adUrl
     */
    private void showAdsLayout(Drawable resource, String adUrl) {
        ivAdPic.setImageDrawable(resource);
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


    @Override
    public void onBackPressed() {

    }
}
