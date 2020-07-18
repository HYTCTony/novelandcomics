package com.huli.foxread.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.huli.foxread.R;
import com.huli.foxread.RxHttp;
import com.huli.foxread.contact.Common;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.entity.BookEntity;
import com.huli.foxread.entity.base.PageList;
import com.huli.foxread.rxhttp.OnError;
import com.huli.foxread.ui.adapters.MoreBooksAdapter;
import com.huli.foxread.ui.base.BaseActivity;
import com.rxjava.rxlife.RxLife;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 项目名称：FoxRead
 * 创建人：Bill
 * 创建时间：2020/7/18  17:33
 * 备注：更多书籍
 */
public class MoreBooksListActivity extends BaseActivity implements OnItemClickListener, OnLoadMoreListener, OnRefreshListener {

    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView recyclerView;
    private MoreBooksAdapter mAdapter;
    private int curPage = 0;        //当前页码

    private String mTitle;
    private String mId;


    public static void start(Context context, String title, String columnId) {
        Intent starter = new Intent(context, MoreBooksListActivity.class);
        starter.putExtra("title", title);
        starter.putExtra("column_id", columnId);
        context.startActivity(starter);
    }

    @Override
    public void initParms(Bundle parms) {
        mTitle = parms.getString("title");
        mId = parms.getString("column_id");
    }

    @Override
    public View bindView() {
        return null;
    }

    @Override
    public int bindLayout() {
        return R.layout.activity_refresh_recy_normal;
    }

    @Override
    public void initView(View view) {
        Toolbar toolbar = $(R.id.toolbar_normal);
        initToolBar(toolbar, mTitle);

        mRefreshLayout = $(R.id.smartRefreshLayout_ac_normal);
        mRefreshLayout.setEnableLoadMore(false);
        recyclerView = $(R.id.recyclerView_ac_normal);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new MoreBooksAdapter();
        mAdapter.setAnimationEnable(true);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setEmptyView(R.layout.layout_empty);
    }

    @Override
    public void setListener() {
        mRefreshLayout.setOnRefreshListener(this);
        mAdapter.getLoadMoreModule().setOnLoadMoreListener(this);
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {
        reqDatas(0);
    }


    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        //可以上拉加载
        mAdapter.getLoadMoreModule().setEnableLoadMore(true);
        curPage = 0;
        reqDatas(curPage);
    }

    @Override
    public void onLoadMore() {
        reqDatas(curPage);
    }


    @Override
    public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
        if (onMoreClick()) {
            return;
        }
        BookEntity book = mAdapter.getData().get(position);
        if (book != null) {
            Intent intent = new Intent(this, BookDetailsActivity.class);
            intent.putExtra(Common.KEY_BOOK_ID, book.getId());
            startActivity(intent);
        }
    }


    /**
     * 获取完本书
     */
    private void reqDatas(int page) {
        RxHttp.get(Consts.NOVEL_COLUMN_DETAIL_API)
                .add(Consts.COLUMN_ID, mId)
                .add(Consts.PAGE, page + 1)
                .asResponseTTPageList(BookEntity.class)
                .doFinally(() -> mRefreshLayout.finishRefresh())
                .to(RxLife.toMain(this))
                .subscribe(entity -> {
                    PageList<BookEntity> datas = entity.getList();

                    curPage = datas.getCurrent_page();
                    List<BookEntity> bookList = datas.getData();
                    if (curPage == 1) {
                        mAdapter.setList(bookList);
                    } else {
                        mAdapter.addData(bookList);
                    }
                    if (datas.getLast_page() <= curPage) {
                        //没有下一页
                        mAdapter.getLoadMoreModule().loadMoreEnd();
                    } else {
                        mAdapter.getLoadMoreModule().loadMoreComplete();
                    }
                }, (OnError) error -> mAdapter.getLoadMoreModule().loadMoreFail());
    }
}
