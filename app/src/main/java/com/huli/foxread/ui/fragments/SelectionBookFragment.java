package com.huli.foxread.ui.fragments;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.huli.foxread.R;
import com.huli.foxread.ui.base.BaseFragment;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

public class SelectionBookFragment extends BaseFragment {

    private SmartRefreshLayout mRefreshLayout;
    private RecyclerView recyclerView;

    @Override
    public int bindLayout() {
        return R.layout.fragment_bookstore_selection_book;
    }

    @Override
    public void setStatusBar(View view) {

    }

    @Override
    public void initView(View view) {

    }

    @Override
    public void setListener() {

    }

    @Override
    public void doBusiness(Context mContext) {

    }
}
