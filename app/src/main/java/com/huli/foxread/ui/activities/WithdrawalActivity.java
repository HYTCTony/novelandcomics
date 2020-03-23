package com.huli.foxread.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.huli.foxread.R;
import com.huli.foxread.callbacks.ookkggoo.LtbCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.entity.CapitalEntity;
import com.huli.foxread.entity.WithdrawalOptionEntity;
import com.huli.foxread.ui.adapters.WithdrawalGoldAdapter;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.utils.DateTimeUtil;
import com.huli.foxread.utils.StatusBarUtils;
import com.huli.foxread.utils.Tos;
import com.kongzue.dialog.v3.MessageDialog;
import com.kongzue.dialog.v3.TipDialog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.text.DecimalFormat;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 金币提现
 */
public class WithdrawalActivity extends BaseActivity implements View.OnClickListener {
    private static final int REQCODE_BIND_BANKCARD = 0x1999;

    private TextView tvGoldBalance, tvExchangeYuan;
    private TextView btnRecord;
    private TextView btnWithdrawal;

    private RecyclerView recyclerView;
    private WithdrawalGoldAdapter mAdapter;

    private TextView tvWithdrawalTips;

    @Override
    protected void setStatusBar() {
        StatusBarUtils.setColor(this, ContextCompat.getColor(this, R.color.transparent), 0);
        StatusBarUtils.setAndroidNativeLightStatusBar(this, false);
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
        return R.layout.activity_gold_coin_withdrawal;
    }

    @Override
    public void initView(View view) {
        Toolbar toolbar = $(R.id.toolbar_normal);
        toolbar.setBackgroundResource(R.color.transparent);
        initToolBar(toolbar, R.string.txt_gold_coin_withdrawal);
        StatusBarUtils.offsetView(this, toolbar);

        tvGoldBalance = $(R.id.tv_my_gold_coin_balance);
        tvExchangeYuan = $(R.id.tv_my_gold_coin_balance_exchange_yuan);
        btnRecord = $(R.id.tv_asBtn_withdrawal_record);
        btnWithdrawal = $(R.id.btn_gold_coin_withdrawal);

        recyclerView = $(R.id.recyclerView_withdrawal);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mAdapter = new WithdrawalGoldAdapter();
        recyclerView.setAdapter(mAdapter);

        LayoutInflater inflater = LayoutInflater.from(this);
        View headView = inflater.inflate(R.layout.layout_rv_head_withdrawal_title, recyclerView, false);
        View footView = inflater.inflate(R.layout.layout_rv_footer_withdrawal, recyclerView, false);
        mAdapter.addHeaderView(headView);
        mAdapter.addFooterView(footView);
        tvWithdrawalTips = footView.findViewById(R.id.tv_withdrawal_tips);

    }

    @Override
    public void setListener() {
        btnRecord.setOnClickListener(this);
        btnWithdrawal.setOnClickListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {
        tvWithdrawalTips.setText("1、微信提现步骤：选择提现金额---微信授权---提现成功\n1、微信提现步骤：选择提现金额---微信授权---提现成功\n1、微信提现步骤：选择提现金额---微信授权---提现成功\n1、微信提现步骤：选择提现金额---微信授权---提现成功"
        );

        reqMyCapitalDetail();

        reqWithdrawalCombo();
    }


    @Override
    public void onClick(View view) {
        if (onMoreClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.tv_asBtn_withdrawal_record:
                startActivity(new Intent(WithdrawalActivity.this, WithdrawalRecordActivity.class));
                break;

            case R.id.btn_gold_coin_withdrawal:
                String planId = mAdapter.getSelectPlanId();
                if (!TextUtils.isEmpty(planId)) {
                    reqGoldWithdrawal(planId);
                }
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
                btnWithdrawal.performLongClick();
            }
        }
    }

    /**
     * 金币提现套餐
     */
    private void reqWithdrawalCombo() {
        OkGo.<String>post(Consts.WITHDRAWAL_MENU_API)
                .execute(new LtbCallback(this) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<List<WithdrawalOptionEntity>> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<List<WithdrawalOptionEntity>>>() {
                                });
                        if (entity.error_code == 0) {
                            List<WithdrawalOptionEntity> datas = entity.getData();
                            mAdapter.setNewData(datas);
                        }
                    }
                });
    }


    /**
     * 金币提现
     */
    private void reqGoldWithdrawal(String planID) {
        OkGo.<String>post(Consts.WITHDRAWAL_SCORE_API)
                .params(Consts.WITHDRAWAL_PLAN_ID, planID)
                .execute(new LtbCallback(this) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<String> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<String>>() {
                                });
                        if (entity.error_code == 0) {
                            MessageDialog.show(WithdrawalActivity.this, getString(R.string.txt_withdrawal_success_title),
                                    DateTimeUtil.getCurrentDate(), getString(R.string.txt_got_it))
                                    .setCustomView(R.layout.dialog_withdrawal_success, (dialog, v) -> {
                                    });
                            reqMyCapitalDetail();
                        } else if (entity.error_code == 10003) {
                            Tos.showShort(WithdrawalActivity.this, entity.msg);
                            startActivityForResult(new Intent(WithdrawalActivity.this, BankCardBindActivity.class), REQCODE_BIND_BANKCARD);
                        } else {
                            TipDialog.show(WithdrawalActivity.this, entity.msg, TipDialog.TYPE.ERROR);
                        }
                    }
                });
    }


    /**
     * 我的资金详情
     */
    public void reqMyCapitalDetail() {
        OkGo.<String>get(Consts.USER_CAPITAL_API)
                .execute(new LtbCallback(this, false) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<CapitalEntity> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<CapitalEntity>>() {
                                });
                        if (entity.error_code == 0) {
                            CapitalEntity data = entity.getData();

                            int goldCoinBalance = data.getScore();
                            tvGoldBalance.setText(String.valueOf(goldCoinBalance));

                            double exchangeMoney;       //金币余额转换RMB
                            try {
                                exchangeMoney = (double) goldCoinBalance / data.getProportion();
                            } catch (Exception e) {
                                exchangeMoney = 0;
                            }
                            DecimalFormat df = new DecimalFormat("#######.##" + getString(R.string.unit_yuan));
                            tvExchangeYuan.setText(df.format(exchangeMoney));
                        }
                    }
                });
    }

}
