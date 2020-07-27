package com.huli.foxread.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.huli.foxread.R;
import com.huli.foxread.RxHttp;
import com.huli.foxread.cache.UserInfoCache;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.entity.CapitalEntity;
import com.huli.foxread.entity.GoldExpenditureBean;
import com.huli.foxread.rxhttp.OnError;
import com.huli.foxread.ui.adapters.GoldCoinDetailAdapter;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.utils.NetworkUtil;
import com.huli.foxread.utils.StatusBarUtils;
import com.rxjava.rxlife.RxLife;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 金币余额
 */
public class MyGoldCoinActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvGoldBalance, tvExchangeYuan;
    private TextView tvAccumulatedGold, tvGetGoldToday;

    private TextView tvGo2Withdrawal;
    private TextView btnGo2Withdrawal;

    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView recyclerView;
    private GoldCoinDetailAdapter mAdapter;


    private int curPage = 0;        //当前页码，下一页 +1

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

        tvGoldBalance = $(R.id.tv_my_gold_coin_balance);
        tvExchangeYuan = $(R.id.tv_my_gold_coin_balance_exchange_yuan);
        tvAccumulatedGold = $(R.id.tv_accumulated_gold);
        tvGetGoldToday = $(R.id.tv_get_gold_today);
        tvGo2Withdrawal = $(R.id.tv_go2_withdrawal);
        btnGo2Withdrawal = $(R.id.btn_invite_friends_2_make_money);

        mRefreshLayout = $(R.id.smartRefreshLayout_my_gold_coin);
        recyclerView = $(R.id.recyclerView_goldCoin_details);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new GoldCoinDetailAdapter();
        recyclerView.setAdapter(mAdapter);
        mAdapter.setEmptyView(R.layout.layout_empty);

        View headView = LayoutInflater.from(this).inflate(R.layout.layout_rv_head_gold_coin_details, recyclerView, false);
        mAdapter.addHeaderView(headView);

    }

    @Override
    public void setListener() {
        tvGo2Withdrawal.setOnClickListener(this);
        btnGo2Withdrawal.setOnClickListener(this);

        mRefreshLayout.setOnRefreshListener(refreshLayout -> {
            curPage = 0;
            reqEarningsDetail(curPage);

            //可以上拉加载
            mAdapter.getLoadMoreModule().setEnableLoadMore(true);
        });
        mAdapter.getLoadMoreModule().setOnLoadMoreListener(() -> reqEarningsDetail(curPage));
    }

    @Override
    public void doBusiness(Context mContext) {
        //需要登录
        if (UserInfoCache.getIsTourist(mContext)) {
            LoginActivity.start4Result(this, LoginActivity.REQCODE_LOGIN);
            return;
        }
        
        tvExchangeYuan.setText((0 + getString(R.string.unit_yuan)));
        tvAccumulatedGold.setText((getString(R.string.txt_accumulated_gold_colon) + "0"));
        tvGetGoldToday.setText((getString(R.string.txt_get_gold_today_colon) + "0"));

        mRefreshLayout.autoRefresh();
    }

    @Override
    protected void onResume() {
        super.onResume();
        reqMyCapitalDetail();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_go2_withdrawal:
            case R.id.btn_invite_friends_2_make_money:
                if (NetworkUtil.isNetworkAvailable(this)) {
                    startActivity(new Intent(this, WithdrawalActivity.class));
                }
                break;
            default:
                break;
        }
    }


    /**
     * 我的资金详情
     */
    public void reqMyCapitalDetail() {
        RxHttp.get(Consts.USER_CAPITAL_API)
                .asResponse(CapitalEntity.class)
                .to(RxLife.toMain(this))
                .subscribe(entity -> {
                    int goldCoinBalance = entity.getScore();
                    tvGoldBalance.setText(String.valueOf(goldCoinBalance));

                    int exchangeMoney;       //金币余额转换RMB
                    try {
                        exchangeMoney = goldCoinBalance / entity.getProportion();
                    } catch (Exception e) {
                        exchangeMoney = 0;
                    }
//                    DecimalFormat df = new DecimalFormat("约" + "#######.##" + getString(R.string.unit_yuan));
                    tvExchangeYuan.setText((exchangeMoney + getString(R.string.unit_yuan)));

                    tvAccumulatedGold.setText((getString(R.string.txt_accumulated_gold_colon) + entity.getScore_sum()));
                    tvGetGoldToday.setText((getString(R.string.txt_get_gold_today_colon) + entity.getToday_score()));
                });
    }


    /**
     * 金币收益明细
     */
    private void reqEarningsDetail(int page) {
        RxHttp.get(Consts.GOLD_EARNINGS_LIST_API)
                .add(Consts.PAGE, page + 1)
                .asResponsePageList(GoldExpenditureBean.class)
                .doFinally(() -> mRefreshLayout.finishRefresh())
                .to(RxLife.toMain(this))
                .subscribe(entity -> {
                    curPage = entity.getCurrent_page();
                    List<GoldExpenditureBean> bookList = entity.getData();
                    if (curPage == 1) {
                        mAdapter.setList(bookList);
                    } else {
                        mAdapter.addData(bookList);
                    }
                    if (entity.getLast_page() <= curPage) {    //没有下一页
                        mAdapter.getLoadMoreModule().loadMoreEnd();
                    } else {
                        mAdapter.getLoadMoreModule().loadMoreComplete();
                    }
                }, (OnError) error -> mAdapter.getLoadMoreModule().loadMoreFail());
    }
}
