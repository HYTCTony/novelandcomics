package com.huli.foxread.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.azhon.appupdate.config.UpdateConfiguration;
import com.azhon.appupdate.dialog.NumberProgressBar;
import com.azhon.appupdate.listener.OnDownloadListener;
import com.azhon.appupdate.manager.DownloadManager;
import com.huli.foxread.FrApp;
import com.huli.foxread.R;
import com.huli.foxread.callbacks.ookkggoo.LtbCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Common;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.entity.AppVersionInfo;
import com.huli.foxread.entity.UpdateInfo;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.ui.dialogs.CommonDialog;
import com.huli.foxread.ui.dialogs.base.BaseDialog;
import com.huli.foxread.utils.DateTimeUtil;
import com.huli.foxread.utils.PackageUtils;
import com.kongzue.dialog.v3.MessageDialog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

public class AboutUsActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvCurVer;
    private TextView btnViewDetail;
    private Button btnUpdate;
    private TextView tvPolicy;
    private StringBuilder info = new StringBuilder();

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_about_us;
    }

    @Override
    public void initView(View view) {
        Toolbar toolbar = $(R.id.toolbar_normal);
        initToolBar(toolbar, R.string.txt_about_us);

        tvCurVer = $(R.id.tv_cur_app_version);
        btnViewDetail = $(R.id.tv_asBtn_view_detail);
        btnUpdate = $(R.id.btn_update_app_version);
        tvPolicy = $(R.id.tv_fox_policy);
    }

    @Override
    public void setListener() {
        btnViewDetail.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {
        tvCurVer.setText((getString(R.string.txt_cur_app_version_l) + PackageUtils.getVersionName(mContext)));

        SpannableString spannableString = new SpannableString(getString(R.string.txt_foxread_user_and_privacy_policy));
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
//                Tos.showShort(AboutUsActivity.this, "用户协议");
                Intent intent = new Intent(AboutUsActivity.this, CommonWebActivity.class);
                intent.putExtra(Common.KEY_URL, Consts.USER_AGREEMENT_URL);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                /**set textColor**/
                ds.setColor(ContextCompat.getColor(AboutUsActivity.this, R.color.txt_red));
                /**Remove the underline**/
                ds.setUnderlineText(false);
            }
        }, 8, 14, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
//                Tos.showShort(AboutUsActivity.this, "用户隐私");
                Intent intent = new Intent(AboutUsActivity.this, CommonWebActivity.class);
                intent.putExtra(Common.KEY_URL, Consts.PRIVACY_POLICY_URL);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                /**set textColor**/
                ds.setColor(ContextCompat.getColor(AboutUsActivity.this, R.color.txt_red));
                /**Remove the underline**/
                ds.setUnderlineText(false);
            }
        }, spannableString.length() - 6, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tvPolicy.setMovementMethod(LinkMovementMethod.getInstance());//不设置 没有点击事件
        tvPolicy.setHighlightColor(ContextCompat.getColor(this, R.color.transparent));
        tvPolicy.setText(spannableString);

        getCurVersionInfo();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_asBtn_view_detail:
                String title = "当前版本内容";
                String message = info.toString();
                if (TextUtils.isEmpty(message)) {
                    return;
                }
                MessageDialog.show(AboutUsActivity.this, title, message, "知道了")
                        .setCancelable(false)
                        .setOnOkButtonClickListener((baseDialog, v) -> {
                            baseDialog.doDismiss();
                            return false;
                        });
                break;
            case R.id.btn_update_app_version:
                /* 检查更新 */
                checkNewVersion();
                break;
            default:
                break;
        }
    }


    private UpdateInfo newVerInfo;

    private void loadUpgradeInfo(AppVersionInfo appVersionInfo) {
        if (btnUpdate == null)
            return;

        newVerInfo = appVersionInfo.getNewVersion();
        if (appVersionInfo.getNewVersion() == null) {
            btnUpdate.setText("已是最新版本");
            btnUpdate.setBackgroundResource(R.drawable.ripple_round_btn_gradual_bg_grey);
            btnUpdate.setEnabled(true);
        }

        UpdateInfo nowVersion = appVersionInfo.getNowVersion();
        if (nowVersion != null) {
            info.append("升级说明: ").append(nowVersion.getContent()).append("\n");
            info.append("版本名: ").append(nowVersion.getVersionName()).append("\n");
            info.append("发布时间: ").append(DateTimeUtil.formatDateTime(nowVersion.getReleaseTime() * 1000)).append("\n");
            info.append("安装包下载地址: ").append(nowVersion.getDownloadurl()).append("\n");
            info.append("安装包大小: ").append(nowVersion.getPackagesize()).append("M").append("\n");
        }
    }


    /**
     * 获取版本详情
     */
    private void getCurVersionInfo() {
        OkGo.<String>post(Consts.VERSION_DETAIL_API)
                .params(Consts.FACILITY, Consts.DEVICE_ANDROID)
                .params(Consts.VERSION_CODE, PackageUtils.getVersionCode(this))
                .execute(new LtbCallback(this, false) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<AppVersionInfo> entity = JSONObject.parseObject(response.body(), new TypeReference<LzyResponse<AppVersionInfo>>() {
                        });
                        if (entity.error_code == 0) {
                            AppVersionInfo data = entity.getData();
                            loadUpgradeInfo(data);
                        }
                    }
                });
    }

    /**
     * 检测更新
     */
    private void checkNewVersion() {
        OkGo.<String>post(Consts.VERSION_CHECK_API)
                .params(Consts.FACILITY, Consts.DEVICE_ANDROID)
                .params(Consts.VERSION_CODE, PackageUtils.getVersionCode(this))
                .execute(new LtbCallback(this, false) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<UpdateInfo> entity = JSONObject.parseObject(response.body(), new TypeReference<LzyResponse<UpdateInfo>>() {
                        });
                        if (entity.error_code == 0) {
                            newVerInfo = entity.getData();
                            boolean isForce = newVerInfo.getEnforce() == 1;
                            CommonDialog.newInstance()
                                    .setLayoutId(R.layout.layout_custom_dialog_version_check)
                                    .setConvertListener((holder, dialog) -> {
                                        ImageView btnClose = holder.getView(R.id.iv_asBtn_close_update);
                                        btnClose.setVisibility(isForce ? View.GONE : View.VISIBLE);
                                        NumberProgressBar progressBar = holder.getView(R.id.numberProgressBar_download_apk);
                                        progressBar.setVisibility(isForce ? View.VISIBLE : View.GONE);
                                        holder.setText(R.id.tv_new_version_name, newVerInfo.getVersionName());
                                        holder.setText(R.id.tv_update_info_content, newVerInfo.getContent());

                                        btnClose.setOnClickListener(v -> dialog.dismiss());
                                        holder.setOnClickListener(R.id.versionchecklib_version_dialog_commit, v -> {
                                            downloadApkTask(newVerInfo, progressBar, dialog);
                                            if (!isForce) {
                                                dialog.dismiss();
                                            }
                                        });
                                    })
                                    .setDimAmout(0.5f)
                                    .setOutCancel(!isForce)
                                    .setBackCancel(!isForce)
                                    .setMargin(32)
                                    .setShowBottom(false)
                                    .setAnimStyle(R.style.BaseDialog)
                                    .show(getSupportFragmentManager());
                        }
                    }
                });
    }

    /**
     * 下载APK
     *
     * @param updateInfo  更新信息
     * @param progressBar 进度条
     * @param dialog      更新提示框
     */
    private void downloadApkTask(UpdateInfo updateInfo, NumberProgressBar progressBar, BaseDialog dialog) {
        if (updateInfo != null) {
            DownloadManager manager = DownloadManager.getInstance(this);
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
                                dialog.dismiss();
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
            manager.setApkName("FoxRead.apk")
                    .setApkUrl(updateInfo.getDownloadurl())
                    .setSmallIcon(R.mipmap.app_huli_logo_small)
                    .download();
        }
    }


}