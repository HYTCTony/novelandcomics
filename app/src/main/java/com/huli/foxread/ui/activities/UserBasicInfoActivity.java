package com.huli.foxread.ui.activities;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.huli.foxread.R;
import com.huli.foxread.cache.UserInfoCache;
import com.huli.foxread.callbacks.ookkggoo.LtbCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.engines.GlideEngine;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.utils.GlideUtil;
import com.huli.foxread.utils.Tos;
import com.kongzue.dialog.util.InputInfo;
import com.kongzue.dialog.v3.InputDialog;
import com.kongzue.dialog.v3.MessageDialog;
import com.kongzue.dialog.v3.TipDialog;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.PostRequest;

import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class UserBasicInfoActivity extends BaseActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks,
        EasyPermissions.RationaleCallbacks {


    private ImageView ivHeadImg;
    private TextView tvNickname, tvGender, tvAccountId;

    private int gender = -1;    //选择性别
    private String path;        //选择图片返回的路径

    private ClipboardManager myClipboard;
    private ClipData myClip;

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_user_basic_info;
    }

    @Override
    public void initView(View view) {
        Toolbar toolbar = $(R.id.toolbar_normal);
        initToolBar(toolbar, R.string.txt_basic_info);

        ivHeadImg = $(R.id.iv_user_headImg);
        tvNickname = $(R.id.tv_user_nickname);
        tvGender = $(R.id.tv_user_gender);
        tvAccountId = $(R.id.tv_user_account_id);
    }

    @Override
    public void setListener() {
        findViewById(R.id.rtl_asBtn_user_headImg).setOnClickListener(this);
        findViewById(R.id.rtl_asBtn_user_nickname).setOnClickListener(this);
        findViewById(R.id.rtl_asBtn_user_gender).setOnClickListener(this);
        findViewById(R.id.rtl_asBtn_user_account_id).setOnLongClickListener(view -> {
            myClip = ClipData.newPlainText("my_id", tvAccountId.getText().toString());
            myClipboard.setPrimaryClip(myClip);
            Tos.showShort(UserBasicInfoActivity.this, R.string.tips_copy_success);
            return true;
        });
    }

    @Override
    public void doBusiness(Context mContext) {
        myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        displayUserInfo(mContext);
    }

    private void displayUserInfo(Context mContext) {
        GlideUtil.loadCircle(mContext, ivHeadImg, UserInfoCache.getHeadPic(mContext));
        tvNickname.setText(UserInfoCache.getUserName(mContext));
        tvAccountId.setText(UserInfoCache.getUserId(mContext));
        tvGender.setText(UserInfoCache.getGender(mContext) == 0 ? getString(R.string.txt_male) : getString(R.string.txt_female));
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rtl_asBtn_user_headImg:
                externalAndCameraTask();
                break;
            case R.id.rtl_asBtn_user_nickname:
                InputDialog.show(this, R.string.txt_hint_input_nickname, R.string.hint_nickname_length_limit, R.string.txt_confirm, R.string.txt_cancel)
                        .setInputInfo(new InputInfo()
                                .setMAX_LENGTH(8)     //限制最大输入长度
                                .setMultipleLines(true)       //支持多行输入
                        )
                        .setOnOkButtonClickListener((baseDialog, v1, inputStr) -> {
                            if (TextUtils.isEmpty(inputStr) || inputStr.length() < 4) {
                                return true;
                            }
                            //提交请求
                            reqSetUserProfile(Consts.USERNAME, inputStr);
                            return false;
                        });
                break;
            case R.id.rtl_asBtn_user_gender:
                MessageDialog.build(this)
                        .setTitle(R.string.txt_plz_select_your_gender)
                        .setMessage("")
                        .setOkButton(R.string.txt_confirm)
                        .setCancelButton(R.string.txt_cancel)
                        .setBackgroundResId(R.drawable.shape_round_whitebg)
                        .setCustomView(R.layout.layout_custom_gender_select2, (dialog, v) -> {
                            //绑定布局事件，可使用v.findViewById(...)来获取子组件
                            RadioGroup rg = v.findViewById(R.id.radioGroup_gender);
                            rg.setOnCheckedChangeListener((radioGroup, i) -> {
                                if (radioGroup.getCheckedRadioButtonId() == R.id.rb_man_gender) {
                                    gender = 0;
                                } else if (radioGroup.getCheckedRadioButtonId() == R.id.rb_female_gender) {
                                    gender = 1;
                                } else {
                                    gender = -1;
                                }
                            });
                        })
                        .setOnOkButtonClickListener((baseDialog, v) -> {
                            if (gender == 0) {
                                tvGender.setText(getString(R.string.txt_male));
                            } else if (gender == 1) {
                                tvGender.setText(getString(R.string.txt_female));
                            } else {
                                return true;
                            }
                            //提交请求
                            reqSetUserProfile(Consts.GENDER, String.valueOf(gender));
                            return false;
                        })
                        .show();

                break;
            default:
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 清除所有缓存 例如：压缩、裁剪、视频、音频所生成的临时文件
        PictureFileUtils.deleteAllCacheDirFile(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    private static final String[] EXTERNAL_AND_CAMERA = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    private static final int RC_EXTERNAL_CAMERA_PERM = 124;

    private boolean hasExternaLAndCameraPermissions() {
        return EasyPermissions.hasPermissions(this, EXTERNAL_AND_CAMERA);
    }

    @AfterPermissionGranted(RC_EXTERNAL_CAMERA_PERM)
    public void externalAndCameraTask() {
        if (hasExternaLAndCameraPermissions()) {
            PictureSelector.create(this)
                    .openGallery(PictureMimeType.ofImage())             // 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                    .selectionMode(PictureConfig.SINGLE)                // 多选 or 单选
                    .loadImageEngine(GlideEngine.createGlideEngine())   // 请参考Demo GlideEngine.java
                    .enableCrop(true)                                   // 是否裁剪
                    .hideBottomControls(false)                          // 是否显示uCrop工具栏，默认不显示
                    .withAspectRatio(1, 1)// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                    .imageFormat(PictureMimeType.PNG)                   // 拍照保存图片格式后缀,默认jpeg
                    .compress(true)                                     // 是否压缩
                    .cutOutQuality(100)                                 // 裁剪压缩质量 默认90
                    .minimumCompressSize(100)                           // 小于100kb的图片不压缩
                    .forResult(result -> {
                        if (result.size() > 0) {
                            LocalMedia media = result.get(0);
                            path = "";
                            if (media.isCut() && !media.isCompressed()) {
                                // 裁剪过
                                path = media.getCutPath();
                            } else if (media.isCompressed() || (media.isCut() && media.isCompressed())) {
                                // 压缩过,或者裁剪同时压缩过,以最终压缩过图片为准（因为是先裁剪后压缩的）
                                path = media.getCompressPath();
                            } else {
                                // 原图
                                path = media.getPath();
                            }
                            File file = new File(path);
                            reqSetUserProfile(file);
                        }
                    });

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
           /* String yes = getString(R.string.yes);
            String no = getString(R.string.no);*/

            // Do something after user returned from app settings screen, like showing a Toast.
            TipDialog.show(this, R.string.txt_no_relevant_permission, TipDialog.TYPE.ERROR);
        }
    }

    @Override
    public void onRationaleAccepted(int requestCode) {
        //点击dialog的确认按钮回调此方法
//        Log.d(TAG, "onRationaleAccepted:" + requestCode);
    }

    @Override
    public void onRationaleDenied(int requestCode) {
        //点击dialog的取消按钮回调此方法
        Log.d(TAG, "onRationaleDenied:" + requestCode);
    }

    private void reqSetUserProfile(String key, String value) {
        reqSetUserProfile(key, value, null);
    }

    private void reqSetUserProfile(File headImg) {
        reqSetUserProfile(null, null, headImg);
    }


    private void reqSetUserProfile(String paramKey, String paramValue, File headImg) {
        PostRequest<String> postRequest = OkGo.<String>post(Consts.SET_USER_PROFILE_API)
                .params(paramKey, paramValue);

        if (!TextUtils.isEmpty(paramKey)) {
            postRequest.params(paramKey, paramValue);
        }
        if (headImg != null) {
            postRequest.params(Consts.AVATAR, headImg);
        }

        postRequest.execute(new LtbCallback(this) {
            @Override
            public void onSuccess(Response<String> response) {
                LzyResponse<String> entity = JSONObject.parseObject(response.body(), new TypeReference<LzyResponse<String>>() {
                });
                if (entity.error_code == 0) {
                    if (!TextUtils.isEmpty(path) && headImg != null) {
                        GlideUtil.loadCircle(UserBasicInfoActivity.this, ivHeadImg, path);
                        UserInfoCache.saveHeadPic(UserBasicInfoActivity.this, path);
                    }

                    if (paramKey.equals(Consts.USERNAME)) {
                        tvNickname.setText(paramValue);
                        UserInfoCache.saveUserName(UserBasicInfoActivity.this, paramValue);
                    } else if (paramKey.equals(Consts.GENDER)) {
                        if (gender == 0) {
                            tvGender.setText(getString(R.string.txt_male));
                        } else if (gender == 1) {
                            tvGender.setText(getString(R.string.txt_female));
                        }
                        UserInfoCache.saveGender(UserBasicInfoActivity.this, gender);
                    }

                    setResult(RESULT_OK);
                    Tos.showShort(UserBasicInfoActivity.this, R.string.hint_modify_success);
                } else {
                    Tos.showShort(UserBasicInfoActivity.this, entity.msg);
                }
            }
        });
    }

}
