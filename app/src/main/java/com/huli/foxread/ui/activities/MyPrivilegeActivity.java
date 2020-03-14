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
import android.widget.ImageView;
import android.widget.TextView;

import com.huli.foxread.R;
import com.huli.foxread.ui.adapters.VipComboAdapter;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.ui.decoration.GridSpacingItemDecoration;
import com.huli.foxread.utils.DensityUtils;
import com.huli.foxread.utils.GlideUtil;
import com.huli.foxread.utils.Tos;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MyPrivilegeActivity extends BaseActivity implements View.OnClickListener {

    private ImageView ivHeadImg;
    private TextView tvNickname, tvTel;

    private RecyclerView recyclerView;
    private VipComboAdapter mAdapter;
    private TextView tvServiceAgreement;
    private Button btnOpenOrRenew;

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_my_privilege;
    }

    @Override
    public void initView(View view) {
        Toolbar toolbar = $(R.id.toolbar_normal);
        initToolBar(toolbar, R.string.txt_my_privilege);

        ivHeadImg = $(R.id.iv_user_headImg);
        tvNickname = $(R.id.tv_user_nickname);
        tvTel = $(R.id.tv_user_tel);

        recyclerView = $(R.id.recyclerView_vip_packages);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, DensityUtils.dp2px(this, 16), true));
        mAdapter = new VipComboAdapter();
        recyclerView.setAdapter(mAdapter);

        tvServiceAgreement = $(R.id.tv_agree_service_agreement);
        btnOpenOrRenew = $(R.id.btn_open_or_renew_vip);
    }

    @Override
    public void setListener() {
        btnOpenOrRenew.setOnClickListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {

        GlideUtil.loadCircle(this, ivHeadImg, "url");
        tvNickname.setText("拉不出屎一声吼！");
        tvTel.setText("177****6666");

        List<String> list = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            list.add("ssssssss" + i);
        }
        mAdapter.setNewData(list);

        String str = getString(R.string.txt_agree_service_agreement);
        SpannableString spab = new SpannableString(str);
        spab.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Tos.showShort(MyPrivilegeActivity.this, "服务协议");
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                /**set textColor**/
//                ds.setColor(ds.linkColor);
                ds.setColor(ContextCompat.getColor(MyPrivilegeActivity.this, R.color.txt_dark_gold));
                ds.setUnderlineText(true);
            }
        }, str.length() - 6, str.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tvServiceAgreement.setMovementMethod(LinkMovementMethod.getInstance());//不设置 没有点击事件
        tvServiceAgreement.setText(spab);

        btnOpenOrRenew.setText("12.00元 立即开通");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_open_or_renew_vip:

                break;

            default:
                break;
        }
    }
}
