package com.huli.foxread.ui.adapters;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.R;
import com.huli.foxread.entity.HotSearchEntity;
import com.huli.foxread.entity.SearchEntity;
import com.huli.foxread.utils.FigureProcessor;
import com.huli.foxread.utils.GlideUtil;

import java.util.List;

import androidx.annotation.NonNull;

public class AttTopSearchAdapter extends BaseQuickAdapter<HotSearchEntity, BaseViewHolder> {

    public AttTopSearchAdapter() {
        super(R.layout.recy_grid_item_att_top_search);
    }

    public AttTopSearchAdapter(List<HotSearchEntity> data) {
        super(R.layout.recy_grid_item_att_top_search, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, HotSearchEntity item) {
        GlideUtil.loadRoundRect(getContext(), helper.getView(R.id.iv_book_cover), item.getHttp_image(), 0);
        helper.setText(R.id.tv_book_name, item.getNovel_name());
        helper.setText(R.id.tv_search_count, String.format(getContext().getString(R.string.txt_search_count_x), FigureProcessor.formatNum(getContext(), item.getNumber())));
    }
}
