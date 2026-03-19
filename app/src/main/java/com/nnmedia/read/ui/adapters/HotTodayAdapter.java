package com.nnmedia.read.ui.adapters;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.nnmedia.novel.R;
import com.nnmedia.read.entity.BookEntity;
import com.nnmedia.read.utils.DensityUtils;
import com.nnmedia.read.utils.FigureProcessor;
import com.nnmedia.read.utils.GlideUtil;

import java.util.List;

import androidx.annotation.NonNull;

/**
 * 今日大热榜
 */
public class HotTodayAdapter extends BaseQuickAdapter<BookEntity, BaseViewHolder> {

    public HotTodayAdapter() {
        super(R.layout.recy_grid_item_hot_today);
    }

    public HotTodayAdapter(List<BookEntity> data) {
        super(R.layout.recy_grid_item_hot_today, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, BookEntity item) {
        GlideUtil.loadRoundRect(getContext(), helper.getView(R.id.iv_book_cover), item.getHttp_image(), DensityUtils.dp2px(getContext(), 2));
        helper.setText(R.id.tv_book_title, item.getName());
        helper.setText(R.id.tv_book_heat_rate, FigureProcessor.formatHeat(getContext(), item.getHeat()));
        String rankStr = String.valueOf(helper.getLayoutPosition() + 1);
        helper.setText(R.id.tv_flag_rank, rankStr);
        if (helper.getLayoutPosition() < 3) {
            helper.setBackgroundResource(R.id.tv_flag_rank, R.mipmap.icon_rank_top3_txtbg);
        } else {
            helper.setBackgroundResource(R.id.tv_flag_rank, R.mipmap.icon_rank_normal_txtbg);
        }
    }
}
