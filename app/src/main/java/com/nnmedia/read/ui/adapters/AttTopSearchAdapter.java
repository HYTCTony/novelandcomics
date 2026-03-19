package com.nnmedia.read.ui.adapters;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.nnmedia.novel.R;
import com.nnmedia.read.entity.BookEntity;
import com.nnmedia.read.utils.FigureProcessor;
import com.nnmedia.read.utils.GlideUtil;

import java.util.List;

import androidx.annotation.NonNull;

public class AttTopSearchAdapter extends BaseQuickAdapter<BookEntity, BaseViewHolder> {

    public AttTopSearchAdapter() {
        super(R.layout.recy_grid_item_att_top_search);
    }

    public AttTopSearchAdapter(List<BookEntity> data) {
        super(R.layout.recy_grid_item_att_top_search, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, BookEntity book) {
        GlideUtil.loadRoundRect(getContext(), helper.getView(R.id.iv_book_cover), book.getHttp_image());
        helper.setText(R.id.tv_book_name, book.getName());
        helper.setText(R.id.tv_search_count, String.format(getContext().getString(R.string.txt_search_count_x), FigureProcessor.formatNum(getContext(), book.getNumber())));
    }
}
