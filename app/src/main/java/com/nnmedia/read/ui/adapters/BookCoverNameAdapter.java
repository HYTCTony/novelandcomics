package com.nnmedia.read.ui.adapters;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.nnmedia.novel.R;
import com.nnmedia.read.GlideApp;
import com.nnmedia.read.entity.BookEntity;

import androidx.annotation.NonNull;

/**
 * 封面  书名
 */
public class BookCoverNameAdapter extends BaseQuickAdapter<BookEntity, BaseViewHolder> {

    public BookCoverNameAdapter() {
        super(R.layout.recy_grid_item_book_cover_name);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, BookEntity data) {
        holder.setText(R.id.tv_book_name, data.getName());
        GlideApp.with(getContext())
                .load(data.getHttp_image())
                .placeholder(R.mipmap.img_holder_rect)
                .error(R.mipmap.img_holder_rect)
                .into((ImageView) holder.getView(R.id.iv_book_cover));
    }
}
