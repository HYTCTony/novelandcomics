package com.huli.foxread.ui.adapters;

import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.R;
import com.huli.foxread.entity.BookEntity;
import com.huli.foxread.utils.DensityUtils;
import com.huli.foxread.utils.GlideUtil;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class HotTodayAdapter extends BaseQuickAdapter<BookEntity, BaseViewHolder> {

    public HotTodayAdapter(List<BookEntity> data) {
        super(R.layout.recy_grid_item_hot_today, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, BookEntity item) {
        GlideUtil.loadRoundRect(getContext(), helper.getView(R.id.iv_book_cover), item.getHttp_image(), DensityUtils.dp2px(getContext(), 2));
        helper.setText(R.id.tv_book_title, item.getName());
        helper.setText(R.id.tv_book_heat_rate, item.getHot() + getContext().getString(R.string.txt_heat_wan));
        TextView tvRank = helper.getView(R.id.tv_ranking_hot);
        String rankStr = String.valueOf(helper.getLayoutPosition() + 1);
        tvRank.setText(rankStr);
        if (helper.getLayoutPosition() < 3) {
            tvRank.setTextColor(ContextCompat.getColor(getContext(), R.color.txt_orange_ff6600));
            helper.setText(R.id.tv_top3_flag, rankStr);
            helper.setVisible(R.id.tv_top3_flag, true);
        } else {
            tvRank.setTextColor(ContextCompat.getColor(getContext(), R.color.txt_black));
            helper.setGone(R.id.tv_top3_flag, true);
        }
    }
}
