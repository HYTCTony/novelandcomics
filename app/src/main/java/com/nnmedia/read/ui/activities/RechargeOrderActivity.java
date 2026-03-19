package com.nnmedia.read.ui.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kongzue.dialog.v3.MessageDialog;
import com.kongzue.dialog.v3.TipDialog;
import com.nnmedia.novel.R;
import com.nnmedia.novel.RxHttp;
import com.nnmedia.page.utils.SpanUtils;
import com.nnmedia.read.cache.VipCache;
import com.nnmedia.read.contact.Common;
import com.nnmedia.read.contact.Consts;
import com.nnmedia.read.ebsevent.WebPayEvent;
import com.nnmedia.read.entity.PaymentInfoEntity;
import com.nnmedia.read.entity.pay.WePay;
import com.nnmedia.read.rxhttp.OnError;
import com.nnmedia.read.ui.base.BaseActivity;
import com.nnmedia.read.ui.widget.CheckBoxSample;
import com.nnmedia.read.ui.widget.NumberAnimTextView;
import com.nnmedia.read.utils.NetworkUtil;
import com.nnmedia.read.utils.Tos;
import com.orhanobut.logger.Logger;
import com.rxjava.rxlife.RxLife;

import org.greenrobot.eventbus.EventBus;

import java.text.NumberFormat;
import java.util.Locale;

import androidx.appcompat.widget.Toolbar;

public class RechargeOrderActivity extends BaseActivity implements View.OnClickListener {

    private static final String ARGUMENTS_KEY = "key_pay_info";
    private static final String TYPE_KEY = "pay_type";
    private PaymentInfoEntity data;
    private int type;
    private int payType;
    private String platform;
    private String[] ali;
    private String[] weichat;

    private String orderId;
    private int queryTimes;

    private NumberFormat nf;

    private double paymentMoney;
    private double needPay;

    private NumberAnimTextView tvTitle;
    private TextView tvNeedMoney;
    private TextView tvOriginalPrice;
    private TextView tvPointAdded;
    private TextView tvDescribe;
    private CheckBoxSample cb0;
    private CheckBoxSample cb1;
    private CheckBoxSample cb2;
    private CheckBoxSample cb3;
    private CheckBoxSample cb4;
    private CheckBoxSample cb5;
    private CheckBoxSample cb6;
    private CheckBoxSample cb7;
    private CheckBoxSample cb8;
    private LinearLayout llCDKEY;
    private RelativeLayout rtl0;
    private RelativeLayout rtl1;
    private RelativeLayout rtl2;
    private RelativeLayout rtl3;
    private RelativeLayout rtl4;
    private RelativeLayout rtl5;
    private RelativeLayout rtl6;
    private RelativeLayout rtl7;
    private RelativeLayout rtl8;
    private RelativeLayout rtlChooseCardPay;
    private Button btnConfirm;
    private TextView btnOpenPayType;
    private TextView tvReductionPrice;

    public static void start(Context context, PaymentInfoEntity data, int type) {
        Intent starter = new Intent(context, RechargeOrderActivity.class);
        starter.putExtra(ARGUMENTS_KEY, data);
        starter.putExtra(TYPE_KEY, type);
        context.startActivity(starter);
    }

    @Override
    public void initParms(Bundle parms) {
        data = (PaymentInfoEntity) parms.getSerializable(ARGUMENTS_KEY);
        type = parms.getInt(TYPE_KEY);
    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_recharge_order;
    }

    @Override
    public void initView(View view) {
        Toolbar toolbar = $(R.id.toolbar_normal);
        initToolBar(toolbar, R.string.txt_order);

        tvTitle = $(R.id.tv_content_payment);
        tvNeedMoney = $(R.id.tv_need2pay_money_payment);
        tvOriginalPrice = $(R.id.tv_original_price);
        tvPointAdded = $(R.id.tv_cdkey_added);
        tvDescribe = $(R.id.tv_describe);

        cb0 = $(R.id.checkbox_0);
        cb1 = $(R.id.checkbox_1);
        cb2 = $(R.id.checkbox_2);
        cb3 = $(R.id.checkbox_3);
        cb4 = $(R.id.checkbox_4);
        cb5 = $(R.id.checkbox_5);
        cb6 = $(R.id.checkbox_6);
        cb7 = $(R.id.checkbox_7);
        cb8 = $(R.id.checkbox_8);
        llCDKEY = $(R.id.ll_asBtn_payment_pay_0);
        rtl0 = $(R.id.rtl_asBtn_payment_pay_0);
        rtl1 = $(R.id.rtl_asBtn_payment_pay_1);
        rtl2 = $(R.id.rtl_asBtn_payment_pay_2);
        rtl3 = $(R.id.rtl_asBtn_payment_pay_3);
        rtl4 = $(R.id.rtl_asBtn_payment_pay_4);
        rtl5 = $(R.id.rtl_asBtn_payment_pay_5);
        rtl6 = $(R.id.rtl_asBtn_payment_pay_6);
        rtl7 = $(R.id.rtl_asBtn_payment_pay_7);
        rtl8 = $(R.id.rtl_asBtn_payment_pay_8);
        rtlChooseCardPay = $(R.id.rtl_choose_card_pay);
        btnOpenPayType = $(R.id.btn_open_pay_type);
        btnConfirm = $(R.id.btn_confirm_2_pay);
        tvReductionPrice = $(R.id.tv_reduction_price);
    }

    @Override
    public void setListener() {
        rtl0.setOnClickListener(this);
        rtl1.setOnClickListener(this);
        rtl2.setOnClickListener(this);
        rtl3.setOnClickListener(this);
        rtl4.setOnClickListener(this);
        rtl5.setOnClickListener(this);
        rtl6.setOnClickListener(this);
        rtl7.setOnClickListener(this);
        rtl8.setOnClickListener(this);
        rtlChooseCardPay.setOnClickListener(this);
        btnOpenPayType.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {
        if (data.getType() == 0) {
            tvTitle.setText(data.getBody());
        } else {
            tvTitle.setPrefixString("购买");
            tvTitle.setPostfixString("阅读点");
            tvTitle.setNumberString("" + (data.getHpoint() + data.getAdded()));
        }

        paymentMoney = data.getPayment_money();
        nf = NumberFormat.getCurrencyInstance(Locale.CHINA);
        nf.setMaximumFractionDigits(2);
        needPay = paymentMoney * 0.98;
        tvNeedMoney.setText("¥" + paymentMoney);
        SpannableString spaOriginalPrice = new SpannableString(nf.format(paymentMoney));
        spaOriginalPrice.setSpan(new RelativeSizeSpan(0.7f), 0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spaOriginalPrice.setSpan(new StrikethroughSpan(), 0, spaOriginalPrice.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        tvOriginalPrice.setText(spaOriginalPrice);
        double reduction = paymentMoney - needPay;
        if (data.getAdded() > 0) {
            tvReductionPrice.setText(String.format(getString(R.string.txt_added_hpoint), data.getAdded() + "阅读点"));
            tvPointAdded.setText(String.format(getString(R.string.txt_user_cdkey), data.getAdded() + "阅读点"));
            tvDescribe.setText(String.format(getString(R.string.txt_describe_cdkey), "" + data.getHpoint(), data.getAdded() + "阅读点"));
            rtlChooseCardPay.setVisibility(View.VISIBLE);
            tvPointAdded.setVisibility(View.VISIBLE);
            tvDescribe.setVisibility(View.VISIBLE);
        } else {
            rtlChooseCardPay.setVisibility(View.GONE);
            tvPointAdded.setVisibility(View.GONE);
            tvDescribe.setVisibility(View.INVISIBLE);
        }

        int selectCDKEY = VipCache.getCDKEY(mContext, type);

        String selectedAlipay = VipCache.getAlipay(mContext, type);
        String selectedWeixin = VipCache.getWeixin(mContext, type);
        Logger.d(selectedAlipay);
        Logger.d(selectedWeixin);
        ali = selectedAlipay.split(",");
        weichat = selectedWeixin.split(",");


        rtlChooseCardPay.setVisibility(View.INVISIBLE);

        if (selectCDKEY == 1) {
            llCDKEY.setVisibility(View.VISIBLE);
        } else {
            llCDKEY.setVisibility(View.GONE);
        }
        if (TextUtils.isEmpty(selectedAlipay)) {
            rtl1.setVisibility(View.GONE);
            rtl2.setVisibility(View.GONE);
            rtl3.setVisibility(View.GONE);
            rtl4.setVisibility(View.GONE);
        } else {
            rtl1.setVisibility(View.VISIBLE);
        }
        if (TextUtils.isEmpty(selectedWeixin)) {
            rtl5.setVisibility(View.GONE);
            rtl6.setVisibility(View.GONE);
            rtl7.setVisibility(View.GONE);
            rtl8.setVisibility(View.GONE);
        } else {
            rtl5.setVisibility(View.VISIBLE);
        }
        if (ali.length <= 1 && weichat.length <= 1) {
            btnOpenPayType.setVisibility(View.GONE);
        }
        cb0.setChecked(true);
    }

    private void setPayView() {
        btnOpenPayType.setVisibility(View.GONE);
        switch (ali.length) {
            case 1:
                if (!ali[0].isEmpty())
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
                if (!weichat[0].isEmpty())
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rtl_asBtn_payment_pay_0:
            case R.id.rtl_choose_card_pay:
                if (data.getType() == 1) {
                    tvTitle.setNumberString("" + data.getHpoint(), "" + (data.getHpoint() + data.getAdded()));
                    tvDescribe.setVisibility(View.VISIBLE);
                    tvOriginalPrice.setVisibility(View.GONE);
                    rtlChooseCardPay.setVisibility(View.INVISIBLE);
                }
                //卡密
                payType = 0;
                platform = "";
                cb0.setChecked(true);
                cb1.setChecked(false);
                cb2.setChecked(false);
                cb3.setChecked(false);
                cb4.setChecked(false);
                cb5.setChecked(false);
                cb6.setChecked(false);
                cb7.setChecked(false);
                cb8.setChecked(false);
                break;
            case R.id.rtl_asBtn_payment_pay_1:
                if (data.getType() == 1) {
                    tvTitle.setNumberString("" + data.getHpoint(), "" + data.getHpoint());
                    tvDescribe.setVisibility(View.INVISIBLE);
                    tvOriginalPrice.setVisibility(View.GONE);
                    rtlChooseCardPay.setVisibility(View.VISIBLE);
                }
                //支付宝
                payType = 1;
                platform = ali[0];
                cb0.setChecked(false);
                cb1.setChecked(true);
                cb2.setChecked(false);
                cb3.setChecked(false);
                cb4.setChecked(false);
                cb5.setChecked(false);
                cb6.setChecked(false);
                cb7.setChecked(false);
                cb8.setChecked(false);
                break;
            case R.id.rtl_asBtn_payment_pay_2:
                if (data.getType() == 1) {
                    tvTitle.setNumberString("" + data.getHpoint(), "" + data.getHpoint());
                    tvDescribe.setVisibility(View.INVISIBLE);
                    tvOriginalPrice.setVisibility(View.GONE);
                    rtlChooseCardPay.setVisibility(View.VISIBLE);
                }
                //支付宝
                payType = 1;
                platform = ali[1];
                cb0.setChecked(false);
                cb1.setChecked(false);
                cb2.setChecked(true);
                cb3.setChecked(false);
                cb4.setChecked(false);
                cb5.setChecked(false);
                cb6.setChecked(false);
                cb7.setChecked(false);
                cb8.setChecked(false);
                break;
            case R.id.rtl_asBtn_payment_pay_3:
                if (data.getType() == 1) {
                    tvTitle.setNumberString("" + data.getHpoint(), "" + data.getHpoint());
                    tvDescribe.setVisibility(View.INVISIBLE);
                    tvOriginalPrice.setVisibility(View.GONE);
                    rtlChooseCardPay.setVisibility(View.VISIBLE);
                }
                //支付宝
                payType = 1;
                platform = ali[2];
                cb0.setChecked(false);
                cb1.setChecked(false);
                cb2.setChecked(false);
                cb3.setChecked(true);
                cb4.setChecked(false);
                cb5.setChecked(false);
                cb6.setChecked(false);
                cb7.setChecked(false);
                cb8.setChecked(false);
                break;
            case R.id.rtl_asBtn_payment_pay_4:
                if (data.getType() == 1) {
                    tvTitle.setNumberString("" + data.getHpoint(), "" + data.getHpoint());
                    tvDescribe.setVisibility(View.INVISIBLE);
                    tvOriginalPrice.setVisibility(View.GONE);
                    rtlChooseCardPay.setVisibility(View.VISIBLE);
                }
                //支付宝
                payType = 1;
                platform = ali[3];
                cb0.setChecked(false);
                cb1.setChecked(false);
                cb2.setChecked(false);
                cb3.setChecked(false);
                cb4.setChecked(true);
                cb5.setChecked(false);
                cb6.setChecked(false);
                cb7.setChecked(false);
                cb8.setChecked(false);
                break;
            case R.id.rtl_asBtn_payment_pay_5:
                if (data.getType() == 1) {
                    tvTitle.setNumberString("" + data.getHpoint(), "" + data.getHpoint());
                    tvDescribe.setVisibility(View.INVISIBLE);
                    tvOriginalPrice.setVisibility(View.GONE);
                    rtlChooseCardPay.setVisibility(View.VISIBLE);
                }
                //微信
                payType = 2;
                platform = weichat[0];
                cb0.setChecked(false);
                cb1.setChecked(false);
                cb2.setChecked(false);
                cb3.setChecked(false);
                cb4.setChecked(false);
                cb5.setChecked(true);
                cb6.setChecked(false);
                cb7.setChecked(false);
                cb8.setChecked(false);
                break;
            case R.id.rtl_asBtn_payment_pay_6:
                if (data.getType() == 1) {
                    tvTitle.setNumberString("" + data.getHpoint(), "" + data.getHpoint());
                    tvDescribe.setVisibility(View.INVISIBLE);
                    tvOriginalPrice.setVisibility(View.GONE);
                }
                rtlChooseCardPay.setVisibility(View.VISIBLE);
                //微信
                payType = 2;
                platform = weichat[1];
                cb0.setChecked(false);
                cb1.setChecked(false);
                cb2.setChecked(false);
                cb3.setChecked(false);
                cb4.setChecked(false);
                cb5.setChecked(false);
                cb6.setChecked(true);
                cb7.setChecked(false);
                cb8.setChecked(false);
                break;
            case R.id.rtl_asBtn_payment_pay_7:
                if (data.getType() == 1) {
                    tvTitle.setNumberString("" + data.getHpoint(), "" + data.getHpoint());
                    tvDescribe.setVisibility(View.INVISIBLE);
                    tvOriginalPrice.setVisibility(View.GONE);
                    rtlChooseCardPay.setVisibility(View.VISIBLE);
                }
                //微信
                payType = 2;
                platform = weichat[2];
                cb0.setChecked(false);
                cb1.setChecked(false);
                cb2.setChecked(false);
                cb3.setChecked(false);
                cb4.setChecked(false);
                cb5.setChecked(false);
                cb6.setChecked(false);
                cb7.setChecked(true);
                cb8.setChecked(false);
                break;
            case R.id.rtl_asBtn_payment_pay_8:
                if (data.getType() == 1) {
                    tvTitle.setNumberString("" + data.getHpoint(), "" + data.getHpoint());
                    tvDescribe.setVisibility(View.INVISIBLE);
                    tvOriginalPrice.setVisibility(View.GONE);
                    rtlChooseCardPay.setVisibility(View.VISIBLE);
                }
                //微信
                payType = 2;
                platform = weichat[3];
                cb0.setChecked(false);
                cb1.setChecked(false);
                cb2.setChecked(false);
                cb3.setChecked(false);
                cb4.setChecked(false);
                cb5.setChecked(false);
                cb6.setChecked(false);
                cb7.setChecked(false);
                cb8.setChecked(true);
                break;
            case R.id.btn_open_pay_type:
                setPayView();
                break;
            case R.id.btn_confirm_2_pay:
                if (cb1.isChecked() || cb2.isChecked() || cb3.isChecked() || cb4.isChecked() || cb5.isChecked() || cb6.isChecked() || cb7.isChecked() || cb8.isChecked()) {
                    SpannableStringBuilder info = new SpanUtils(this)
                            .append("①请在订单匹配成功后90秒内完成支付；")
                            .appendLine("②支付失败请重复尝试充值就可以解决；")
                            .create();
                    MessageDialog.show(this, "温馨提示", info.toString(), "知道了")
                            .setCancelable(false)
                            .setOnOkButtonClickListener((baseDialog, v1) -> {
                                pay(data.getOrder_sn());
                                baseDialog.doDismiss();
                                return false;
                            });
                } else if (cb0.isChecked()) {
                    pay(data.getOrder_sn());
                } else {
                    Tos.showShort(this, R.string.txt_plz_select_payment_way);
                }
                break;
        }
    }

    private void pay(String order_sn) {
        switch (payType) {
            case 0:
                RxHttp.postForm(Consts.PAY_ESCROW_CDKET)
                        .add("order_sn", order_sn)
                        .asResponse(String.class)
                        .doOnSubscribe(disposable -> showLoadingDialog())
                        .doFinally(this::dismissLoadingDialog)
                        .to(RxLife.toMain(this))
                        .subscribe(cdkey -> {
                            jumpOutSide(data.getLink());
                            finish();
                        }, (OnError) error -> TipDialog.show(this, error.getErrorMsg(),
                                TipDialog.TYPE.ERROR));
                break;
            case 1:
//                    RxHttp.postForm(Consts.PAY_ESCROW_TEST)
//                            .add("order_sn", order_sn)
//                            .add("platform", "test")
//                            .add("ip", NetworkUtil.getLocalIpAddress(GoldExchangeVipActivity.this))
//                            .asResponse(WePay.class)
//                            .doOnSubscribe(disposable -> showLoadingDialog())
//                            .doFinally(this::dismissLoadingDialog)
//                            .to(RxLife.toMain(this))
//                            .subscribe(data -> {
//                                jumpInSide("https://www.baidu.com/", order_sn);
//                            }, (OnError) error -> TipDialog.show(GoldExchangeVipActivity.this, error.getErrorMsg(),
//                                    TipDialog.TYPE.ERROR));
                RxHttp.postForm(Consts.PAY_ESCROW_ALI_API_1)
                        .add("order_sn", order_sn)
                        .add("platform", platform)
                        .add("ip", NetworkUtil.getLocalIpAddress(this))
                        .asResponse(WePay.class)
                        .doOnSubscribe(disposable -> showLoadingDialog())
                        .doFinally(this::dismissLoadingDialog)
                        .to(RxLife.toMain(this))
                        .subscribe(data -> {
                            jumpInSide(data.getUrl(), order_sn);
                            finish();
                        }, (OnError) error -> TipDialog.show(this, error.getErrorMsg(),
                                TipDialog.TYPE.ERROR));
                break;
            case 2:
//                    RxHttp.postForm(Consts.PAY_ESCROW_TEST)
//                            .add("order_sn", order_sn)
//                            .add("platform", "test")
//                            .add("ip", NetworkUtil.getLocalIpAddress(GoldExchangeVipActivity.this))
//                            .asResponse(WePay.class)
//                            .doOnSubscribe(disposable -> showLoadingDialog())
//                            .doFinally(this::dismissLoadingDialog)
//                            .to(RxLife.toMain(this))
//                            .subscribe(data -> {
//                                jumpInSide("https://www.baidu.com/", order_sn);
//                            }, (OnError) error -> TipDialog.show(GoldExchangeVipActivity.this, error.getErrorMsg(),
//                                    TipDialog.TYPE.ERROR));
                RxHttp.postForm(Consts.PAY_ESCROW_WX_API_1)
                        .add("order_sn", order_sn)
                        .add("platform", platform)
                        .add("ip", NetworkUtil.getLocalIpAddress(this))
                        .asResponse(WePay.class)
                        .doOnSubscribe(disposable -> showLoadingDialog())
                        .doFinally(this::dismissLoadingDialog)
                        .to(RxLife.toMain(this))
                        .subscribe(data -> {
                            jumpInSide(data.getUrl(), order_sn);
                            finish();
                        }, (OnError) error -> TipDialog.show(this, error.getErrorMsg(),
                                TipDialog.TYPE.ERROR));
                break;
            default:
                Toast.makeText(this, "请选择支付方式！", Toast.LENGTH_SHORT).show();
        }

    }

    private void jumpInSide(String link, String payOrderId) {
        Intent intent = new Intent(this, CommonWebActivity.class);
        intent.putExtra(Common.KEY_URL, link);
        intent.putExtra(Common.KEY_PAY_ID, payOrderId);
        intent.putExtra(Common.KEY_PAY, true);
        startActivity(intent);
    }

    private void jumpOutSide(String link) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(link));
        // 注意此处的判断intent.resolveActivity()可以返回显示该Intent的Activity对应的组件名
        // 官方解释 : Name of the component implementing an activity that can display the intent
        if (intent.resolveActivity(getPackageManager()) != null) {
            final ComponentName componentName = intent.resolveActivity(getPackageManager());
            startActivity(Intent.createChooser(intent, "请选择浏览器"));
            EventBus.getDefault().post(new WebPayEvent(""));
        } else {
            TipDialog.show(this, "链接错误或无浏览器", TipDialog.TYPE.ERROR);
        }
    }
//    public void queryPayStatu(String orderId) {
//        StringBuilder info = new StringBuilder();
//        info.append("正在查询订单！").append("\n");
//        info.append("点击空白处取消等待").append("\n");
//        WaitDialog.show(this, info.toString()).setCancelable(true).setOnDismissListener(new OnDismissListener() {
//            @Override
//            public void onDismiss() {
//                mHandler.removeMessages(10001);
//            }
//        });
//        RxHttp.postForm(Consts.PAY_QUERY_API)
//                .add("other_order_sn", orderId)
//                .add("platform", platform)
//                .asResponse(Integer.class)
//                .to(RxLife.toMain(this))
//                .subscribe(code -> {
//                    switch (code) {
//                        case 0:
//                            if (queryTimes < 10) {
//                                Message msg = new Message();
//                                msg.what = 10001;
//                                mHandler.sendMessageDelayed(msg, 3000);
//                                queryTimes++;
//                            } else {
//                                mHandler.removeMessages(10001);
//                                WaitDialog.dismiss();
////                                TipDialog.show(GoldExchangeVipActivity.this, "支付失败，请重新尝试。", TipDialog.TYPE.ERROR);
//                                queryTimes = 0;
//                                MessageDialog.show(this, "订单未生效", "如您已经支付成功，可以选择继续等待或者查看帮助。", "继续等待", "查看帮助")
//                                        .setCancelable(true)
//                                        .setOnOkButtonClickListener((baseDialog, v1) -> {
//                                            queryPayStatu(orderId);
//                                            baseDialog.doDismiss();
//                                            return false;
//                                        })
//                                        .setOnCancelButtonClickListener(new OnDialogButtonClickListener() {
//                                            @Override
//                                            public boolean onClick(BaseDialog baseDialog, View v) {
//                                                Intent intent = new Intent(RechargeOrderActivity.this, CommonWebActivity.class);
//                                                intent.putExtra(Common.KEY_URL, Consts.PRIVILEGE_EXPLAIN_URL);
//                                                startActivityForResult(intent, 1000);
//                                                return false;
//                                            }
//                                        });
//                            }
//                            break;
//                        case 1:
//                            if (type == 0) {
//                                reqUserInfo();
//                            } else if (type == 1) {
//                                reqMyCapitalDetail();
//                            }
//                            WaitDialog.dismiss();
//                            mHandler.removeMessages(10001);
//                            queryTimes = 0;
//                            break;
//                        case 2:
//                            WaitDialog.dismiss();
//                            mHandler.removeMessages(10001);
//                            TipDialog.show(this, "支付失败，请重新尝试。", TipDialog.TYPE.ERROR);
//                            queryTimes = 0;
//                            break;
//                    }
//                }, (OnError) error -> {
//                    if (queryTimes < 10) {
//                        Message msg = new Message();
//                        msg.what = 10001;
//                        mHandler.sendMessageDelayed(msg, 3000);
//                        queryTimes++;
//                    } else {
//                        mHandler.removeMessages(10001);
//                        WaitDialog.dismiss();
//                        TipDialog.show(this, "支付失败，请重新尝试。", TipDialog.TYPE.ERROR);
//                        queryTimes = 0;
//                    }
//                });
//    }

//    private void reqUserInfo() {
//        String uniqueID = UniqueIdManager.getUniqueID(this);
//        RxHttp.postForm(Consts.USERS_INFO_API)
//                .add(Consts.UNIQUE_ID, uniqueID)
//                .asResponse(FUser.class)
//                .to(RxLife.toMain(this))
//                .subscribe(fUser -> {
//                    UserInfoCache.saveUserInfo(this, fUser);
//                    if (UserInfoCache.getSuperVip(this) == 1) {
//                        EventBus.getDefault().postSticky(new VipChargerEvent(true));
//                        EventBus.getDefault().postSticky(fUser);
//                        MessageDialog.build(this)
//                                .setTitle(R.string.txt_payment_success)
//                                .setMessage(R.string.txt_go2_experience_it)
//                                .setOkButton(R.string.txt_experience_it)
//                                .setCancelButton(R.string.txt_look_a_little_bit_more)
//                                .setCustomView(R.layout.dialog_payment_success, (dialog, v) -> {
//                                }).setOnOkButtonClickListener((baseDialog, v) -> {
//                            finish();
//                            return false;
//                        }).show();
//                    } else if (UserInfoCache.getIsVip(this)) {
//                        EventBus.getDefault().postSticky(new VipChargerEvent(true));
//                        EventBus.getDefault().postSticky(fUser);
//                        MessageDialog.build(this)
//                                .setTitle(R.string.txt_payment_success)
//                                .setMessage(R.string.txt_go2_experience_it)
//                                .setOkButton(R.string.txt_experience_it)
//                                .setCancelButton(R.string.txt_look_a_little_bit_more)
//                                .setCustomView(R.layout.dialog_payment_success, (dialog, v) -> {
//                                }).setOnOkButtonClickListener((baseDialog, v) -> {
//                            finish();
//                            return false;
//                        }).show();
//                    }
//                });
//    }
//
//    public void reqMyCapitalDetail() {
//        RxHttp.get(Consts.USER_CAPITAL_API)
//                .asResponse(CapitalEntity.class)
//                .to(RxLife.toMain(this))  //感知生命周期，并在主线程回调
//                .subscribe(data -> {
//                    MessageDialog.build(this)
//                            .setTitle(R.string.txt_payment_success)
//                            .setMessage(R.string.txt_go2_experience_it)
//                            .setOkButton(R.string.txt_experience_it)
//                            .setCancelButton(R.string.txt_look_a_little_bit_more)
//                            .setCustomView(R.layout.dialog_payment_success, (dialog, v) -> {
//                            }).setOnOkButtonClickListener((baseDialog, v) -> {
//                        finish();
//                        return false;
//                    }).show();
//                    EventBus.getDefault().postSticky(data);
//                });
//    }

//    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
//    public void onWebPayFinishEvent(WebPayEvent event) {
//        orderId = event.getCode();
//        queryPayStatu(orderId);
//    }
//
//    private final Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(@NonNull Message msg) {
//            super.handleMessage(msg);
//            if (msg.what == 10001) {
//                queryPayStatu(orderId);
//            }
//        }
//    };
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        MessageDialog.show(this, "订单未生效", "如您已经支付成功，可以选择继续等待或者查看帮助。", "继续等待", "查看帮助")
//                .setCancelable(true)
//                .setOnOkButtonClickListener((baseDialog, v1) -> {
//                    queryPayStatu(orderId);
//                    baseDialog.doDismiss();
//                    return false;
//                })
//                .setOnCancelButtonClickListener(new OnDialogButtonClickListener() {
//                    @Override
//                    public boolean onClick(BaseDialog baseDialog, View v) {
//                        Intent intent = new Intent(RechargeOrderActivity.this, CommonWebActivity.class);
//                        intent.putExtra(Common.KEY_URL, Consts.PRIVILEGE_EXPLAIN_URL);
//                        startActivityForResult(intent, 1000);
//                        return false;
//                    }
//                });
//    }

}