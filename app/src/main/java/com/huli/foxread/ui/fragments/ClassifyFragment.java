package com.huli.foxread.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.TypeReference;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.huli.foxread.R;
import com.huli.foxread.callbacks.ookkggoo.LtbJsonCallback;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Common;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.entity.CategoryEntity;
import com.huli.foxread.ui.activities.ClassifyDetailActivity;
import com.huli.foxread.ui.adapters.ClassifyAdapter;
import com.huli.foxread.ui.base.BaseFragment;
import com.huli.foxread.ui.decoration.GridSpacingItemDecoration;
import com.huli.foxread.utils.DensityUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ClassifyFragment extends BaseFragment implements OnItemClickListener {

    private RecyclerView recyclerView;
    private ClassifyAdapter mAdapter;

    private TextView tvTotal;

    private int pid;
    private int totalNum = 0;

    public static ClassifyFragment newInstance(int pid, int totalNum) {
        Bundle bundle = new Bundle();
        bundle.putInt(Common.KEY_CAT_PID, pid);
        bundle.putInt(Common.KEY_CAT_TOTAL_BOOK, totalNum);
        ClassifyFragment frag = new ClassifyFragment();
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public int bindLayout() {
        return R.layout.fragment_classify;
    }

    @Override
    public void setStatusBar(View view) {

    }

    @Override
    public void initView(View view) {
        recyclerView = $(view, R.id.recyclerView_book_classify_type);
        recyclerView.setLayoutManager(new GridLayoutManager(mActivity, 2));
        mAdapter = new ClassifyAdapter();
        recyclerView.setAdapter(mAdapter);

        View headView = LayoutInflater.from(mActivity).inflate(R.layout.layout_rv_head_book_classify, recyclerView, false);
        mAdapter.addHeaderView(headView);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, DensityUtils.dp2px(mActivity, 12), true, 1));

        tvTotal = $(headView, R.id.tv_books_total);
    }

    @Override
    public void setListener() {
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {
        Bundle bundle = getArguments();
        pid = bundle.getInt(Common.KEY_CAT_PID);
        totalNum = bundle.getInt(Common.KEY_CAT_TOTAL_BOOK);
        tvTotal.setText(String.format(getString(R.string.txt_total_books_x), totalNum));

        reqSubCategory(pid);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        CategoryEntity entity = mAdapter.getData().get(position);
        Intent intent = new Intent(mActivity, ClassifyDetailActivity.class);
        intent.putExtra(Common.KEY_CAT_ID, entity.getId());
        intent.putExtra(Common.KEY_CAT_TITLE, entity.getName());
        startActivity(intent);
    }


    /**
     * 子分类
     */
    private void reqSubCategory(int pid) {
        OkGo.<LzyResponse<List<CategoryEntity>>>post(Consts.NOVEL_CATEGORY_SUB_API)
                .cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)
                .cacheTime(60 * 60 * 1000)
                .params(Consts.CAT_PID, pid)
                .execute(new LtbJsonCallback<LzyResponse<List<CategoryEntity>>>((AppCompatActivity) mActivity, false,
                        new TypeReference<LzyResponse<List<CategoryEntity>>>() {
                        }) {
                    @Override
                    public void onSuccess(Response<LzyResponse<List<CategoryEntity>>> response) {
                        LzyResponse<List<CategoryEntity>> entity = response.body();
                        if (entity.error_code == 0) {
                            List<CategoryEntity> datas = entity.getData();
                            mAdapter.setNewData(datas);
                        }
                    }

                    @Override
                    public void onCacheSuccess(Response<LzyResponse<List<CategoryEntity>>> response) {
                        super.onCacheSuccess(response);
                        onSuccess(response);
                    }
                });
    }
}
