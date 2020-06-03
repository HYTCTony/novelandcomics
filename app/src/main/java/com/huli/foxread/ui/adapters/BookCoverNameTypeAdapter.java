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
 * 封面  书名  归属分类
 */
public class BookCoverNameTypeAdapter extends BaseQuickAdapter<BookEntity, BaseViewHolder> {

    public BookCoverNameTypeAdapter() {
        super(R.layout.recy_grid_item_book_cover_type);
    }

    public BookCoverNameTypeAdapter(List<BookEntity> datas) {
        super(R.layout.recy_grid_item_book_cover_type, datas);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, BookEntity data) {
        holder.setText(R.id.tv_book_name, data.getName());
        holder.setText(R.id.tv_book_type, data.getClassify_name());
        GlideUtil.loadRoundRect(getContext(), holder.getView(R.id.iv_book_cover), data.getHttp_image());
    }
}
