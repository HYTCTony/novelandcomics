package com.huli.foxread.ui.adapters;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.R;
import com.huli.foxread.entity.CategoryEntity;
import com.huli.foxread.utils.GlideUtil;

import androidx.annotation.NonNull;

public class ClassifyAdapter extends BaseQuickAdapter<CategoryEntity, BaseViewHolder> {

    public ClassifyAdapter() {
        super(R.layout.recy_grid_item_book_classify);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, CategoryEntity item) {
        holder.setText(R.id.tv_book_classify_type_title, item.getName());
        holder.setText(R.id.tv_book_classify_number_total, item.getNovel_sum() + "部");
        GlideUtil.loadRoundRect(getContext(), holder.getView(R.id.iv_book_classify_type), item.getHttp_image(), 0);
    }
}
