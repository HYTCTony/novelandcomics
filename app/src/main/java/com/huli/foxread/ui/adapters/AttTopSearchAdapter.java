package com.huli.foxread.ui.adapters;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.R;
import com.huli.foxread.utils.GlideUtil;

import androidx.annotation.NonNull;

public class AttTopSearchAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public AttTopSearchAdapter() {
        super(R.layout.recy_grid_item_att_top_search);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, String item) {
        GlideUtil.loadRoundRect(getContext(), helper.getView(R.id.iv_book_cover), "url", 0);
        helper.setText(R.id.tv_book_name, "猎能者猎能者猎能者");
        helper.setText(R.id.tv_search_count, String.format(getContext().getString(R.string.txt_search_count_x), 6498));
    }
}
