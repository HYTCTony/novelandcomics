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
import com.huli.foxread.R;
import com.huli.foxread.cache.UserInfoCache;
import com.huli.foxread.callbacks.ookkggoo.LtbCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Common;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.entity.PaymentInfoEntity;
import com.huli.foxread.entity.ReChargeSetEntity;
import com.huli.foxread.listeners.OnClickEvent;
import com.huli.foxread.ui.adapters.VipComboAdapter;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.ui.decoration.GridSpacingItemDecoration;
import com.huli.foxread.ui.dialogs.PaymentSelectDialog;
import com.huli.foxread.ui.widget.CheckBoxSample;
import com.huli.foxread.utils.DensityUtils;
import com.huli.foxread.utils.GlideUtil;
import com.huli.foxread.utils.Tos;
import com.kongzue.dialog.v3.TipDialog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MyPrivilegeActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvVipTypeTitle, tvVipTime, tvVipTips, tvAccountSetup;
    private ImageView ivIconVipSymbol;

    private ImageView ivHeadImg;
    private TextView tvNickname, tvTel;
    private TextView btnPrivilegeExplain;

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

        tvVipTypeTitle = $(R.id.tv_vip_type_title);
        tvVipTime = $(R.id.tv_expiration_time_vip);
        tvVipTips = $(R.id.tv_vip_tips_mp);
        tvAccountSetup = $(R.id.tv_instant_account_setup);
        ivIconVipSymbol = $(R.id.iv_icon_vip_symbol);

        ivHeadImg = $(R.id.iv_user_headImg);
        tvNickname = $(R.id.tv_user_nickname);
        tvTel = $(R.id.tv_user_tel);
        btnPrivilegeExplain = $(R.id.tv_asBtn_privilege_explain);

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
        btnPrivilegeExplain.setOnClickListener(this);
        btnOpenOrRenew.setOnClickListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {

        GlideUtil.loadCircle(this, ivHeadImg, UserInfoCache.getHeadPic(this));
        tvNickname.setText(UserInfoCache.getUserName(this));
        String phoneNum = UserInfoCache.getMobile(this);
        tvTel.setText(phoneNum.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2"));

        String str = getString(R.string.txt_agree_service_agreement);
        SpannableString spab = new SpannableString(str);
        spab.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
//                Tos.showShort(MyPrivilegeActivity.this, "服务协议");
                Intent intent = new Intent(MyPrivilegeActivity.this, CommonWebActivity.class);
                intent.putExtra(Common.KEY_URL, Consts.MEMBERSHIP_AGREEMENT_URL);
                startActivity(intent);
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

        boolean isVip = UserInfoCache.getIsVip(this);
        displayVipUI(isVip);

        btnOpenOrRenew.setText("12.00元 立即开通");

        reqRechargeCombo();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_asBtn_privilege_explain:       //查看特权说明
                Intent intent = new Intent(MyPrivilegeActivity.this, CommonWebActivity.class);
                intent.putExtra(Common.KEY_URL, Consts.PRIVILEGE_EXPLAIN_URL);
                startActivity(intent);
                break;

            case R.id.btn_open_or_renew_vip:
                String comboId = mAdapter.getSelectedComboId();
                if (!TextUtils.isEmpty(comboId)) {
                    reqSubmitOrder(comboId);
                }
                break;

            default:
                break;
        }
    }

    private void displayVipUI(boolean isVip) {
        if (isVip) {
            tvVipTypeTitle.setText("包月VIP");
            //TODO VIP 到期时间
            tvVipTime.setText("到期时间：2020-3-14");
            tvVipTips.setText(R.string.txt_tips_vip_state);
            tvAccountSetup.setVisibility(View.GONE);
            ivIconVipSymbol.setVisibility(View.VISIBLE);
        } else {
            tvVipTypeTitle.setText(null);
            tvVipTime.setText(null);
            tvVipTips.setText(null);
            tvAccountSetup.setVisibility(View.VISIBLE);
            ivIconVipSymbol.setVisibility(View.GONE);
        }
    }


    /**
     * 充值列表
     */
    private void reqRechargeCombo() {
        OkGo.<String>get(Consts.ORDER_RECHARGE_API)
                .execute(new LtbCallback(this) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<List<ReChargeSetEntity>> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<List<ReChargeSetEntity>>>() {
                                });
                        if (entity.error_code == 0) {
                            List<ReChargeSetEntity> datas = entity.getData();
                            mAdapter.setNewData(datas);
                        }
                    }
                });
    }

    private void reqSubmitOrder(String comboId) {
        OkGo.<String>post(Consts.ORDER_CREATE_API)
                .params(Consts.VIP_COMBO_ID, comboId)
                .execute(new LtbCallback(this) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<PaymentInfoEntity> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<PaymentInfoEntity>>() {
                                });
                        if (entity.error_code == 0) {
                            PaymentInfoEntity data = entity.getData();
                            PaymentSelectDialog.newInstance(data)
                                    .setLayoutId(R.layout.dialog_payment_select)
                                    .setConvertListener(viewConvertListener)
                                    .setDimAmout(0.5f)
                                    .setShowBottom(true)
                                    .setAnimStyle(R.style.DialogAnimation)
                                    /*.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialog) {
                                            Tos.showShort(MyPrivilegeActivity.this, "支付取消...");
                                        }
                                    })*/
                                    .show(getSupportFragmentManager());
                        } else {
                            TipDialog.show(MyPrivilegeActivity.this, entity.msg, TipDialog.TYPE.ERROR);
                        }
                    }
                });
    }


    private PaymentSelectDialog.PConvertListener viewConvertListener = (holder, data, dialog) -> {
        double paymentMoney = data.getPayment_money();
        holder.setText(R.id.tv_content_payment, data.getBody());
        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.CHINA);
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
        holder.setText(R.id.tv_need2pay_money_payment, String.format(getString(R.string.txt_need_2_pay), nf.format(paymentMoney)));
        CheckBoxSample cbWechat = holder.getView(R.id.checkbox_wechat);
        CheckBoxSample cbAlipay = holder.getView(R.id.checkbox_alipay);

        holder.setOnClickListener(R.id.rtl_asBtn_payment_wechat_pay, view -> {
            cbWechat.setChecked(true);
            cbAlipay.setChecked(false);
        });
        holder.setOnClickListener(R.id.rtl_asBtn_payment_alipay, view -> {
            cbWechat.setChecked(false);
            cbAlipay.setChecked(true);
        });

        Button btnConfirm = holder.getView(R.id.btn_confirm_2_pay);
        btnConfirm.setOnClickListener(new OnClickEvent() {
            @Override
            public void singleClick(View v) {
                if (cbWechat.isChecked()) {
                    //TODO 去支付
                    Tos.showShort(MyPrivilegeActivity.this, "微信支付");
                    dialog.dismiss();
                } else if (cbAlipay.isChecked()) {
                    //TODO 去支付
                    Tos.showShort(MyPrivilegeActivity.this, "支付宝");
                    dialog.dismiss();
                } else {
                    Tos.showShort(MyPrivilegeActivity.this, R.string.txt_plz_select_payment_way);
                }
            }

        });
    };

}
