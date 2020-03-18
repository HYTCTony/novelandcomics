package com.huli.foxread.ui.adapters;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.R;
import com.huli.foxread.entity.BookEntity;
import com.huli.foxread.entity.SearchEntity;
import com.huli.foxread.utils.GlideUtil;

import java.util.List;

import androidx.annotation.NonNull;

public class AttTopSearchAdapter extends BaseQuickAdapter<SearchEntity, BaseViewHolder> {

    public AttTopSearchAdapter() {
        super(R.layout.recy_grid_item_att_top_search);
    }

    public AttTopSearchAdapter(List<SearchEntity> data) {
        super(R.layout.recy_grid_item_att_top_search, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, SearchEntity item) {
        GlideUtil.loadRoundRect(getContext(), helper.getView(R.id.iv_book_cover), item.getProfile_novel().getHttp_image(), 0);
        helper.setText(R.id.tv_book_name, item.getProfile_novel().getName());
        helper.setText(R.id.tv_search_count, String.format(getContext().getString(R.string.txt_search_count_x), item.getNumber()));
    }
}
