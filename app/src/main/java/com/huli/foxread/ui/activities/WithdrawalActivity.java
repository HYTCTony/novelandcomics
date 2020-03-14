package com.huli.foxread.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.huli.foxread.R;
import com.huli.foxread.ui.adapters.WithdrawalAmountAdapter;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.utils.DensityUtils;
import com.huli.foxread.utils.StatusBarUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class WithdrawalActivity extends BaseActivity {

    private TextView tvExchangeYuan;

    private RecyclerView recyclerView;
    private WithdrawalAmountAdapter mAdapter;

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
        initToolBar(toolbar, R.string.txt_cash_withdrawal);
        StatusBarUtils.offsetView(this, toolbar);

        tvExchangeYuan = $(R.id.tv_my_gold_coin_balance_exchange_yuan);

        recyclerView = $(R.id.recyclerView_withdrawal);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mAdapter = new WithdrawalAmountAdapter();
        recyclerView.setAdapter(mAdapter);

        LayoutInflater inflater = LayoutInflater.from(this);
        View headView = inflater.inflate(R.layout.layout_rv_head_withdrawal_title, recyclerView, false);
        View footView = inflater.inflate(R.layout.layout_rv_footer_withdrawal, recyclerView, false);
        mAdapter.addHeaderView(headView);
        mAdapter.addFooterView(footView);

    }

    @Override
    public void setListener() {

    }

    @Override
    public void doBusiness(Context mContext) {
        tvExchangeYuan.setText(("10.5元"));

        List<String> list = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            list.add("ssssssssss" + i);
        }
        mAdapter.setNewData(list);
    }
}
