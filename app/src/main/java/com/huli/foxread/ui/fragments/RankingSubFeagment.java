package com.huli.foxread.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.huli.foxread.R;
import com.huli.foxread.ui.adapters.RankingSubAdapter;
import com.huli.foxread.ui.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RankingSubFeagment extends BaseFragment {

    private TextView tvExplain, tvUpdateTime;
    private RecyclerView recyclerView;
    private RankingSubAdapter mAdapter;

    private int mType;

    public static RankingSubFeagment newInstance(int type) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        RankingSubFeagment frag = new RankingSubFeagment();
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
    }

    @Override
    public void setListener() {

    }

    @Override
    public void doBusiness(Context mContext) {
        mType = getArguments().getInt("type");


        tvExplain.setText("近7天阅读次数排行");
        tvUpdateTime.setText("02月07日更新");

        List<String> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add("sssssss" + i);
        }
        mAdapter.setNewData(list);
    }
}
