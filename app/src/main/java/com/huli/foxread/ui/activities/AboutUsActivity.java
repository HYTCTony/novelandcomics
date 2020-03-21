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
import android.widget.TextView;

import com.huli.foxread.R;
import com.huli.foxread.contact.Common;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.utils.PackageUtils;
import com.huli.page.utils.FileUtils;
import com.huli.page.utils.TimeUtils;
import com.kongzue.dialog.v3.MessageDialog;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.UpgradeInfo;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

public class AboutUsActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvCurVer;
    private TextView btnViewDetail;
    private Button btnUpdate;
    private TextView tvPolicy;
    StringBuilder info = new StringBuilder();

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
        initToolBar(toolbar, R.string.txt_about_huli);

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
        }, 4, 10, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
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
        loadUpgradeInfo();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_asBtn_view_detail:
                String title = "新版本内容";
                String message = info.toString();
                if (TextUtils.isEmpty(message)) {
                    title = "已是最新版本";
                }
                MessageDialog.show(AboutUsActivity.this, title, message, "知道了")
                        .setCancelable(false)
                        .setOnOkButtonClickListener((baseDialog, v) -> {
                            baseDialog.doDismiss();
                            return false;
                        });
                break;
            case R.id.btn_update_app_version:
                /***** 检查更新 *****/
                Beta.checkUpgrade();
                break;
            default:
                break;
        }
    }

    private void loadUpgradeInfo() {
        if (btnUpdate == null)
            return;

        /***** 获取升级信息 *****/
        UpgradeInfo upgradeInfo = Beta.getUpgradeInfo();

        if (upgradeInfo == null) {
            btnUpdate.setText("已是最新版本");
            btnUpdate.setBackgroundResource(R.drawable.ripple_round_btn_gradual_bg_grey);
            return;
        }
//        info.append("id: ").append(upgradeInfo.id).append("\n");
        info.append("标题: ").append(upgradeInfo.title).append("\n");
        info.append("升级说明: ").append(upgradeInfo.newFeature).append("\n");
//        info.append("versionCode: ").append(upgradeInfo.versionCode).append("\n");
        info.append("版本号: ").append(upgradeInfo.versionName).append("\n");
        info.append("发布时间: ").append(TimeUtils.yyyyMMddHHmmss(upgradeInfo.publishTime)).append("\n");
//        info.append("安装包Md5: ").append(upgradeInfo.apkMd5).append("\n");
        info.append("安装包下载地址: ").append(upgradeInfo.apkUrl).append("\n");
        info.append("安装包大小: ").append(FileUtils.getFileSize(upgradeInfo.fileSize)).append("\n");
//        info.append("弹窗间隔（ms）: ").append(upgradeInfo.popInterval).append("\n");
//        info.append("弹窗次数: ").append(upgradeInfo.popTimes).append("\n");
//        info.append("发布类型: ").append(upgradeInfo.publishType == 0 ? "测试" : "正式").append("\n");
//        info.append("弹窗类型（1:建议 2:强制 3:手工）: ").append(upgradeInfo.upgradeType);

    }

}