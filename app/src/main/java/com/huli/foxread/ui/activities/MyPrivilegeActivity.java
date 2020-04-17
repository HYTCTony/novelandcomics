package com.huli.foxread.ui.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.alipay.sdk.app.PayTask;
import com.huli.foxread.FrApp;
import com.huli.foxread.R;
import com.huli.foxread.cache.UserInfoCache;
import com.huli.foxread.callbacks.ookkggoo.LtbCallback;
import com.huli.foxread.callbacks.ookkggoo.LtbJsonCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Common;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.entity.FUser;
import com.huli.foxread.entity.PaymentInfoEntity;
import com.huli.foxread.entity.ReChargeSetEntity;
import com.huli.foxread.entity.eventbus.VipChargerEvent;
import com.huli.foxread.entity.eventbus.WXPaySuccessEvent;
import com.huli.foxread.listeners.OnClickEvent;
import com.huli.foxread.payment.PayResult;
import com.huli.foxread.ui.adapters.VipComboAdapter;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.ui.decoration.GridSpacingItemDecoration;
import com.huli.foxread.ui.dialogs.PaymentSelectDialog;
import com.huli.foxread.ui.widget.CheckBoxSample;
import com.huli.foxread.utils.DateTimeUtil;
import com.huli.foxread.utils.DensityUtils;
import com.huli.foxread.utils.GlideUtil;
import com.huli.foxread.utils.Tos;
import com.kongzue.dialog.v3.MessageDialog;
import com.kongzue.dialog.v3.TipDialog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MyPrivilegeActivity extends BaseActivity implements View.OnClickListener, VipComboAdapter.OnVipComboSelectListenr {

    private TextView tvVipTypeTitle, tvVipTime, tvVipTips, tvAccountSetup;
    private ImageView ivIconVipSymbol;

    private ImageView ivHeadImg;
    private TextView tvNickname, tvTel;
    private TextView btnPrivilegeExplain;

    private RecyclerView recyclerView;
    private VipComboAdapter mAdapter;
    private TextView tvServiceAgreement;
    private Button btnOpenOrRenew;

    private String selectedComboId;

    private IWXAPI iwxapi;      //微信支付

    private static final int SDK_PAY_FLAG = 1;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
//                        Toast.makeText(OnlinePaymentActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(OnlinePaymentActivity.this, PaySuccessActivity.class);
//                        startActivity(intent);
                        reqUserInfo();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Tos.showShort(MyPrivilegeActivity.this, R.string.txt_payment_failure);
                    }
                    break;
                }
            }
        }
    };

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
        mAdapter = new VipComboAdapter(this);
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
        //需要登录---返回结果BaseActivity处理
        if (UserInfoCache.getIsTourist(mContext)) {
            LoginActivity.start4Result(this, LoginActivity.REQCODE_LOGIN);
            return;
        }

        iwxapi = WXAPIFactory.createWXAPI(this, FrApp.WECHAT_APP_ID);

        FUser userInfo = UserInfoCache.getUserInfo(mContext);
        GlideUtil.loadCircle(this, ivHeadImg, userInfo.getHttp_avatar());
        tvNickname.setText(userInfo.getUsername());
        String phoneNum = userInfo.getMobile();
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

        displayVipUI(userInfo);

        btnOpenOrRenew.setText(R.string.txt_activate_immediately);

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
                if (!TextUtils.isEmpty(selectedComboId)) {
                    reqSubmitOrder(selectedComboId);
                }
                break;

            default:
                break;
        }
    }


    @Override
    public void onVipComboSelect(String comboId, double discountPrice) {
        selectedComboId = comboId;
        btnOpenOrRenew.setText((new DecimalFormat("######0.00").format(discountPrice) + getString(R.string.txt_yuan_open)));
    }

    private void displayVipUI(FUser fUser) {
        boolean isVip = fUser.isIs_vip();
        if (isVip) {
            tvVipTypeTitle.setText(R.string.txt_monthly_vip);
            tvVipTime.setText(String.format(getString(R.string.txt_vip_end_time_colon),
                    DateTimeUtil.formatDateTime(fUser.getVip_end() * 1000, "yyyy-MM-dd")));
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
                    // 微信支付
                    reqChannelsAndPayment(Consts.PAY_WECHAT_API, data.getOrder_sn());
                    dialog.dismiss();
                } else if (cbAlipay.isChecked()) {
                    // 支付宝支付
                    reqChannelsAndPayment(Consts.PAY_ALIPAY_API, data.getOrder_sn());
                    dialog.dismiss();
                } else {
                    Tos.showShort(MyPrivilegeActivity.this, R.string.txt_plz_select_payment_way);
                }
            }

        });
    };


    /**
     * 获取充值列表
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

    /**
     * 提交订单
     */
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
                                    .setAnimStyle(R.style.PaymentDialogAnim)
                                    .show(getSupportFragmentManager());
                        } else {
                            TipDialog.show(MyPrivilegeActivity.this, entity.msg, TipDialog.TYPE.ERROR);
                        }
                    }
                });
    }

    /**
     * 获取用户信息---刷新会员时间
     */
    private void reqUserInfo() {
        OkGo.<LzyResponse<FUser>>get(Consts.USERS_INFO_API)
                .execute(new LtbJsonCallback<LzyResponse<FUser>>(this,
                        new TypeReference<LzyResponse<FUser>>() {
                        }) {
                    @Override
                    public void onSuccess(Response<LzyResponse<FUser>> response) {
                        if (response.body().error_code == 0) {
                            FUser data = response.body().getData();
                            UserInfoCache.saveUserInfo(MyPrivilegeActivity.this, data);

                            if (UserInfoCache.getIsVip(MyPrivilegeActivity.this)) {
                                tvVipTypeTitle.setText(R.string.txt_monthly_vip);
                                tvVipTime.setText(String.format(getString(R.string.txt_vip_end_time_colon),
                                        DateTimeUtil.formatDateTime(data.getVip_end() * 1000, "yyyy-MM-dd")));
                                tvVipTips.setText(R.string.txt_tips_vip_state);
                                tvAccountSetup.setVisibility(View.GONE);
                                ivIconVipSymbol.setVisibility(View.VISIBLE);

                                EventBus.getDefault().postSticky(new VipChargerEvent(true));

                                MessageDialog.build(MyPrivilegeActivity.this)
                                        .setTitle(R.string.txt_payment_success)
                                        .setMessage(R.string.txt_go2_experience_it)
                                        .setOkButton(R.string.txt_experience_it)
                                        .setCancelButton(R.string.txt_look_a_little_bit_more)
                                        .setCustomView(R.layout.dialog_payment_success, (dialog, v) -> {
                                        }).setOnOkButtonClickListener((baseDialog, v) -> {
                                    finish();
                                    return false;
                                }).show();
                            }
                        }
                    }
                });
    }


    /**
     * 选择支付通道--->发起支付
     */
    private void reqChannelsAndPayment(String payChannelsUrl, String orderId) {
        OkGo.<String>post(payChannelsUrl)
                .params(Consts.ORDER_ID, orderId)
                .execute(new LtbCallback(this) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<String> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<String>>() {
                                });
                        if (entity.error_code == 0) {
                            if (payChannelsUrl.equals(Consts.PAY_ALIPAY_API)) {
                                String orderInfo = entity.getData();
                                Runnable payRunnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        PayTask alipay = new PayTask(MyPrivilegeActivity.this);
                                        Map<String, String> result = alipay.payV2(orderInfo, true);
                                        Message msg = new Message();
                                        msg.what = SDK_PAY_FLAG;
                                        msg.obj = result;
                                        mHandler.sendMessage(msg);
                                    }
                                };
                                Thread payThread = new Thread(payRunnable);
                                payThread.start();
                            } else if (payChannelsUrl.equals(Consts.PAY_WECHAT_API)) {
                                String orderInfo = entity.getData();
                                wechatPay(orderInfo);
                            }
                        } else {
                            TipDialog.show(MyPrivilegeActivity.this, entity.msg, TipDialog.TYPE.ERROR);
                        }
                    }
                });
    }

    /**
     * 调起微信支付
     */
    private void wechatPay(String data) {
        boolean isPaySupported = iwxapi.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
        if (!isPaySupported) {
            Tos.showShort(this, "当前微信版本不支持支付功能");
            return;
        }

        JSONObject json = JSONObject.parseObject(data);
        PayReq req = new PayReq();
        req.appId = json.getString("appid");
        req.partnerId = json.getString("partnerid");
        req.prepayId = json.getString("prepayid");
        req.packageValue = json.getString("package");
        req.nonceStr = json.getString("noncestr");
        req.timeStamp = json.getString("timestamp");
        req.sign = json.getString("sign");
        // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
        iwxapi.sendReq(req);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWXPaySuccessEvent(WXPaySuccessEvent event) {
        if (event.getCode() == BaseResp.ErrCode.ERR_OK) {
            reqUserInfo();
        }
    }

}
