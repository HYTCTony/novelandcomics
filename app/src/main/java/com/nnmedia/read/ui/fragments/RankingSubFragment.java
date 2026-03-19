package com.nnmedia.read.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.nnmedia.novel.R;
import com.nnmedia.novel.RxHttp;
import com.nnmedia.read.contact.Common;
import com.nnmedia.read.contact.Consts;
import com.nnmedia.read.entity.RankBookEntity;
import com.nnmedia.read.rxhttp.OnError;
import com.nnmedia.read.ui.activities.BookDetailsActivity;
import com.nnmedia.read.ui.adapters.RankingSubAdapter;
import com.nnmedia.read.ui.base.BaseFragment;
import com.nnmedia.read.utils.DateTimeUtil;
import com.rxjava.rxlife.RxLife;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RankingSubFragment extends BaseFragment implements OnItemClickListener {

    private TextView tvExplain, tvUpdateTime;
    private RecyclerView recyclerView;
    private RankingSubAdapter mAdapter;

    private int typeBG;
    private int typeRank;

    private int curPage = 0;

    public static RankingSubFragment newInstance(int typeBG, int typeRank) {
        Bundle bundle = new Bundle();
        bundle.putInt(Consts.RANK_FORM_BG, typeBG);
        bundle.putInt(Consts.TYPE_RANK, typeRank);
        RankingSubFragment frag = new RankingSubFragment();
        frag.setArguments(bundle);
        return frag;
    }


    @Override
    public int bindLayout() {
        return R.layout.fragment_rangking_sub;
    }

    @Override
    public void setStatusBar(View view) {

    }

    @Override
    public void initView(View view) {
        tvExplain = $(view, R.id.tv_rank_list_explain);
        tvUpdateTime = $(view, R.id.tv_rank_list_update_time);

        recyclerView = $(view, R.id.recyclerView_ranking_sub);
        recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mAdapter = new RankingSubAdapter();
        recyclerView.setAdapter(mAdapter);
        mAdapter.setEmptyView(R.layout.layout_empty);
    }

    @Override
    public void setListener() {
        mAdapter.setOnItemClickListener(this);
        // 设置加载更多监听事件
        mAdapter.getLoadMoreModule().setOnLoadMoreListener(() -> reqDataFromNet(typeBG, typeRank, curPage));
    }

    @Override
    public void doBusiness(Context mContext) {
        Bundle bundle = getArguments();
        typeBG = bundle.getInt(Consts.RANK_FORM_BG);
        typeRank = bundle.getInt(Consts.TYPE_RANK);

        mAdapter.setTypeRank(typeRank);
        switch (typeRank) {
            case Consts.RANK_TYPE_HOT:
                tvExplain.setText("根据实时阅读热度排行");
                break;
            case Consts.RANK_TYPE_END:
                tvExplain.setText("根据昨日阅读热度排行");
                break;
            case Consts.RANK_TYPE_DARK_HORSE:
                tvExplain.setText("根据昨日新增阅读人数排行");
                break;
            case Consts.RANK_TYPE_HOT_BOT:
                tvExplain.setText("根据昨日搜索次数排行");
                break;
            default:
                tvExplain.setText("阅读次数排行");
                break;
        }

        reqDataFromNet(typeBG, typeRank, 0);

        reqUpdateTime();
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (onMoreClick()) {
            return;
        }
        RankBookEntity bookEntity = mAdapter.getData().get(position);
        Intent intent = new Intent(mActivity, BookDetailsActivity.class);
        intent.putExtra(Common.KEY_BOOK_ID, bookEntity.getNovel_id());
        startActivity(intent);
    }


    /**
     * 获取排行榜更新时间
     */
    private void reqUpdateTime() {
        RxHttp.get(Consts.POPULAR_TIME_API)
                .asResponse(String.class)
                .to(RxLife.toMain(this))
                .subscribe(s -> {
                    JSONObject data = JSONObject.parseObject(s);
                    long time = data.getLongValue("result");
                    tvUpdateTime.setText(DateTimeUtil.formatDateTime(time * 1000, "MM月dd日更新"));
                });
    }

    /**
     * 获取排行榜信息
     *
     * @param typeBG   男生榜 女生榜
     * @param typeRank
     */
    private void reqDataFromNet(int typeBG, int typeRank, int prePage) {
        RxHttp.get(Consts.POPULAR_RANKING_API)
                .add(Consts.TYPE, typeBG)
                .add(Consts.CATEGORY, typeRank)
                .add(Consts.PAGE, prePage + 1)
                .asResponsePageList(RankBookEntity.class)
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
                },(OnError) error-> mAdapter.getLoadMoreModule().loadMoreFail());
    }
}