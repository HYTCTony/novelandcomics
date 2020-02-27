package com.huli.foxread.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.R;
import com.huli.foxread.listeners.OnClickEvent;
import com.huli.foxread.ui.base.BaseFragment;
import com.huli.foxread.ui.decoration.GridSpacingItemDecoration;
import com.huli.foxread.utils.DensityUtils;
import com.huli.foxread.utils.GlideUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ExWorksShowFargment extends BaseFragment {

    private int mType;

    private RecyclerView recyclerView;
    private MyAdapter mAdapter;

    private TextView btnAll;

    public static ExWorksShowFargment newInstance(int type) {
        Bundle bundle = new Bundle();
        bundle.putInt("sssss", type);
        ExWorksShowFargment mFragment = new ExWorksShowFargment();
        mFragment.setArguments(bundle);
        return mFragment;
    }

    @Override
    public int bindLayout() {
        return R.layout.fragment_excellent_works_show;
    }

    @Override
    public void setStatusBar(View view) {

    }

    @Override
    public void initView(View view) {
        recyclerView = $(view, R.id.recyclerView_excellent_works_show);
        recyclerView.setLayoutManager(new GridLayoutManager(mActivity, 4));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(4, DensityUtils.dp2px(mActivity, 12), true));
        mAdapter = new MyAdapter();
        recyclerView.setAdapter(mAdapter);

        btnAll = $(view, R.id.tv_asBtn_excellent_works_show_all);

    }

    @Override
    public void setListener() {
        btnAll.setOnClickListener(new OnClickEvent() {
            @Override
            public void singleClick(View v) {

            }
        });
    }

    @Override
    public void doBusiness(Context mContext) {
        mType = getArguments().getInt("sssss");

        btnAll.setText("全部现代言情");

        List<String> list = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            list.add("sssssssssss");
        }
        mAdapter.setNewData(list);
    }


    private class MyAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

        public MyAdapter() {
            super(R.layout.recy_grid_item_count4_excellent_works);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder helper, String item) {
            GlideUtil.loadRoundRect(getContext(), helper.getView(R.id.iv_book_cover_cew), "url", 0);
            helper.setText(R.id.tv_book_name, "猎荒者猎荒者");
            helper.setText(R.id.tv_book_viewers_cur, "259万人在看");
        }
    }
}
