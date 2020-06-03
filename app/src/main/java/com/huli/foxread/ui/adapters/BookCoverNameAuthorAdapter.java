package com.huli.foxread.ui.adapters;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.GlideApp;
import com.huli.foxread.R;
import com.huli.foxread.entity.BookEntity;
import com.huli.foxread.utils.GlideUtil;

import java.util.List;

import androidx.annotation.NonNull;

/**
 * 封面  书名
 */
public class BookCoverNameAuthorAdapter extends BaseQuickAdapter<BookEntity, BaseViewHolder> {

    public BookCoverNameAuthorAdapter() {
        super(R.layout.recy_grid_item_book_cover_name_author);
    }

    public BookCoverNameAuthorAdapter(List<BookEntity> data) {
        super(R.layout.recy_grid_item_book_cover_name_author, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, BookEntity data) {
        holder.setText(R.id.tv_book_name, data.getName());
        holder.setText(R.id.tv_authorName, data.getAuthor());
        GlideUtil.loadRoundRect(getContext(), holder.getView(R.id.iv_book_cover), data.getHttp_image());
    }
}
