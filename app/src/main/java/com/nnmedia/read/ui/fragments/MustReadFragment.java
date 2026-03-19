package com.nnmedia.read.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.nnmedia.novel.R;
import com.nnmedia.novel.RxHttp;
import com.nnmedia.read.contact.Common;
import com.nnmedia.read.contact.Consts;
import com.nnmedia.read.entity.RankBookEntity;
import com.nnmedia.read.rxhttp.OnError;
import com.nnmedia.read.ui.activities.BookDetailsActivity;
import com.nnmedia.read.ui.adapters.MustReadAdapter;
import com.nnmedia.read.ui.base.BaseFragment;
import com.rxjava.rxlife.RxLife;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/*
 * 必读榜
 * */
public class MustReadFragment extends BaseFragment implements OnItemClickListener {

    int grey;

    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView recyclerView;
    private MustReadAdapter mAdapter;

    private int typeBG = 1;
    private int typeRank = 1;

    private int curPage = 0;

    public static MustReadFragment newInstance() {
        Bundle args = new Bundle();
        MustReadFragment fragment = new MustReadFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int bindLayout() {
        return R.layout.fragment_must_read;
    }

    @Override
    public void setStatusBar(View view) {

    }

    @Override
    public void initView(View view) {
        grey = ContextCompat.getColor(mActivity, R.color.light_translucent);

        mRefreshLayout = $(view, R.id.smartRefreshLayout_book_store);

        recyclerView = $(view, R.id.recyclerView_book_store);
//        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
//        recyclerView.setLayoutManager(manager);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity())
                .color(grey)
                .sizeResId(R.dimen.dp_0_5)
                .build());
        recyclerView.setHasFixedSize(true);
        mAdapter = new MustReadAdapter();
        recyclerView.setAdapter(mAdapter);
        mAdapter.setEmptyView(R.layout.layout_empty);
    }

    @Override
    public void setListener() {
        mAdapter.setOnItemClickListener(this);

        //刷新
        mRefreshLayout.setOnRefreshListener(refreshLayout -> {
            reqDataFromNet(false, typeBG, typeRank, 0);

            mAdapter.getLoadMoreModule().setEnableLoadMore(true);

            curPage = 0;

            refreshLayout.finishLoadMore(15);
        });

        // 设置加载更多监听事件
        mAdapter.getLoadMoreModule().setOnLoadMoreListener(() -> reqDataFromNet(false, typeBG, typeRank, curPage));
    }

    @Override
    public void doBusiness(Context mContext) {
        reqDataFromNet(true, typeBG, typeRank, 0);
    }

    @Override
    public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
        if (onMoreClick()) {
            return;
        }
        RankBookEntity bookEntity = mAdapter.getData().get(position);
        Intent intent = new Intent(mActivity, BookDetailsActivity.class);
        intent.putExtra(Common.KEY_BOOK_ID, bookEntity.getNovel_id());
        startActivity(intent);
    }

    /**
     * 获取排行榜信息
     *
     * @param typeBG   男生榜 女生榜
     * @param typeRank
     */
    private void reqDataFromNet(boolean showDialog, int typeBG, int typeRank, int prePage) {
        RxHttp.get(Consts.POPULAR_RANKING_API)
                .add(Consts.TYPE, typeBG)
                .add(Consts.CATEGORY, typeRank)
                .add(Consts.PAGE, prePage + 1)
                .asResponsePageList(RankBookEntity.class)
                .doOnSubscribe(disposable -> {
                    if (showDialog) {
                        showLoadingDialog();
                    }
                })
                .doFinally(() -> {
                    dismissLoadingDialog();
                    mRefreshLayout.finishRefresh();
                })
                .to(RxLife.toMain(this))
                .subscribe(entity -> {
                    curPage = entity.getCurrent_page();
                    List<RankBookEntity> bookList = entity.getData();
                    if (curPage == 1) {
                        mAdapter.setList(bookList);
                    } else {
                        mAdapter.addData(bookList);
                    }
                    if (entity.getLast_page() <= curPage) {
                        //没有下一页
                        mAdapter.getLoadMoreModule().loadMoreEnd();
                    } else {
                        mAdapter.getLoadMoreModule().loadMoreComplete();
                    }
                }, (OnError) error -> mAdapter.getLoadMoreModule().loadMoreFail());
    }

}