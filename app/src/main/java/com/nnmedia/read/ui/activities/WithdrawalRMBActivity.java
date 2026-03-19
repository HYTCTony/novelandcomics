package com.nnmedia.read.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.kongzue.dialog.v3.MessageDialog;
import com.kongzue.dialog.v3.TipDialog;
import com.nnmedia.novel.R;
import com.nnmedia.novel.RxHttp;
import com.nnmedia.read.cache.UserInfoCache;
import com.nnmedia.read.contact.Common;
import com.nnmedia.read.contact.Consts;
import com.nnmedia.read.entity.CapitalEntity;
import com.nnmedia.read.entity.WithdrawalOptionEntity;
import com.nnmedia.read.rxhttp.OnError;
import com.nnmedia.read.rxhttp.Tip;
import com.nnmedia.read.ui.adapters.WithdrawalMoneyAdapter;
import com.nnmedia.read.ui.base.BaseActivity;
import com.nnmedia.read.utils.DateTimeUtil;
import com.nnmedia.read.utils.StatusBarUtils;
import com.nnmedia.read.utils.Tos;
import com.rxjava.rxlife.RxLife;

import java.text.DecimalFormat;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * RMB提现
 */
public class WithdrawalRMBActivity extends BaseActivity implements View.OnClickListener {
    private static final int REQCODE_BIND_BANKCARD = 0x1999;

    private TextView btnRecord;
    private TextView tvBalance;
    private RecyclerView recyclerView;
    private WithdrawalMoneyAdapter mAdapter;

    private TextView tvWithdrawalTips;
    private TextView btnConfirm;

    private double myMoney;
    private DecimalFormat df = new DecimalFormat("#######0.00");

    @Override
    protected void setStatusBar() {
        StatusBarUtils.setColor(this, ContextCompat.getColor(this, R.color.transparent), 0);
        StatusBarUtils.setAndroidNativeLightStatusBar(this, false);
    }

    @Override
    public void initParms(Bundle parms) {
        if (parms != null) {
            myMoney = parms.getDouble(Common.EXTRA_KEY_MONEY, 0);
        }
    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_rmb_withdrawal;
    }

    @Override
    public void initView(View view) {
        Toolbar toolbar = $(R.id.toolbar_normal);
        toolbar.setBackgroundResource(R.color.transparent);
        initToolBar(toolbar, R.string.txt_money_withdrawal);
        StatusBarUtils.offsetView(this, toolbar);

        btnRecord = $(R.id.tv_asBtn_withdrawal_record);
        tvBalance = $(R.id.tv_my_money_balance);
        tvBalance.setText(df.format(myMoney));

        btnConfirm = $(R.id.btn_money_withdrawal);

        recyclerView = $(R.id.recyclerView_withdrawal);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mAdapter = new WithdrawalMoneyAdapter();
        recyclerView.setAdapter(mAdapter);

        LayoutInflater inflater = LayoutInflater.from(this);
        View headView = inflater.inflate(R.layout.layout_rv_head_withdrawal_title, recyclerView, false);
        View footView = inflater.inflate(R.layout.layout_rv_footer_withdrawal, recyclerView, false);
        mAdapter.addHeaderView(headView);
        mAdapter.addFooterView(footView);
        TextView subTitle = headView.findViewById(R.id.tv_title);
        subTitle.setText(R.string.txt_money_withdrawal);
        tvWithdrawalTips = footView.findViewById(R.id.tv_withdrawal_tips);

    }

    @Override
    public void setListener() {
        btnRecord.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {
        //需要登录
        if (UserInfoCache.getIsTourist(mContext)) {
            LoginActivity2.start4Result(this, LoginActivity2.REQCODE_LOGIN);
            return;
        }
        reqWithdrawalCombo();
    }


    @Override
    public void onClick(View view) {
        if (onMoreClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.tv_asBtn_withdrawal_record:
                startActivity(new Intent(WithdrawalRMBActivity.this, WithdrawalRecordActivity.class));
                break;
            case R.id.btn_money_withdrawal:
                String planId = mAdapter.getSelectPlanId();
                if (TextUtils.isEmpty(planId)) {
                    Tos.showShort(this, R.string.txt_plz_select_withdrawal_money);
                    return;
                }
                reqMoneyWithdrawal(planId);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQCODE_BIND_BANKCARD) {
                btnConfirm.performLongClick();
            }
        }
    }


    /**
     * 现金（RMB）提现套餐
     */
    private void reqWithdrawalCombo() {
        RxHttp.postForm(Consts.WITHDRAWAL_FARE_API)
                .asResponseList(WithdrawalOptionEntity.class)
                .doOnSubscribe(disposable -> showLoadingDialog())
                .doFinally(this::dismissLoadingDialog)
                .to(RxLife.toMain(this))
                .subscribe(datas -> mAdapter.setList(datas));
    }

    /**
     * 现金（余额）提现
     */
    private void reqMoneyWithdrawal(String planID) {
        RxHttp.postForm(Consts.WITHDRAWAL_MONEY_API)
                .add(Consts.WITHDRAWAL_PLAN_ID, planID)
                .asResponse(String.class)
                .doOnSubscribe(disposable -> showLoadingDialog())
                .doFinally(this::dismissLoadingDialog)
                .to(RxLife.toMain(this))
                .subscribe(s -> {
                    reqMyCapitalDetail();
                    MessageDialog.show(WithdrawalRMBActivity.this, getString(R.string.txt_withdrawal_success_title),
                            DateTimeUtil.getCurrentDate(), getString(R.string.txt_got_it))
                            .setCustomView(R.layout.dialog_withdrawal_success, (dialog, v) -> {
                            });
                }, (OnError) error -> {
                    if (error.getErrorCode() == 10008) {
                        Tip.show(error.getErrorMsg());
                        startActivityForResult(new Intent(WithdrawalRMBActivity.this, BankCardBindActivity.class), REQCODE_BIND_BANKCARD);
                    } else {
                        TipDialog.show(WithdrawalRMBActivity.this, error.getErrorMsg(), TipDialog.TYPE.ERROR);
                    }
                });
    }


    /**
     * 我的资金详情
     */
    public void reqMyCapitalDetail() {
        RxHttp.get(Consts.USER_CAPITAL_API)
                .asResponse(CapitalEntity.class)
                .to(RxLife.toMain(this))
                .subscribe(data -> tvBalance.setText(df.format(data.getMoney())));
    }
}
