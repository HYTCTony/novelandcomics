package com.huli.foxread.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.R;
import com.huli.foxread.contact.Common;
import com.huli.foxread.entity.BookEntity;
import com.huli.foxread.entity.HpClassifyNvET;
import com.huli.foxread.listeners.OnClickEvent;
import com.huli.foxread.ui.activities.BookDetailsActivity;
import com.huli.foxread.ui.base.BaseFragment;
import com.huli.foxread.ui.decoration.GridSpacingItemDecoration;
import com.huli.foxread.utils.DensityUtils;
import com.huli.foxread.utils.GlideUtil;
import com.huli.foxread.utils.Tos;
import com.huli.foxread.utils.UnitConverUtil;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ExWorksShowFargment extends BaseFragment implements OnItemClickListener {
    private static final String EXTRA_KEY = "Hp_ClassifyNv_ET";

    private MyAdapter mAdapter;

    private TextView btnAll;

    public static ExWorksShowFargment newInstance(HpClassifyNvET data) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_KEY, data);
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
        RecyclerView recyclerView = $(view, R.id.recyclerView_excellent_works_show);
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
                Tos.showShort(mActivity, btnAll.getText());
            }
        });

        mAdapter.setOnItemClickListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            HpClassifyNvET data = (HpClassifyNvET) bundle.getSerializable(EXTRA_KEY);
            if (data != null) {
                btnAll.setText(data.getName());
                List<BookEntity> novels = data.getNovel();
                mAdapter.setNewData(novels);
            }
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        BookEntity entity = mAdapter.getData().get(position);
        Intent intent = new Intent(mActivity, BookDetailsActivity.class);
        intent.putExtra(Common.KEY_BOOK_ID, entity.getId());
        startActivity(intent);
    }


    private class MyAdapter extends BaseQuickAdapter<BookEntity, BaseViewHolder> {

        public MyAdapter() {
            super(R.layout.recy_grid_item_count4_excellent_works);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder helper, BookEntity item) {
            GlideUtil.loadRoundRect(getContext(), helper.getView(R.id.iv_book_cover_cew), item.getHttp_image(), 0);
            helper.setText(R.id.tv_book_name, item.getName());
            helper.setText(R.id.tv_book_viewers_cur, UnitConverUtil.formatNum(getContext(), item.getRead_sum()) + getString(R.string.txt_book_watching_w));
        }
    }
}
