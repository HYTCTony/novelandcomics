package com.huli.foxread.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.TypeReference;
import com.huli.foxread.R;
import com.huli.foxread.callbacks.ookkggoo.LtbJsonCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.entity.RankBookEntity;
import com.huli.foxread.ui.adapters.RankingSubAdapter;
import com.huli.foxread.ui.base.BaseFragment;
import com.huli.foxread.utils.Tos;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RankingSubFeagment extends BaseFragment {

    private TextView tvExplain, tvUpdateTime;
    private RecyclerView recyclerView;
    private RankingSubAdapter mAdapter;

    private int typeBG;
    private int typeRank;

    public static RankingSubFeagment newInstance(int typeBG, int typeRank) {
        Bundle bundle = new Bundle();
        bundle.putInt(Consts.RANK_FORM, typeBG);
        bundle.putInt(Consts.TYPE, typeRank);
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
        Bundle bundle = getArguments();
        typeBG = bundle.getInt(Consts.RANK_FORM);
        typeRank = bundle.getInt(Consts.TYPE);

        tvExplain.setText("阅读次数排行");
        tvUpdateTime.setText("02月07日更新");

        reqDataFromNet(typeBG, typeRank);
    }

    private void reqDataFromNet(int typeBG, int typeRank) {
        OkGo.<LzyResponse<List<RankBookEntity>>>get(Consts.INDEX_RANKING_API)
                .params(Consts.RANK_FORM, typeBG)
                .params(Consts.TYPE, typeRank)
                .execute(new LtbJsonCallback<LzyResponse<List<RankBookEntity>>>((AppCompatActivity) mActivity, false,
                        new TypeReference<LzyResponse<List<RankBookEntity>>>() {
                        }) {
                    @Override
                    public void onSuccess(Response<LzyResponse<List<RankBookEntity>>> response) {
                        LzyResponse<List<RankBookEntity>> entity = response.body();
                        if (entity.error_code == 0) {
                            List<RankBookEntity> datas = entity.getData();
                            mAdapter.setNewData(datas);
                        } else {
                            Tos.showShort(mActivity, entity.msg);
                        }
                    }

                    @Override
                    public void onCacheSuccess(Response<LzyResponse<List<RankBookEntity>>> response) {
                        super.onCacheSuccess(response);
                        onSuccess(response);
                    }
                });
    }

}
