package com.huli.foxread.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.huli.foxread.R;
import com.huli.foxread.ui.adapters.GoldCoinDetailAdapter;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.utils.StatusBarUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MyGoldCoinActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvExchangeYuan;
    private TextView tvAccumulatedGold, tvGetGoldToday;

    private TextView tvGo2Withdrawal;
    private TextView btnGo2Withdrawal;

    private RecyclerView recyclerView;
    private GoldCoinDetailAdapter mAdapter;

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
        return R.layout.activity_my_gold_coin;
    }

    @Override
    public void initView(View view) {
        Toolbar toolbar = $(R.id.toolbar_normal);
        toolbar.setBackgroundResource(R.color.transparent);
        initToolBar(toolbar, R.string.txt_goldcoin_balance);
        StatusBarUtils.offsetView(this, toolbar);

        tvExchangeYuan = $(R.id.tv_my_gold_coin_balance_exchange_yuan);
        tvAccumulatedGold = $(R.id.tv_accumulated_gold);
        tvGetGoldToday = $(R.id.tv_get_gold_today);
        tvGo2Withdrawal = $(R.id.tv_go2_withdrawal);
        btnGo2Withdrawal = $(R.id.btn_gold_coin_withdrawal);

        recyclerView = $(R.id.recyclerView_goldCoin_details);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new GoldCoinDetailAdapter();
        recyclerView.setAdapter(mAdapter);

        View headView = LayoutInflater.from(this).inflate(R.layout.layout_rv_head_gold_coin_details, recyclerView, false);
        mAdapter.addHeaderView(headView);

    }

    @Override
    public void setListener() {
        tvGo2Withdrawal.setOnClickListener(this);
        btnGo2Withdrawal.setOnClickListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {
        tvExchangeYuan.setText(("10.5元"));
        tvAccumulatedGold.setText((getString(R.string.txt_accumulated_gold_colon) + "1000"));
        tvGetGoldToday.setText((getString(R.string.txt_get_gold_today_colon) + "200"));

        List<String> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add("ssssssssss" + i);
        }
        mAdapter.setNewData(list);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_go2_withdrawal:
            case R.id.btn_gold_coin_withdrawal:
                startActivity(new Intent(this, WithdrawalActivity.class));
                break;
            default:
                break;
        }
    }
}
