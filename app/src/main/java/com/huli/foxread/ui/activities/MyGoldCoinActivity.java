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
import com.huli.foxread.entity.GoldCoinInfoBean;
import com.huli.foxread.entity.GoldExpenditureBean;
import com.huli.foxread.entity.base.PagingWarpper;
import com.huli.foxread.ui.adapters.GoldCoinDetailAdapter;
import com.huli.foxread.ui.base.BaseActivity;
import com.huli.foxread.utils.NetworkUtil;
import com.huli.foxread.utils.StatusBarUtils;
import com.huli.foxread.utils.Tos;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
        btnGo2Withdrawal = $(R.id.btn_gold_coin_withdrawal);

        mRefreshLayout = $(R.id.smartRefreshLayout_my_gold_coin);
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

        mRefreshLayout.setOnRefreshListener(refreshLayout -> {
            curPage = 0;
            reqEarningsDetail(curPage, false);

            //可以上拉加载
            mAdapter.getLoadMoreModule().setEnableLoadMore(true);
        });
        mAdapter.getLoadMoreModule().setOnLoadMoreListener(() -> reqEarningsDetail(curPage, false));
    }

    @Override
    public void doBusiness(Context mContext) {
        tvExchangeYuan.setText((0 + getString(R.string.unit_yuan)));
        tvAccumulatedGold.setText((getString(R.string.txt_accumulated_gold_colon) + "0"));
        tvGetGoldToday.setText((getString(R.string.txt_get_gold_today_colon) + "0"));

        reqMyGoldCoinInfo();

        reqEarningsDetail(curPage, true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_go2_withdrawal:
            case R.id.btn_gold_coin_withdrawal:
                if (NetworkUtil.isNetworkAvailable(this)) {
                    startActivity(new Intent(this, WithdrawalActivity.class));
                }
                break;
            default:
                break;
        }
    }

    /**
     * 获取金币信息
     */
    private void reqMyGoldCoinInfo() {
        OkGo.<String>get(Consts.GOLD_COIN_INFO_API)
                .cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)
                .cacheTime(5 * 60 * 1000)
                .execute(new LtbCallback(this, false) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<GoldCoinInfoBean> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<GoldCoinInfoBean>>() {
                                });
                        if (entity.error_code == 0) {
                            GoldCoinInfoBean data = entity.getData();
                            tvGoldBalance.setText(String.valueOf(data.getScore()));
                            tvExchangeYuan.setText((data.getScore_money() + getString(R.string.unit_yuan)));
                            tvAccumulatedGold.setText((getString(R.string.txt_accumulated_gold_colon) + data.getScore_total()));
                            tvGetGoldToday.setText((getString(R.string.txt_get_gold_today_colon) + data.getScore_today()));
                        } else {
                            Tos.showShort(MyGoldCoinActivity.this, entity.msg);
                        }
                    }
                });
    }

    /**
     * 金币收益明细
     */
    private void reqEarningsDetail(int page, boolean showDiaog) {
        OkGo.<String>get(Consts.GOLD_EARNINGS_LIST_API)
                .params(Consts.PAGE, page + 1)
                .execute(new LtbCallback(this, showDiaog) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<PagingWarpper<List<GoldExpenditureBean>>> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<PagingWarpper<List<GoldExpenditureBean>>>>() {
                                });
                        if (entity.error_code == 0) {
                            PagingWarpper<List<GoldExpenditureBean>> datas = entity.getData();
                            curPage = datas.getCurrent_page();
                            List<GoldExpenditureBean> bookList = datas.getData();
                            if (curPage == 1) {
                                mAdapter.setNewData(bookList);
                            } else {
                                mAdapter.addData(bookList);
                            }
                            if (datas.getLast_page() <= curPage) {    //没有下一页
                                mAdapter.getLoadMoreModule().loadMoreEnd();
                            } else {
                                mAdapter.getLoadMoreModule().loadMoreComplete();
                            }
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        mAdapter.getLoadMoreModule().loadMoreFail();
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        mRefreshLayout.finishRefresh();
                    }
                });
    }
}
