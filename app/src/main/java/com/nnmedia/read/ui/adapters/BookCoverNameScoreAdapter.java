package com.nnmedia.read.ui.adapters;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.nnmedia.novel.R;
import com.nnmedia.read.entity.BookEntity;
import com.nnmedia.read.utils.GlideUtil;

import java.util.List;

import androidx.annotation.NonNull;

/**
 * 封面  书名  分数
 */
public class BookCoverNameScoreAdapter extends BaseQuickAdapter<BookEntity, BaseViewHolder> {

    public BookCoverNameScoreAdapter() {
        super(R.layout.recy_grid_item_book_cover_name_score);
    }

    public BookCoverNameScoreAdapter(List<BookEntity> datas) {
        super(R.layout.recy_grid_item_book_cover_name_score, datas);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, BookEntity data) {
        holder.setText(R.id.tv_book_name, data.getName());
        holder.setText(R.id.tv_book_score, String.valueOf(data.getScore()));
        GlideUtil.loadRoundRect(getContext(), holder.getView(R.id.iv_book_cover), data.getHttp_image());
    }
}
