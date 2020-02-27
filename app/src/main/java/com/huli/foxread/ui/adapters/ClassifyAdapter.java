package com.huli.foxread.ui.adapters;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.R;
import com.huli.foxread.utils.GlideUtil;

import androidx.annotation.NonNull;

public class ClassifyAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public ClassifyAdapter() {
        super(R.layout.recy_grid_item_book_classify);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, String s) {
        holder.setText(R.id.tv_book_classify_type_title, "现代言情");
        holder.setText(R.id.tv_book_classify_number_total, "265665部");
        GlideUtil.loadRoundRect(getContext(), holder.getView(R.id.iv_book_classify_type), "url", 0);
    }
}
