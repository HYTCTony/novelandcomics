package com.huli.foxread.ui.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.huli.foxread.FrApp;
import com.huli.foxread.R;
import com.huli.foxread.entity.AdEntity;
import com.huli.foxread.listeners.OnClickEvent;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.utils.NetworkUtil;
import com.huli.foxread.utils.Tos;
import com.kongzue.dialog.v3.TipDialog;
import com.lzy.okgo.OkGo;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class FrLaunchActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {

    private static final String[] EXTERNAL_AND_CAMERA = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};
    private static final int RC_EXTERNAL_CAMERA_PERM = 124;

    private ConstraintLayout layoutAdvertising;
    private Button btnSkip;
    private AdEntity adEntity;

    private ConstraintLayout ctlNoNetwork;
    private TextView btnNetworkSetting, btnReconnect;

    //广告是否已经显示过（考虑到网络情况）
    private boolean adHasbeenShow = false;

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
        ctlNoNetwork = $(R.id.ctl_no_network_show);
        btnNetworkSetting = $(R.id.tv_asBtn_network_setting);
        btnReconnect = $(R.id.tv_asBtn_reconnect);
    }

    @Override
    public void setListener() {
        btnSkip.setOnClickListener(onClickEvent);

        btnNetworkSetting.setOnClickListener(onClickEvent);
        btnReconnect.setOnClickListener(onClickEvent);
    }

    @Override
    public void doBusiness(Context mContext) {
//        startNetReq();

       /* Random r = new Random();
        i = r.nextInt(10);
        if (i < 5) {
            //随机概率会出现广告页
        } else {
            startActivity(new Intent(FrLaunchActivity.this, MainActivity.class));
            finish();
        }*/

        mHandler.sendEmptyMessageDelayed(1, 2000);
    }

    @Override
    protected void handleNetWorkChange(boolean has) {
        super.handleNetWorkChange(has);
        if (!has) {
            ctlNoNetwork.setVisibility(View.VISIBLE);
            OkGo.getInstance().cancelAll();
        }
    }

    /*  private int[] picsLayout = {R.layout.layout_hello, R.layout.layout_hello2,
              R.layout.layout_hello3, R.layout.layout_hello4, R.layout.layout_hello5};
      private int i;*/
    private int count = 5;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 0) {
                btnSkip.setText(String.format(getString(R.string.txt_skip_x), getCount()));
                mHandler.sendEmptyMessageDelayed(0, 1000);
            }else if(msg.what==1){
                layoutAdvertising.setVisibility(View.VISIBLE);
                btnSkip.setText(String.format(getString(R.string.txt_skip_x), count));
                mHandler.sendEmptyMessageDelayed(0, 1000);
            }
            return false;
        }
    });

    public int getCount() {
        count--;
        if (count == 0) {
            btnSkip.performClick();
        }
        return count;
    }

    private void startNetReq() {
        String mToken = FrApp.getInstance().getToken();
        /*if (TextUtils.isEmpty(mToken)) {
            uniqueIdLoginTask();
        } else {
            getUserInfoTask(mToken);
        }*/
    }

    private OnClickEvent onClickEvent = new OnClickEvent() {
        @Override
        public void singleClick(View v) {
            switch (v.getId()) {
                case R.id.btn_skip_ad:
                    startActivity(new Intent(FrLaunchActivity.this, MainActivity.class));
                    mHandler.removeMessages(0);
                    finish();
                    break;
                case R.id.tv_asBtn_network_setting:

                    break;
                case R.id.tv_asBtn_reconnect:
                    if (NetworkUtil.isNetworkAvailable(FrLaunchActivity.this)) {
                        startNetReq();
                    } else {
                        Tos.showShort(FrLaunchActivity.this, R.string.txt_network_error);
                    }
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    private boolean hasExternaLAndCameraPermissions() {
        return EasyPermissions.hasPermissions(this, EXTERNAL_AND_CAMERA);
    }


    @AfterPermissionGranted(RC_EXTERNAL_CAMERA_PERM)
    public void goMain() {
        if (hasExternaLAndCameraPermissions()) {
            startActivity(new Intent(FrLaunchActivity.this, MainActivity.class));
            finish();
        } else {
            // Ask for both permissions
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.rationale_location_contacts),
                    RC_EXTERNAL_CAMERA_PERM,
                    EXTERNAL_AND_CAMERA);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
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


    private void uniqueIdLoginTask() {
        /*String psuedoID = PsuedoIDUtil.getUniquePsuedoID();
        OkGo.<String>post(Consts.USE_UNIQUE_ID_LOGIN_OR_REG_API)
                .params(Consts.UNIQUE_ID, psuedoID)
                .execute(new EncryptCallback(this) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        BaseEntity<LoginRpsEntity> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<BaseEntity<LoginRpsEntity>>() {
                                });
                        if (entity.getError() == 0) {
                            LoginRpsEntity data = entity.getData();
                            String mToken = data.getToken();
                            PvApp.getInstance().setToken(mToken);

                            getUserInfoTask(mToken);
                        }
                    }
                });*/
    }

    private void getUserInfoTask(String token) {
       /* OkGo.<String>post(Consts.USERS_INFO_API)
                .params(Consts.TOKEN, token)
                .execute(new EncryptCallback(this) {
                    @Override
                    public void onSuccess(Response<String> response) {
//                        Log.e(TAG, "用户信息===" + response.body());
                        BaseEntity<WUser> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<BaseEntity<WUser>>() {
                                });
                        if (entity.getError() == 0) {
                            WUser wUser = entity.getData();
                            boolean isEmpty = TextUtils.isEmpty(wUser.getTel());
                            PvApp.getInstance().setBindPhone(!isEmpty);

                            //保存用户信息
                            mACache.put(Common.USER_DTO, wUser);

                            //TODO 记得解除注释
//                            if (adHasbeenShow) {
                                goMain();
//                            } else {
//                                getADTask();
//                            }
                        }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        if (loadingView != null) {
                            loadingView.stop();
                            loadingView.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }
                });*/
    }

    private void getADTask() {
      /*  OkGo.<String>post(Consts.GET_AD_API)
                .execute(new EncryptCallback(this) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        BaseEntity<AdEntity> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<BaseEntity<AdEntity>>() {
                                });
                        if (entity.getError() == 0) {
                            adEntity = entity.getData();
                            if (adEntity != null) {
                                GlideApp.with(FrLaunchActivity.this)
                                        .load(adEntity.getImage())
                                        .error(R.drawable.img_default_ad)
                                        .into(new SimpleTarget<Drawable>() {
                                            @Override
                                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                                showADDialog(resource, adEntity.getUrl());
                                            }

                                            @Override
                                            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                                super.onLoadFailed(errorDrawable);
                                                showADDialog(errorDrawable, adEntity.getUrl());
                                            }
                                        });
                            }
                        }
                    }
                });*/

    }

    /**
     * 显示广告
     *
     * @param resource
     * @param adUrl
     */
    private void showADDialog(Drawable resource, String adUrl) {
       /* CustomDialog adDialog = CustomDialog.build(this, R.layout.layout_dialog_ad_fullscreen, (dialog, v) -> {
            ImageView ivAdPic = v.findViewById(R.id.iv_advertising_picture);
            TimingButton btnEnter = v.findViewById(R.id.timingButton_enter);
            ivAdPic.setOnClickListener(new OnClickEvent() {
                @Override
                public void singleClick(View v) {
                    if (TextUtils.isEmpty(adUrl) || !NetworkUtil.isNetworkAvailable(FrLaunchActivity.this)) {
                        return;
                    }
                    //TODO 打开广告页面
                }
            });
            btnEnter.setOnClickListener(new OnClickEvent() {
                @Override
                public void singleClick(View v) {
                    if (NetworkUtil.isNetworkAvailable(FrLaunchActivity.this)) {
                        goMain();
                    } else {
                        Tos.showShort(FrLaunchActivity.this, R.string.txt_network_error);
                        dialog.doDismiss();
                    }
                }
            });

            if (resource != null) {
                ivAdPic.setImageDrawable(resource);
            }
            btnEnter.start();
        });
        adDialog.setCancelable(false);
        adDialog.setOnBackClickListener(() -> {
            finish();
            return false;
        });
        adDialog.setFullScreen(true);
        adDialog.show();

        adHasbeenShow = true;*/
    }


}
