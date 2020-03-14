package com.huli.foxread.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.huli.foxread.R;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.utils.PackageUtils;
import com.huli.foxread.utils.Tos;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

public class AboutUsActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvCurVer;
    private TextView btnViewDetail;
    private Button btnUpdate;
    private TextView tvPolicy;

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
                Tos.showShort(AboutUsActivity.this, "用户协议");
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
                Tos.showShort(AboutUsActivity.this, "用户隐私");
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
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_asBtn_view_detail:

                break;
            case R.id.btn_update_app_version:

                break;
            default:
                break;
        }
    }
}
