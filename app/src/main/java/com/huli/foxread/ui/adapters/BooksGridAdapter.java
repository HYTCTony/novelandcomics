package com.huli.foxread.ui.adapters;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.R;
import com.huli.foxread.entity.ExclusiveBookEntity;
import com.huli.foxread.utils.GlideUtil;

import androidx.annotation.NonNull;

public class BooksGridAdapter extends BaseQuickAdapter<ExclusiveBookEntity, BaseViewHolder> {

    public BooksGridAdapter() {
        super(R.layout.recy_grid_item_book_normal);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, ExclusiveBookEntity data) {
        GlideUtil.loadRoundRect(getContext(), helper.getView(R.id.iv_book_cover), data.getProfileNovel().getHttp_image(), 0);
        helper.setText(R.id.tv_book_name, data.getProfileNovel().getName());
        helper.setText(R.id.tv_authorName, data.getProfileNovel().getAuthor());
    }
}
