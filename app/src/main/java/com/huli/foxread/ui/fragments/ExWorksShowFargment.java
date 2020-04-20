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
import com.huli.foxread.entity.GemEntity;
import com.huli.foxread.entity.GemGroupEntity;
import com.huli.foxread.listeners.OnClickEvent;
import com.huli.foxread.ui.activities.BookDetailsActivity;
import com.huli.foxread.ui.base.BaseFragment;
import com.huli.foxread.ui.decoration.GridSpacingItemDecoration;
import com.huli.foxread.utils.DensityUtils;
import com.huli.foxread.utils.FigureProcessor;
import com.huli.foxread.utils.GlideUtil;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ExWorksShowFargment extends BaseFragment implements OnItemClickListener {
    private static final String EXTRA_KEY = "Hp_ClassifyNv_ET";

    private MyAdapter mAdapter;

    private TextView btnAll;

    public static ExWorksShowFargment newInstance(GemGroupEntity data) {
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
//                Tos.showShort(mActivity, btnAll.getText());
            }
        });

        mAdapter.setOnItemClickListener(this);
    }

    @Override
    public void doBusiness(Context mContext) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            GemGroupEntity data = (GemGroupEntity) bundle.getSerializable(EXTRA_KEY);
            if (data != null) {
                btnAll.setText(data.getName());
                List<GemEntity> novels = data.getProfile_novel_masterpiece();
                if (novels.size() > 4) {
                    mAdapter.setNewData(novels.subList(0, 4));
                } else {
                    mAdapter.setNewData(novels);
                }
            }
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (onMoreClick()) {
            return;
        }
        GemEntity entity = mAdapter.getData().get(position);
        Intent intent = new Intent(mActivity, BookDetailsActivity.class);
        intent.putExtra(Common.KEY_BOOK_ID, entity.getId());
        startActivity(intent);
    }


    private class MyAdapter extends BaseQuickAdapter<GemEntity, BaseViewHolder> {

        public MyAdapter() {
            super(R.layout.recy_grid_item_count4_excellent_works);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder helper, GemEntity item) {
            GlideUtil.loadRoundRect(getContext(), helper.getView(R.id.iv_book_cover_cew), item.getHttp_image(), 0);
            helper.setText(R.id.tv_book_name, item.getNovel_name());
            helper.setText(R.id.tv_book_viewers_cur, FigureProcessor.formatNum(getContext(), item.getReading_size()) + getString(R.string.txt_book_watching));
        }
    }
}
