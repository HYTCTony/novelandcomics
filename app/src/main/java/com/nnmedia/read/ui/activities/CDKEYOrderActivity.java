package com.nnmedia.read.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.nnmedia.novel.R;
import com.nnmedia.novel.RxHttp;
import com.nnmedia.read.cache.UserInfoCache;
import com.nnmedia.read.contact.Consts;
import com.nnmedia.read.entity.CEYOrderEntity;
import com.nnmedia.read.rxhttp.OnError;
import com.nnmedia.read.ui.adapters.CDKEYOrderAdapter;
import com.nnmedia.read.ui.base.BaseActivity;
import com.rxjava.rxlife.RxLife;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CDKEYOrderActivity extends BaseActivity implements View.OnClickListener {
    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView recyclerView;
    private CDKEYOrderAdapter mAdapter;
    private int curPage = 0;        //当前页码，下一页 +1

    public static void start(Context context) {
        Intent starter = new Intent(context, CDKEYOrderActivity.class);
        context.startActivity(starter);
    }

    @Override
    public void onClick(View v) {

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
        return R.layout.activity_cdkey_order;
    }

    @Override
    public void initView(View view) {
        Toolbar toolbar = $(R.id.toolbar_normal);
        initToolBar(toolbar, R.string.txt_cdkey_records);

        mRefreshLayout = $(R.id.smart);
        recyclerView = $(R.id.recyclerView_pay_records);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new CDKEYOrderAdapter();
        recyclerView.setAdapter(mAdapter);
        mAdapter.setEmptyView(R.layout.layout_empty);
    }

    @Override
    public void setListener() {
        mRefreshLayout.setOnRefreshListener(refreshLayout -> {
            curPage = 0;
            reqCDKEYOrder(curPage);

            //可以上拉加载
            mAdapter.getLoadMoreModule().setEnableLoadMore(true);
        });
        mAdapter.getLoadMoreModule().setOnLoadMoreListener(() -> reqCDKEYOrder(curPage));
    }

    @Override
    public void doBusiness(Context mContext) {
        //需要登录
        if (UserInfoCache.getIsTourist(mContext)) {
            LoginActivity2.start4Result(this, LoginActivity2.REQCODE_LOGIN);
            return;
        }
        mRefreshLayout.autoRefresh();
    }

    /**
     * 账单明细
     */
    private void reqCDKEYOrder(int page) {
        RxHttp.get(Consts.CDKEY_RECORD_API)
                .add(Consts.PAGE, page + 1)
                .asResponsePageList(CEYOrderEntity.class)
                .doFinally(() -> mRefreshLayout.finishRefresh())
                .to(RxLife.toMain(this))
                .subscribe(entity -> {
                    curPage = entity.getCurrent_page();
                    List<CEYOrderEntity> bookList = entity.getData();
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