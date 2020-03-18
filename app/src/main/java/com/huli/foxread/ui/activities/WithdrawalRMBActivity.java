package com.huli.foxread.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.huli.foxread.R;
import com.huli.foxread.callbacks.ookkggoo.LtbCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.entity.WithdrawalOptionEntity;
import com.huli.foxread.listeners.OnClickEvent;
import com.huli.foxread.ui.adapters.WithdrawalMoneyAdapter;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.utils.StatusBarUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * RMB提现
 */
public class WithdrawalRMBActivity extends BaseActivity {

    private TextView btnRecord;

    private RecyclerView recyclerView;
    private WithdrawalMoneyAdapter mAdapter;

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
        return R.layout.activity_rmb_withdrawal;
    }

    @Override
    public void initView(View view) {
        Toolbar toolbar = $(R.id.toolbar_normal);
        toolbar.setBackgroundResource(R.color.transparent);
        initToolBar(toolbar, R.string.txt_money_withdrawal);
        StatusBarUtils.offsetView(this, toolbar);

        btnRecord = $(R.id.tv_asBtn_withdrawal_record);

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
        btnRecord.setOnClickListener(new OnClickEvent() {
            @Override
            public void singleClick(View v) {
                startActivity(new Intent(WithdrawalRMBActivity.this, WithdrawalRecordActivity.class));
            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {

        tvWithdrawalTips.setText("1、微信提现步骤：选择提现金额---微信授权---提现成功\n1、微信提现步骤：选择提现金额---微信授权---提现成功\n1、微信提现步骤：选择提现金额---微信授权---提现成功\n1、微信提现步骤：选择提现金额---微信授权---提现成功"
        );

        reqWithdrawalCombo();
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
                            mAdapter.setNewData(datas);
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

                        }
                    }
                });
    }
}
