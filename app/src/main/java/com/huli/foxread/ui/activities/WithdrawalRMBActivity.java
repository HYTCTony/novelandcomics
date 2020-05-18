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
import com.huli.foxread.cache.UserInfoCache;
import com.huli.foxread.callbacks.ookkggoo.LtbCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Common;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.entity.CapitalEntity;
import com.huli.foxread.entity.WithdrawalOptionEntity;
import com.huli.foxread.ui.adapters.WithdrawalMoneyAdapter;
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
            LoginActivity.start4Result(this, LoginActivity.REQCODE_LOGIN);
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
        OkGo.<String>post(Consts.WITHDRAWAL_FARE_API)
                .execute(new LtbCallback(this) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<List<WithdrawalOptionEntity>> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<List<WithdrawalOptionEntity>>>() {
                                });
                        if (entity.error_code == 0) {
                            List<WithdrawalOptionEntity> datas = entity.getData();
                            mAdapter.setNewInstance(datas);
                        }
                    }
                });
    }

    /**
     * 现金（余额）提现
     */
    private void reqMoneyWithdrawal(String planID) {
        OkGo.<String>post(Consts.WITHDRAWAL_MONEY_API)
                .params(Consts.WITHDRAWAL_PLAN_ID, planID)
                .execute(new LtbCallback(this) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<String> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<String>>() {
                                });
                        if (entity.error_code == 0) {
                            reqMyCapitalDetail();
                            MessageDialog.show(WithdrawalRMBActivity.this, getString(R.string.txt_withdrawal_success_title),
                                    DateTimeUtil.getCurrentDate(), getString(R.string.txt_got_it))
                                    .setCustomView(R.layout.dialog_withdrawal_success, (dialog, v) -> {
                                    });
                        } else if (entity.error_code == 10008) {
                            Tos.showShort(WithdrawalRMBActivity.this, entity.msg);
                            startActivityForResult(new Intent(WithdrawalRMBActivity.this, BankCardBindActivity.class), REQCODE_BIND_BANKCARD);
                        } else {
                            TipDialog.show(WithdrawalRMBActivity.this, entity.msg, TipDialog.TYPE.ERROR);
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
                            tvBalance.setText(df.format(data.getMoney()));
                        }
                    }
                });
    }
}
