package com.huli.foxread.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.huli.foxread.R;
import com.huli.foxread.ui.activities.ClassifyDetailActivity;
import com.huli.foxread.ui.adapters.ClassifyAdapter;
import com.huli.foxread.ui.base.BaseFragment;
import com.huli.foxread.ui.decoration.GridSpacingItemDecoration;
import com.huli.foxread.utils.DensityUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ClassifyFragment extends BaseFragment implements OnItemClickListener {

    private RecyclerView recyclerView;
    private ClassifyAdapter mAdapter;

    private int mType;

    public static ClassifyFragment newInstance(int type) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
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
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, DensityUtils.dp2px(mActivity, 16), true, 1));

        TextView tvTotal = $(headView, R.id.tv_books_total);
        tvTotal.setText(String.format(getString(R.string.txt_total_books_x), 456789));
    }

    @Override
    public void setListener() {
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {
        mType = getArguments().getInt("type");

        List<String> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add("sssssssss" + i);
        }
        mAdapter.setNewData(list);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        startActivity(new Intent(mActivity, ClassifyDetailActivity.class));
    }
}
