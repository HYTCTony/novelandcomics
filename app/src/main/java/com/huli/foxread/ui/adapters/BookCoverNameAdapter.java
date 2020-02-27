package com.huli.foxread.ui.adapters;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.GlideApp;
import com.huli.foxread.R;

import androidx.annotation.NonNull;

public class BookCoverNameAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public BookCoverNameAdapter() {
        super(R.layout.recy_grid_item_book_cover_name);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, String s) {
        holder.setText(R.id.tv_book_name, "赖上甜甜冰淇淋");
        GlideApp.with(getContext())
                .load("url")
                .placeholder(R.mipmap.img_holder_rect)
                .error(R.mipmap.img_holder_rect)
                .into((ImageView) holder.getView(R.id.iv_book_cover));
    }
}
