package com.huli.foxread.ui.adapters;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.GlideApp;
import com.huli.foxread.R;
import com.huli.foxread.entity.BookEntity2;

import androidx.annotation.NonNull;

public class BookCoverNameAdapter extends BaseQuickAdapter<BookEntity2, BaseViewHolder> {

    public BookCoverNameAdapter() {
        super(R.layout.recy_grid_item_book_cover_name);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, BookEntity2 data) {
        holder.setText(R.id.tv_book_name, data.getName());
        GlideApp.with(getContext())
                .load(data.getHttp_image())
                .placeholder(R.mipmap.img_holder_rect)
                .error(R.mipmap.img_holder_rect)
                .into((ImageView) holder.getView(R.id.iv_book_cover));
    }
}
