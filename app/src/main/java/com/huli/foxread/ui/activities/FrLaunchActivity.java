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
import com.huli.foxread.cache.UserInfoCache;
import com.huli.foxread.callbacks.ookkggoo.LtbCallback;
import com.huli.foxread.callbacks.ookkggoo.LtbJsonCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.entity.AdEntity;
import com.huli.foxread.entity.FUser;
import com.huli.foxread.listeners.OnClickEvent;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.utils.NetworkUtil;
import com.huli.foxread.utils.UniqueIdManager;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.Response;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

public class FrLaunchActivity extends BaseActivity {

    private static final String[] PHONE_STATE = {Manifest.permission.READ_PHONE_STATE};
    private static final int RC_PHONE_STATE_PERM = 124;

    private ConstraintLayout layoutAdvertising;
    private Button btnSkip;
    private AdEntity adEntity;
    private ImageView ivAdPic;

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

        //启动页延长显示时间   至少800毫秒 防止一闪而过
        mHandler.sendEmptyMessageDelayed(9, 800);
    }

    private void start(){
        //token为空 判定为APP安装后第一次登录，反之。
        String token = UserInfoCache.getToken(this);

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
            }else if(msg.what==9){
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



   /* @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    private boolean hasReadPhoneStatePermissions() {
        return EasyPermissions.hasPermissions(this, PHONE_STATE);
    }



    @AfterPermissionGranted(RC_PHONE_STATE_PERM)
    public void readPhoneStateTask() {
        if (hasReadPhoneStatePermissions()) {
            if (NetworkUtil.isNetworkAvailable(FrLaunchActivity.this)) {
                reqInitUserInfo();
            } else {
                goMain();
            }
        } else {
            // Ask for both permissions
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_phone_state), RC_PHONE_STATE_PERM, PHONE_STATE);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Log.e(TAG, "权限授予");
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
        //TODO 弹出提示框不给权限(不能取消)-----点确定----退出
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            String yes = getString(R.string.yes);
            String no = getString(R.string.no);
            // Do something after user returned from app settings screen, like showing a Toast.
            TipDialog.show(this, R.string.txt_no_relevant_permission, TipDialog.TYPE.ERROR);
        }
    }
*/

    /**
     * 获取用户信息
     */
    private void reqInitUserInfo() {
        OkGo.<LzyResponse<FUser>>get(Consts.USERS_INFO_API)
                .execute(new LtbJsonCallback<LzyResponse<FUser>>(this, false,
                        new TypeReference<LzyResponse<FUser>>() {
                        }) {
                    @Override
                    public void onSuccess(Response<LzyResponse<FUser>> response) {
                        if (response.body().error_code == 0) {
                            FUser data = response.body().getData();
                            UserInfoCache.saveCacheAll(FrLaunchActivity.this, data);
                            // 游客登录 是否有性别---> 无：  startActivity(new Intent(mContext, GenderChoiceActivity.class));
                            // 游客登录 是否有性别---> 有：   reqAdsFromNet();
                            // 正式用户登录(肯定有性别)---> reqAdsFromNet();
                            int gender = data.getGender();
                            if (gender == -1) {
                                startActivity(new Intent(mContext, GenderChoiceActivity.class));
                                finish();
                            } else {
                                reqAdsFromNet();
                            }
                        }
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
                        LzyResponse<String> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<String>>() {
                                });
                        if (entity.error_code == 0) {
                            $(R.id.ctl_no_network_show).setVisibility(View.GONE);

                            JSONObject object = JSONObject.parseObject(entity.getData());
                            String token = object.getString("token");
                            UserInfoCache.saveToken(FrLaunchActivity.this, token);

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
                                    .error(R.drawable.img_default_ad)
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
                autoSkip = false;
                Uri uri = Uri.parse(adUrl);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        layoutAdvertising.setVisibility(View.VISIBLE);
        btnSkip.setText(String.format(getString(R.string.txt_skip_x), count));
        mHandler.sendEmptyMessageDelayed(0, 1000);
    }


    @Override
    public void onBackPressed() {

    }
}
