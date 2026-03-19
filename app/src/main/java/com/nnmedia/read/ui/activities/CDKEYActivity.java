package com.nnmedia.read.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kongzue.dialog.v3.MessageDialog;
import com.kongzue.dialog.v3.TipDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.nnmedia.novel.R;
import com.nnmedia.novel.RxHttp;
import com.nnmedia.read.cache.UserInfoCache;
import com.nnmedia.read.contact.Common;
import com.nnmedia.read.contact.Consts;
import com.nnmedia.read.ebsevent.VipChargerEvent;
import com.nnmedia.read.entity.CDKEYDetialEntity;
import com.nnmedia.read.entity.CapitalEntity;
import com.nnmedia.read.entity.FUser;
import com.nnmedia.read.rxhttp.OnError;
import com.nnmedia.read.ui.base.BaseActivity;
import com.nnmedia.read.utils.Tos;
import com.nnmedia.read.utils.UniqueIdManager;
import com.rxjava.rxlife.RxLife;

import org.greenrobot.eventbus.EventBus;

import androidx.appcompat.widget.Toolbar;

public class CDKEYActivity extends BaseActivity implements View.OnClickListener {

    private EditText etCDKEY;
    private TextView btnQueryCDKEY;
    private TextView btnCDKEYOrder;
    private Button btnExchange;

    public static void start(Context context) {
        Intent starter = new Intent(context, CDKEYActivity.class);
        context.startActivity(starter);
    }

    @Override
    public void initParms(Bundle parms) {

    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_cdkey;
    }

    @Override
    public void initView(View view) {
        Toolbar toolbar = $(R.id.toolbar_normal);
        initToolBar(toolbar, R.string.txt_cdkey_exchange);

        etCDKEY = $(R.id.et_cdkey_number);
        btnQueryCDKEY = $(R.id.iv_asBtn_cdkey_query);
        btnCDKEYOrder = $(R.id.iv_asBtn_cdkey_order);
        btnExchange = $(R.id.btn_exchange_cdkey);
    }

    @Override
    public void setListener() {
        btnQueryCDKEY.setOnClickListener(this);
        btnCDKEYOrder.setOnClickListener(this);
        btnExchange.setOnClickListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {
        //需要登录
        if (UserInfoCache.getIsTourist(mContext)) {
            LoginActivity2.start4Result(this, LoginActivity2.REQCODE_LOGIN);
            return;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_asBtn_cdkey_query:
                reqCDKAY();
                break;
            case R.id.iv_asBtn_cdkey_order:
                CDKEYOrderActivity.start(this);
                break;
            case R.id.btn_exchange_cdkey:
                String voucher = etCDKEY.getText().toString();
                if (TextUtils.isEmpty(voucher)) {
                    Tos.showShort(this, R.string.txt_cdkey_tips);
                    return;
                }
                exchange(voucher);
                break;
        }
    }

    /**
     * 卡密兑换
     *
     * @param voucher 卡密
     */
    private void exchange(String voucher) {
        WaitDialog.show(CDKEYActivity.this, "兑换中！");
        RxHttp.postForm(Consts.CDKEY_EXCHANGE_API)
                .add(Consts.VOUCHER, voucher)
                .asResponse(CDKEYDetialEntity.class)
                .to(RxLife.toMain(this))
                .subscribe(data -> {
                    if (data.getType() == 0) {
                        reqUserInfo();
                    } else {
                        reqMyCapitalDetail();
                    }
                }, (OnError) error -> TipDialog.show(CDKEYActivity.this, error.getErrorMsg(), TipDialog.TYPE.ERROR));
    }

    /**
     * 获取用户信息
     */
    private void reqUserInfo() {
        String uniqueID = UniqueIdManager.getUniqueID(this);
        RxHttp.postForm(Consts.USERS_INFO_API)
                .add(Consts.UNIQUE_ID, uniqueID)
                .asResponse(FUser.class)
                .to(RxLife.toMain(this))
                .subscribe(fUser -> {
                    UserInfoCache.saveUserInfo(CDKEYActivity.this, fUser);
                    if (UserInfoCache.getSuperVip(CDKEYActivity.this) == 1) {

                        EventBus.getDefault().postSticky(new VipChargerEvent(true));
                        EventBus.getDefault().postSticky(fUser);
                        MessageDialog.build(CDKEYActivity.this)
                                .setTitle(R.string.txt_exchange_success)
                                .setMessage(R.string.txt_go2_experience_it)
                                .setOkButton(R.string.txt_experience_it)
                                .setCancelButton(R.string.txt_look_a_little_bit_more)
                                .setCustomView(R.layout.dialog_payment_success, (dialog, v) -> {
                                }).setOnOkButtonClickListener((baseDialog, v) -> {
                            finish();
                            return false;
                        }).show();
                    } else if (UserInfoCache.getIsVip(CDKEYActivity.this)) {

                        EventBus.getDefault().postSticky(new VipChargerEvent(true));
                        EventBus.getDefault().postSticky(fUser);
                        MessageDialog.build(CDKEYActivity.this)
                                .setTitle(R.string.txt_exchange_success)
                                .setMessage(R.string.txt_go2_experience_it)
                                .setOkButton(R.string.txt_experience_it)
                                .setCancelButton(R.string.txt_look_a_little_bit_more)
                                .setCustomView(R.layout.dialog_payment_success, (dialog, v) -> {
                                }).setOnOkButtonClickListener((baseDialog, v) -> {
                            finish();
                            return false;
                        }).show();
                    }
                }, (OnError) error -> {
                    TipDialog.show(CDKEYActivity.this, error.getErrorMsg(), TipDialog.TYPE.ERROR);
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
                    MessageDialog.build(CDKEYActivity.this)
                            .setTitle(R.string.txt_exchange_success)
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

    /**
     * 卡密查询
     */
    public void reqCDKAY() {
        RxHttp.get(Consts.CDKET_ORDER_QUERY)
                .asResponse(String.class)
                .doOnSubscribe(disposable -> showLoadingDialog())
                .doFinally(this::dismissLoadingDialog)
                .to(RxLife.toMain(this))  //感知生命周期，并在主线程回调
                .subscribe(url -> {
                    Intent intent = new Intent(CDKEYActivity.this, CommonWebActivity.class);
                    intent.putExtra(Common.KEY_URL, url);
                    startActivity(intent);
                });
    }
}