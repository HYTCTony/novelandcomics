package com.nnmedia.read.ui.adapters;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.nnmedia.novel.R;
import com.nnmedia.read.entity.RankBookEntity;
import com.nnmedia.read.utils.GlideUtil;

import androidx.annotation.NonNull;

public class SHotBooksAdapter extends BaseQuickAdapter<RankBookEntity, BaseViewHolder> implements LoadMoreModule {

    public SHotBooksAdapter() {
        super(R.layout.recy_list_item_search_hot_book);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, RankBookEntity item) {
        GlideUtil.loadRoundRect(getContext(), holder.getView(R.id.iv_book_cover), item.getHttp_image());
        holder.setText(R.id.tv_book_name, item.getNovel_name());
        holder.setText(R.id.tv_book_introduction, item.getIntroduce());
    }
}
