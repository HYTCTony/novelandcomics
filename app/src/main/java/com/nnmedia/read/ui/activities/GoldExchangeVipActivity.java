package com.nnmedia.read.ui.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.tablayout.SlidingScaleTabLayout;
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialog.interfaces.OnDismissListener;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.v3.MessageDialog;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.nnmedia.novel.R;
import com.nnmedia.novel.RxHttp;
import com.nnmedia.page.utils.SpanUtils;
import com.nnmedia.read.cache.UserInfoCache;
import com.nnmedia.read.cache.VipCache;
import com.nnmedia.read.contact.Common;
import com.nnmedia.read.contact.Consts;
import com.nnmedia.read.ebsevent.VipChargerEvent;
import com.nnmedia.read.ebsevent.WebPayEvent;
import com.nnmedia.read.entity.CapitalEntity;
import com.nnmedia.read.entity.FUser;
import com.nnmedia.read.entity.PaymentInfoEntity;
import com.nnmedia.read.entity.pay.WePay;
import com.nnmedia.read.listeners.OnClickEvent;
import com.nnmedia.read.rxhttp.OnError;
import com.nnmedia.read.ui.adapters.VipMealAdapter;
import com.nnmedia.read.ui.base.BaseActivity;
import com.nnmedia.read.ui.dialogs.PaymentSelectDialog;
import com.nnmedia.read.ui.widget.CheckBoxSample;
import com.nnmedia.read.ui.widget.WrapViewPager;
import com.nnmedia.read.utils.DateTimeUtil;
import com.nnmedia.read.utils.GlideUtil;
import com.nnmedia.read.utils.NetworkUtil;
import com.nnmedia.read.utils.Tos;
import com.nnmedia.read.utils.UniqueIdManager;
import com.orhanobut.logger.Logger;
import com.rxjava.rxlife.RxLife;
import com.tencent.bugly.crashreport.CrashReport;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.NumberFormat;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

public class GoldExchangeVipActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvVipTypeTitle, tvVipTime, tvVipTips, tvAccountSetup;
    private ImageView ivIconVipSymbol;

    private TextView ivAsBtnAccount;

    private ImageView ivHeadImg;
    private TextView tvNickname, tvTel;

    private TextView btnPrivilegeExplain;
    private Button btnOpenOrRenew;

    private LinearLayout llContent;
    //    private RecyclerView recyclerView;
//    private VipExchangeAdapter mAdapter;
    private TextView tvServiceAgreement;

    private String selectedComboId;

    private int type;
    private int payType;

    private String platform;

    private SlidingScaleTabLayout tabLayout;
    private WrapViewPager viewPager;

    private static final String TYPE = "exchangeType";

    public static void start(Context context, int exchangeType) {
        Intent starter = new Intent(context, GoldExchangeVipActivity.class);
        starter.putExtra("exchangeType", exchangeType);
        context.startActivity(starter);
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_gold_exchange_vip;
    }

    @Override
    public void initView(View view) {
        Toolbar toolbar = $(R.id.toolbar_normal);
        initToolBar(toolbar, R.string.txt_my_privilege);

        tvVipTypeTitle = $(R.id.tv_vip_type_title);
        ivAsBtnAccount = $(R.id.iv_asBtn_account);
        tvVipTime = $(R.id.tv_expiration_time_vip);
        tvVipTips = $(R.id.tv_vip_tips_mp);
        tvAccountSetup = $(R.id.tv_instant_account_setup);
        ivIconVipSymbol = $(R.id.iv_icon_vip_symbol);

        ivHeadImg = $(R.id.iv_user_headImg);
        tvNickname = $(R.id.tv_user_nickname);
        tvTel = $(R.id.tv_user_tel);
        btnPrivilegeExplain = $(R.id.tv_asBtn_privilege_explain);

        llContent = $(R.id.ll_content_member_combo);
//        recyclerView = $(R.id.recyclerView_vip_packages);
//        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setNestedScrollingEnabled(false);
//        recyclerView.addItemDecoration(new GridSpacingItemDecoration(3, DensityUtils.dp2px(this, 16), true));
//        mAdapter = new VipExchangeAdapter(this);
//        recyclerView.setAdapter(mAdapter);

        tvServiceAgreement = $(R.id.tv_agree_service_agreement);
        btnOpenOrRenew = $(R.id.btn_open_or_renew_vip);

        tabLayout = $(R.id.slidingtablayout);
        viewPager = $(R.id.viewpager);
        String[] tabTitles = getResources().getStringArray(R.array.tab_vip);
        viewPager.setOffscreenPageLimit(tabTitles.length);
        viewPager.setAdapter(new VipMealAdapter(getSupportFragmentManager(), tabTitles));
        tabLayout.setViewPager(viewPager);
        tabLayout.setCurrentTab(type);
    }

    @Override
    public void setListener() {
        btnPrivilegeExplain.setOnClickListener(this);
        btnOpenOrRenew.setOnClickListener(this);
        ivAsBtnAccount.setOnClickListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {

        if (UserInfoCache.getIsTourist(mContext)) {
            LoginActivity2.start4Result(this, LoginActivity2.REQCODE_LOGIN);
            return;
        }

        FUser userInfo = UserInfoCache.getUserInfo(mContext);
        GlideUtil.loadCircle(this, ivHeadImg, userInfo.getHttp_avatar());
        tvNickname.setText(userInfo.getUsername());
        String phoneNum = userInfo.getMobile();
        tvTel.setText(phoneNum.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2"));

        displayVipUI(userInfo);
        btnOpenOrRenew.setText(R.string.txt_go_to_pay);
//        if (userInfo.getSuper_vip() != 1) {
//            reqRechargeCombo();
//        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_asBtn_privilege_explain:
//                Intent intent = new Intent(GoldExchangeVipActivity.this, CommonWebActivity.class);
//                intent.putExtra(Common.KEY_URL, Consts.PRIVILEGE_EXPLAIN_URL);
//                startActivity(intent);
                break;
            case R.id.btn_open_or_renew_vip:
                type = viewPager.getCurrentItem();
                selectedComboId = VipCache.getComboId(GoldExchangeVipActivity.this, type);
                if (!TextUtils.isEmpty(selectedComboId)) {
                    reqSubmitOrder(selectedComboId);
                }
                break;
            case R.id.iv_asBtn_account:
                BillDetailActivity.start(GoldExchangeVipActivity.this);
                break;
        }
    }

//    @Override
//    public void onVipComboSelect(String comboId, double discountPrice) {
//        selectedComboId = comboId;
//    }

    /**
     * 不同身份用户对应不同UI
     */
    private void displayVipUI(FUser fUser) {
        btnOpenOrRenew.setText(R.string.txt_activate_immediately);
        if (fUser.getSuper_vip() == 1) {    //终身会员
            tvVipTypeTitle.setText(R.string.txt_honor_vip);
            tvVipTime.setText(String.format(getString(R.string.txt_vip_end_time_colon), getString(R.string.txt_permanent_validity)));
            tvVipTips.setText(R.string.txt_tips_vip_state_life_member);
            tvAccountSetup.setVisibility(View.GONE);
            ivIconVipSymbol.setVisibility(View.VISIBLE);

            llContent.setVisibility(View.GONE);
            btnOpenOrRenew.setVisibility(View.GONE);
        } else {     //非终身VIP
            if (fUser.isIs_vip()) {     //普通VIP
                tvVipTypeTitle.setText(R.string.txt_monthly_vip);
                tvVipTime.setText(String.format(getString(R.string.txt_vip_end_time_colon),
                        DateTimeUtil.formatDateTime(fUser.getVip_end() * 1000, DateTimeUtil.DF_YYYY_MM_DD)));
                tvVipTips.setText(R.string.txt_tips_vip_state);
                tvAccountSetup.setVisibility(View.GONE);
                ivIconVipSymbol.setVisibility(View.VISIBLE);

                llContent.setVisibility(View.VISIBLE);
                btnOpenOrRenew.setVisibility(View.VISIBLE);
            } else {        //非VIP
                tvVipTypeTitle.setText(null);
                tvVipTime.setText(null);
                tvVipTips.setText(null);
                tvAccountSetup.setVisibility(View.VISIBLE);
                ivIconVipSymbol.setVisibility(View.GONE);

                llContent.setVisibility(View.VISIBLE);
                btnOpenOrRenew.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 获取充值列表
     */
//    private void reqRechargeCombo() {
//        if (type == 0) {
//            RxHttp.get(Consts.ORDER_GOLD_EXCHANGE_API)
//                    .asResponseList(ReExchangeSetEntity.class)
//                    .to(RxLife.toMain(this))
//                    .subscribe(datas -> mAdapter.setList(datas));
//        } else {
//            RxHttp.get(Consts.ORDER_POINT_EXCHANGE_API)
//                    .asResponseList(ReExchangeSetEntity.class)
//                    .to(RxLife.toMain(this))
//                    .subscribe(datas -> mAdapter.setList(datas));
//        }
//    }

    /**
     * 提交订单
     */
    private void reqSubmitOrder(String comboId) {
        if (type == 0 ) {//|| type == 1
            RxHttp.postForm(Consts.ORDER_ESCROW_CREATE_API)
                    .add(Consts.VIP_COMBO_ID, comboId)
                    .asResponse(PaymentInfoEntity.class)
                    .doOnSubscribe(disposable -> showLoadingDialog())
                    .doFinally(this::dismissLoadingDialog)
                    .to(RxLife.toMain(this))
                    .subscribe(data -> {
//                        PaymentSelectDialog.newInstance(data)
//                                .setLayoutId(R.layout.dialog_payment_select2)
//                                .setConvertListener(viewConvertListener2)
//                                .setDimAmout(0.5f)
//                                .setShowBottom(true)
//                                .setAnimStyle(R.style.PaymentDialogAnim)
//                                .show(getSupportFragmentManager());
                        RechargeOrderActivity.start(this, data, type);
                    }, (OnError) error -> TipDialog.show(GoldExchangeVipActivity.this, error.getErrorMsg(),
                            TipDialog.TYPE.ERROR));
        }
//        else if (type == 1) {
//            RxHttp.postForm(Consts.ORDER_ESCROW_CREATE_API)
//                    .add(Consts.VIP_COMBO_ID, comboId)
//                    .asResponse(PaymentInfoEntity.class)
//                    .doOnSubscribe(disposable -> showLoadingDialog())
//                    .doFinally(this::dismissLoadingDialog)
//                    .to(RxLife.toMain(this))
//                    .subscribe(data -> {
//                        PaymentSelectDialog.newInstance(data)
//                                .setLayoutId(R.layout.dialog_payment_select2)
//                                .setConvertListener(viewConvertListener2)
//                                .setDimAmout(0.5f)
//                                .setShowBottom(true)
//                                .setAnimStyle(R.style.PaymentDialogAnim)
//                                .show(getSupportFragmentManager());
//                        RehargeOrderActivity.start(this, data, type);
//                    }, (OnError) error -> TipDialog.show(GoldExchangeVipActivity.this, error.getErrorMsg(),
//                            TipDialog.TYPE.ERROR));
//        }
        else {
            RxHttp.postForm(Consts.ORDER_EXCHANGE_CREATE_API)
                    .add(Consts.VIP_COMBO_ID, comboId)
                    .asResponse(PaymentInfoEntity.class)
                    .doOnSubscribe(disposable -> showLoadingDialog())
                    .doFinally(this::dismissLoadingDialog)
                    .to(RxLife.toMain(this))
                    .subscribe(data -> {
                        PaymentSelectDialog.newInstance(data)
                                .setLayoutId(R.layout.dialog_payment_select)
                                .setConvertListener(viewConvertListener)
                                .setDimAmout(0.5f)
                                .setShowBottom(true)
                                .setAnimStyle(R.style.PaymentDialogAnim)
                                .show(getSupportFragmentManager());
                    }, (OnError) error -> TipDialog.show(GoldExchangeVipActivity.this, error.getErrorMsg(),
                            TipDialog.TYPE.ERROR));
        }
    }

    private PaymentSelectDialog.PConvertListener viewConvertListener = (holder, data, dialog) -> {
        double paymentMoney = data.getPayment_money();
        holder.setText(R.id.tv_content_payment, data.getBody());
        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.CHINA);
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
        CheckBoxSample cb1 = holder.getView(R.id.checkbox_1);
        CheckBoxSample cb2 = holder.getView(R.id.checkbox_2);

        RelativeLayout rtl1 = holder.getView(R.id.rtl_asBtn_payment_pay_1);
        RelativeLayout rtl2 = holder.getView(R.id.rtl_asBtn_payment_pay_2);

        rtl1.setVisibility(View.GONE);
        rtl2.setVisibility(View.GONE);

        switch (type) {
            case 1:
                holder.setText(R.id.tv_need2pay_money_payment, String.format(getString(R.string.txt_need_2_pay), paymentMoney + "金币"));
                rtl1.setVisibility(View.VISIBLE);
                cb1.setChecked(true);
                cb2.setChecked(false);
                break;
            case 2:
                holder.setText(R.id.tv_need2pay_money_payment, String.format(getString(R.string.txt_need_2_pay), paymentMoney + "积分"));
                rtl2.setVisibility(View.VISIBLE);
                cb1.setChecked(false);
                cb2.setChecked(true);
                break;
        }
        Button btnConfirm = holder.getView(R.id.btn_confirm_2_pay);
        btnConfirm.setOnClickListener(new OnClickEvent() {
            @Override
            public void singleClick(View v) {
                if (cb1.isChecked()) {
                    // 金币支付
                    pay(data.getOrder_sn());
                    dialog.dismiss();
                } else if (cb2.isChecked()) {
                    // 积分支付
                    pay(data.getOrder_sn());
                    dialog.dismiss();
                } else {
                    Tos.showShort(GoldExchangeVipActivity.this, R.string.txt_plz_select_payment_way);
                }
            }
        });
    };

    private PaymentSelectDialog.PConvertListener viewConvertListener2 = (holder, data, dialog) -> {
        double paymentMoney = data.getPayment_money();
        holder.setText(R.id.tv_content_payment, data.getBody());
        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.CHINA);
        nf.setMaximumFractionDigits(2);

        CheckBoxSample cb1 = holder.getView(R.id.checkbox_1);
        CheckBoxSample cb2 = holder.getView(R.id.checkbox_2);
        CheckBoxSample cb3 = holder.getView(R.id.checkbox_3);
        CheckBoxSample cb4 = holder.getView(R.id.checkbox_4);
        CheckBoxSample cb5 = holder.getView(R.id.checkbox_5);
        CheckBoxSample cb6 = holder.getView(R.id.checkbox_6);
        CheckBoxSample cb7 = holder.getView(R.id.checkbox_7);
        CheckBoxSample cb8 = holder.getView(R.id.checkbox_8);
        RelativeLayout rtl1 = holder.getView(R.id.rtl_asBtn_payment_pay_1);
        RelativeLayout rtl2 = holder.getView(R.id.rtl_asBtn_payment_pay_2);
        RelativeLayout rtl3 = holder.getView(R.id.rtl_asBtn_payment_pay_3);
        RelativeLayout rtl4 = holder.getView(R.id.rtl_asBtn_payment_pay_4);
        RelativeLayout rtl5 = holder.getView(R.id.rtl_asBtn_payment_pay_5);
        RelativeLayout rtl6 = holder.getView(R.id.rtl_asBtn_payment_pay_6);
        RelativeLayout rtl7 = holder.getView(R.id.rtl_asBtn_payment_pay_7);
        RelativeLayout rtl8 = holder.getView(R.id.rtl_asBtn_payment_pay_8);
        rtl1.setVisibility(View.GONE);
        rtl2.setVisibility(View.GONE);
        rtl3.setVisibility(View.GONE);
        rtl4.setVisibility(View.GONE);
        rtl5.setVisibility(View.GONE);
        rtl6.setVisibility(View.GONE);
        rtl7.setVisibility(View.GONE);
        rtl8.setVisibility(View.GONE);
        holder.setText(R.id.tv_need2pay_money_payment, String.format(getString(R.string.txt_need_2_pay), nf.format(paymentMoney)));
        String selectedAlipay = VipCache.getAlipay(GoldExchangeVipActivity.this, type);
        String selectedWeixin = VipCache.getWeixin(GoldExchangeVipActivity.this, type);
        String[] ali = selectedAlipay.split(",");
        String[] weichat = selectedWeixin.split(",");
        switch (ali.length) {
            case 1:
                rtl1.setVisibility(View.VISIBLE);
                break;
            case 2:
                rtl1.setVisibility(View.VISIBLE);
                rtl2.setVisibility(View.VISIBLE);
                break;
            case 3:
                rtl1.setVisibility(View.VISIBLE);
                rtl2.setVisibility(View.VISIBLE);
                rtl3.setVisibility(View.VISIBLE);
                break;
            case 4:
                rtl1.setVisibility(View.VISIBLE);
                rtl2.setVisibility(View.VISIBLE);
                rtl3.setVisibility(View.VISIBLE);
                rtl4.setVisibility(View.VISIBLE);
                break;
        }
        switch (weichat.length) {
            case 1:
                rtl5.setVisibility(View.VISIBLE);
                break;
            case 2:
                rtl5.setVisibility(View.VISIBLE);
                rtl6.setVisibility(View.VISIBLE);
                break;
            case 3:
                rtl5.setVisibility(View.VISIBLE);
                rtl6.setVisibility(View.VISIBLE);
                rtl7.setVisibility(View.VISIBLE);
                break;
            case 4:
                rtl5.setVisibility(View.VISIBLE);
                rtl6.setVisibility(View.VISIBLE);
                rtl7.setVisibility(View.VISIBLE);
                rtl8.setVisibility(View.VISIBLE);
                break;
        }
        rtl1.setOnClickListener(v -> {
            //支付宝
            payType = 1;
            platform = ali[0];
            cb1.setChecked(true);
            cb2.setChecked(false);
            cb3.setChecked(false);
            cb4.setChecked(false);
            cb5.setChecked(false);
            cb6.setChecked(false);
            cb7.setChecked(false);
            cb8.setChecked(false);
        });
        rtl2.setOnClickListener(v -> {
            //支付宝
            payType = 1;
            platform = ali[1];
            cb1.setChecked(false);
            cb2.setChecked(true);
            cb3.setChecked(false);
            cb4.setChecked(false);
            cb5.setChecked(false);
            cb6.setChecked(false);
            cb7.setChecked(false);
            cb8.setChecked(false);
        });
        rtl3.setOnClickListener(v -> {
            //支付宝
            payType = 1;
            platform = ali[2];
            cb1.setChecked(false);
            cb2.setChecked(false);
            cb3.setChecked(true);
            cb4.setChecked(false);
            cb5.setChecked(false);
            cb6.setChecked(false);
            cb7.setChecked(false);
            cb8.setChecked(false);
        });
        rtl4.setOnClickListener(v -> {
            //支付宝
            payType = 1;
            platform = ali[3];
            cb1.setChecked(false);
            cb2.setChecked(false);
            cb3.setChecked(false);
            cb4.setChecked(true);
            cb5.setChecked(false);
            cb6.setChecked(false);
            cb7.setChecked(false);
            cb8.setChecked(false);
        });
        rtl5.setOnClickListener(v -> {
            //微信
            payType = 2;
            platform = weichat[0];
            cb1.setChecked(false);
            cb2.setChecked(false);
            cb3.setChecked(false);
            cb4.setChecked(false);
            cb5.setChecked(true);
            cb6.setChecked(false);
            cb7.setChecked(false);
            cb8.setChecked(false);
        });
        rtl6.setOnClickListener(v -> {
            //微信
            payType = 2;
            platform = weichat[1];
            cb1.setChecked(false);
            cb2.setChecked(false);
            cb3.setChecked(false);
            cb4.setChecked(false);
            cb5.setChecked(false);
            cb6.setChecked(true);
            cb7.setChecked(false);
            cb8.setChecked(false);
        });
        rtl7.setOnClickListener(v -> {
            //微信
            payType = 2;
            platform = weichat[2];
            cb1.setChecked(false);
            cb2.setChecked(false);
            cb3.setChecked(false);
            cb4.setChecked(false);
            cb5.setChecked(false);
            cb6.setChecked(false);
            cb7.setChecked(true);
            cb8.setChecked(false);
        });
        rtl8.setOnClickListener(v -> {
            //微信
            payType = 2;
            platform = weichat[3];
            cb1.setChecked(false);
            cb2.setChecked(false);
            cb3.setChecked(false);
            cb4.setChecked(false);
            cb5.setChecked(false);
            cb6.setChecked(false);
            cb7.setChecked(false);
            cb8.setChecked(true);
        });
        Button btnConfirm = holder.getView(R.id.btn_confirm_2_pay);
        btnConfirm.setOnClickListener(new OnClickEvent() {
            @Override
            public void singleClick(View v) {
                if (cb1.isChecked() || cb2.isChecked() || cb3.isChecked() || cb4.isChecked() || cb5.isChecked() || cb6.isChecked() || cb7.isChecked() || cb8.isChecked()) {
                    SpannableStringBuilder info = new SpanUtils(GoldExchangeVipActivity.this)
                            .append("①请在订单匹配成功后90秒内完成支付；")
                            .appendLine("②支付失败请重复尝试充值就可以解决；")
                            .create();
                    MessageDialog.show(GoldExchangeVipActivity.this, "温馨提示", info.toString(), "知道了")
                            .setCancelable(false)
                            .setOnOkButtonClickListener((baseDialog, v1) -> {
                                pay(data.getOrder_sn());
                                baseDialog.doDismiss();
                                return false;
                            });
                    dialog.dismiss();
                } else {
                    Tos.showShort(GoldExchangeVipActivity.this, R.string.txt_plz_select_payment_way);
                }
            }
        });
    };

    private void pay(String order_sn) {
        if (type == 1) {
            RxHttp.postForm(Consts.PAY_GOLD_API)
                    .add("order_sn", order_sn)
                    .asResponse(String.class)
                    .doOnSubscribe(disposable -> showLoadingDialog())
                    .doFinally(this::dismissLoadingDialog)
                    .to(RxLife.toMain(this))
                    .subscribe(data -> {
                        reqUserInfo();
                    }, (OnError) error -> TipDialog.show(GoldExchangeVipActivity.this, error.getErrorMsg(),
                            TipDialog.TYPE.ERROR));

        } else if (type == 2) {
            RxHttp.postForm(Consts.PAY_POINT_API)
                    .add("order_sn", order_sn)
                    .asResponse(String.class)
                    .doOnSubscribe(disposable -> showLoadingDialog())
                    .doFinally(this::dismissLoadingDialog)
                    .to(RxLife.toMain(this))
                    .subscribe(data -> {
                        reqUserInfo();
                    }, (OnError) error -> TipDialog.show(GoldExchangeVipActivity.this, error.getErrorMsg(),
                            TipDialog.TYPE.ERROR));
        } else {
            switch (payType) {
                case 1:
                    RxHttp.postForm(Consts.PAY_ESCROW_TEST)
                            .add("order_sn", order_sn)
                            .add("platform", "test")
                            .add("ip", NetworkUtil.getLocalIpAddress(GoldExchangeVipActivity.this))
                            .asResponse(WePay.class)
                            .doOnSubscribe(disposable -> showLoadingDialog())
                            .doFinally(this::dismissLoadingDialog)
                            .to(RxLife.toMain(this))
                            .subscribe(data -> {
                                jumpInSide("https://www.baidu.com/", order_sn);
                            }, (OnError) error -> TipDialog.show(GoldExchangeVipActivity.this, error.getErrorMsg(),
                                    TipDialog.TYPE.ERROR));
//                    RxHttp.postForm(Consts.PAY_ESCROW_ALI_API_1)
//                            .add("order_sn", order_sn)
//                            .add("platform", platform)
//                            .add("ip", NetworkUtil.getLocalIpAddress(GoldExchangeVipActivity.this))
//                            .asResponse(WePay.class)
//                            .doOnSubscribe(disposable -> showLoadingDialog())
//                            .doFinally(this::dismissLoadingDialog)
//                            .to(RxLife.toMain(this))
//                            .subscribe(data -> {
//                                jumpInSide(data.getUrl(), order_sn);
//                            }, (OnError) error -> TipDialog.show(GoldExchangeVipActivity.this, error.getErrorMsg(),
//                                    TipDialog.TYPE.ERROR));
                    break;
                case 2:
                    RxHttp.postForm(Consts.PAY_ESCROW_TEST)
                            .add("order_sn", order_sn)
                            .add("platform", "test")
                            .add("ip", NetworkUtil.getLocalIpAddress(GoldExchangeVipActivity.this))
                            .asResponse(WePay.class)
                            .doOnSubscribe(disposable -> showLoadingDialog())
                            .doFinally(this::dismissLoadingDialog)
                            .to(RxLife.toMain(this))
                            .subscribe(data -> {
                                jumpInSide("https://www.baidu.com/", order_sn);
                            }, (OnError) error -> TipDialog.show(GoldExchangeVipActivity.this, error.getErrorMsg(),
                                    TipDialog.TYPE.ERROR));
//                    RxHttp.postForm(Consts.PAY_ESCROW_WX_API_1)
//                            .add("order_sn", order_sn)
//                            .add("platform", platform)
//                            .add("ip", NetworkUtil.getLocalIpAddress(GoldExchangeVipActivity.this))
//                            .asResponse(WePay.class)
//                            .doOnSubscribe(disposable -> showLoadingDialog())
//                            .doFinally(this::dismissLoadingDialog)
//                            .to(RxLife.toMain(this))
//                            .subscribe(data -> {
//                                jumpInSide(data.getUrl(), order_sn);
//                            }, (OnError) error -> TipDialog.show(GoldExchangeVipActivity.this, error.getErrorMsg(),
//                                    TipDialog.TYPE.ERROR));
                    break;
                default:
                    Toast.makeText(this, "请选择支付方式！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void jumpInSide(String link, String payOrderId) {
        Intent intent = new Intent(GoldExchangeVipActivity.this, CommonWebActivity.class);
        intent.putExtra(Common.KEY_URL, link);
        intent.putExtra(Common.KEY_PAY_ID, payOrderId);
        intent.putExtra(Common.KEY_PAY, true);
        startActivity(intent);
    }

    private void jumpOutSide(String link) {
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(link));
        // 注意此处的判断intent.resolveActivity()可以返回显示该Intent的Activity对应的组件名
        // 官方解释 : Name of the component implementing an activity that can display the intent
        if (intent.resolveActivity(getPackageManager()) != null) {
            final ComponentName componentName = intent.resolveActivity(getPackageManager());
            startActivity(Intent.createChooser(intent, "请选择浏览器"));
        } else {
            TipDialog.show(this, "链接错误或无浏览器", TipDialog.TYPE.ERROR);
        }
    }

    /**
     * 获取用户信息---刷新会员时间
     */
    private void reqUserInfo() {
        String uniqueID = UniqueIdManager.getUniqueID(this);
        RxHttp.postForm(Consts.USERS_INFO_API)
                .add(Consts.UNIQUE_ID, uniqueID)
                .asResponse(FUser.class)
                .to(RxLife.toMain(this))
                .subscribe(fUser -> {
                    UserInfoCache.saveUserInfo(GoldExchangeVipActivity.this, fUser);
                    if (UserInfoCache.getSuperVip(GoldExchangeVipActivity.this) == 1) {
                        tvVipTypeTitle.setText(R.string.txt_honor_vip);
                        tvVipTime.setText(String.format(getString(R.string.txt_vip_end_time_colon), getString(R.string.txt_permanent_validity)));
                        tvVipTips.setText(R.string.txt_tips_vip_state_life_member);
                        tvAccountSetup.setVisibility(View.GONE);
                        ivIconVipSymbol.setVisibility(View.VISIBLE);

                        llContent.setVisibility(View.GONE);
                        btnOpenOrRenew.setVisibility(View.GONE);

                        EventBus.getDefault().postSticky(new VipChargerEvent(true));
                        EventBus.getDefault().postSticky(fUser);
                        MessageDialog.build(GoldExchangeVipActivity.this)
                                .setTitle(R.string.txt_payment_success)
                                .setMessage(R.string.txt_go2_experience_it)
                                .setOkButton(R.string.txt_experience_it)
                                .setCancelButton(R.string.txt_look_a_little_bit_more)
                                .setCustomView(R.layout.dialog_payment_success, (dialog, v) -> {
                                }).setOnOkButtonClickListener((baseDialog, v) -> {
                            finish();
                            return false;
                        }).show();
                    } else if (UserInfoCache.getIsVip(GoldExchangeVipActivity.this)) {
                        tvVipTypeTitle.setText(R.string.txt_monthly_vip);
                        tvVipTime.setText(String.format(getString(R.string.txt_vip_end_time_colon),
                                DateTimeUtil.formatDateTime(fUser.getVip_end() * 1000, "yyyy-MM-dd")));
                        tvVipTips.setText(R.string.txt_tips_vip_state);
                        tvAccountSetup.setVisibility(View.GONE);
                        ivIconVipSymbol.setVisibility(View.VISIBLE);

                        EventBus.getDefault().postSticky(new VipChargerEvent(true));
                        EventBus.getDefault().postSticky(fUser);
                        MessageDialog.build(GoldExchangeVipActivity.this)
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
                });
    }

    public void queryPayStatu(String orderId) {
        StringBuilder info = new StringBuilder();
        info.append("正在查询订单！").append("\n");
        info.append("点击空白处取消等待").append("\n");
        WaitDialog.show(GoldExchangeVipActivity.this, info.toString()).setCancelable(true).setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                mHandler.removeMessages(10001);
            }
        });
        RxHttp.postForm(Consts.PAY_QUERY_API)
                .add("other_order_sn", orderId)
                .add("platform", platform)
                .asResponse(Integer.class)
                .to(RxLife.toMain(this))
                .subscribe(code -> {
                    switch (code) {
                        case 0:
                            if (queryTimes < 10) {
                                Message msg = new Message();
                                msg.what = 10001;
                                mHandler.sendMessageDelayed(msg, 3000);
                                queryTimes++;
                            } else {
                                mHandler.removeMessages(10001);
                                WaitDialog.dismiss();
//                                TipDialog.show(GoldExchangeVipActivity.this, "支付失败，请重新尝试。", TipDialog.TYPE.ERROR);
                                queryTimes = 0;
                                MessageDialog.show(GoldExchangeVipActivity.this, "订单未生效", "如您已经支付成功，可以选择继续等待或者查看帮助。", "继续等待", "查看帮助")
                                        .setCancelable(true)
                                        .setOnOkButtonClickListener((baseDialog, v1) -> {
                                            queryPayStatu(orderId);
                                            baseDialog.doDismiss();
                                            return false;
                                        })
                                        .setOnCancelButtonClickListener(new OnDialogButtonClickListener() {
                                            @Override
                                            public boolean onClick(BaseDialog baseDialog, View v) {
                                                Intent intent = new Intent(GoldExchangeVipActivity.this, CommonWebActivity.class);
                                                intent.putExtra(Common.KEY_URL, Consts.PRIVILEGE_EXPLAIN_URL);
                                                startActivityForResult(intent, 1000);
                                                return false;
                                            }
                                        });
                            }
                            break;
                        case 1:
                            if (type == 0) {
                                reqUserInfo();
                            }
//                            else if (type == 1) {
//                                reqMyCapitalDetail();
//                            }
                            WaitDialog.dismiss();
                            mHandler.removeMessages(10001);
                            queryTimes = 0;
                            break;
                        case 2:
                            WaitDialog.dismiss();
                            mHandler.removeMessages(10001);
                            TipDialog.show(GoldExchangeVipActivity.this, "支付失败，请重新尝试。", TipDialog.TYPE.ERROR);
                            queryTimes = 0;
                            break;
                    }
                }, (OnError) error -> {
                    if (queryTimes < 10) {
                        Message msg = new Message();
                        msg.what = 10001;
                        mHandler.sendMessageDelayed(msg, 3000);
                        queryTimes++;
                    } else {
                        mHandler.removeMessages(10001);
                        WaitDialog.dismiss();
                        TipDialog.show(GoldExchangeVipActivity.this, "支付失败，请重新尝试。", TipDialog.TYPE.ERROR);
                        queryTimes = 0;
                    }
                });
    }

    /**
     * 我的资金详情
     */
    public void reqMyCapitalDetail() {
        RxHttp.get(Consts.USER_CAPITAL_API)
                .asResponse(CapitalEntity.class)
                .to(RxLife.toMain(this))  //感知生命周期，并在主线程回调
                .subscribe(data -> {
                    MessageDialog.build(GoldExchangeVipActivity.this)
                            .setTitle(R.string.txt_payment_success)
                            .setMessage(R.string.txt_go2_experience_it)
                            .setOkButton(R.string.txt_experience_it)
                            .setCancelButton(R.string.txt_look_a_little_bit_more)
                            .setCustomView(R.layout.dialog_payment_success, (dialog, v) -> {
                            }).setOnOkButtonClickListener((baseDialog, v) -> {
                        finish();
                        return false;
                    }).show();
                    EventBus.getDefault().postSticky(data);
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            MessageDialog.show(GoldExchangeVipActivity.this, "订单未生效", "如您已经支付成功，可以选择继续等待或者查看帮助。", "继续等待", "查看帮助")
                .setCancelable(true)
                .setOnOkButtonClickListener((baseDialog, v1) -> {
                    queryPayStatu(orderId);
                    baseDialog.doDismiss();
                    return false;
                })
                .setOnCancelButtonClickListener(new OnDialogButtonClickListener() {
                    @Override
                    public boolean onClick(BaseDialog baseDialog, View v) {
                        Intent intent = new Intent(GoldExchangeVipActivity.this, CommonWebActivity.class);
                        intent.putExtra(Common.KEY_URL, Consts.PRIVILEGE_EXPLAIN_URL);
                        startActivityForResult(intent, 1000);
                        return false;
                    }
                });
        }
    }

    private String orderId;
    private int queryTimes;

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onWebPayFinishEvent(WebPayEvent event) {
        orderId = event.getCode();
        if (TextUtils.isEmpty(orderId)) {
            MessageDialog.build(GoldExchangeVipActivity.this)
                    .setTitle(R.string.txt_cdkey_success)
                    .setMessage(R.string.txt_go2_experience_cdkey)
                    .setOkButton(R.string.txt_experience_cdkey)
                    .setCancelButton(R.string.txt_next_time_exchange)
                    .setCustomView(R.layout.dialog_payment_success, (dialog, v) -> {
                    }).setOnOkButtonClickListener((baseDialog, v) -> {
                CDKEYActivity.start(GoldExchangeVipActivity.this);
                finish();
                return false;
            }).show();
        } else {
            queryPayStatu(orderId);
        }
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 10001) {
                queryPayStatu(orderId);
            }
        }
    };


    @Override
    public void initParms(Bundle parms) {
        type = parms.getInt(TYPE, 0);
    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VipCache.clear(GoldExchangeVipActivity.this);
    }
}