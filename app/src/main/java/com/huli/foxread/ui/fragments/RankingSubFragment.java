package com.huli.foxread.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.huli.foxread.R;
import com.huli.foxread.callbacks.ookkggoo.LtbCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Common;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.entity.RankBookEntity;
import com.huli.foxread.entity.base.PagingWarpper;
import com.huli.foxread.ui.activities.BookDetailsActivity;
import com.huli.foxread.ui.adapters.RankingSubAdapter;
import com.huli.foxread.ui.base.BaseFragment;
import com.huli.foxread.utils.DateTimeUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
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
        OkGo.<String>get(Consts.POPULAR_TIME_API)
                .execute(new LtbCallback((AppCompatActivity) mActivity, false) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<String> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<String>>() {
                                });
                        if (entity.error_code == 0) {
                            JSONObject data = JSONObject.parseObject(entity.getData());
                            long time = data.getLongValue("result");
                            tvUpdateTime.setText(DateTimeUtil.formatDateTime(time * 1000, "MM月dd日更新"));
                        }
                    }
                });
    }

    /**
     * 获取排行榜信息
     *
     * @param typeBG   男生榜 女生榜
     * @param typeRank
     */
    private void reqDataFromNet(int typeBG, int typeRank, int prePage) {
        OkGo.<String>get(Consts.POPULAR_RANKING_API)
                .params(Consts.TYPE, typeBG)
                .params(Consts.CATEGORY, typeRank)
                .params(Consts.PAGE, prePage + 1)
                .execute(new LtbCallback((AppCompatActivity) mActivity, false) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        LzyResponse<PagingWarpper<List<RankBookEntity>>> entity = JSONObject.parseObject(response.body(),
                                new TypeReference<LzyResponse<PagingWarpper<List<RankBookEntity>>>>() {
                                });
                        if (entity.error_code == 0) {
                            PagingWarpper<List<RankBookEntity>> datas = entity.getData();
                            curPage = datas.getCurrent_page();
                            List<RankBookEntity> bookList = datas.getData();
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
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        mAdapter.getLoadMoreModule().loadMoreFail();
                    }
                });
    }
}
