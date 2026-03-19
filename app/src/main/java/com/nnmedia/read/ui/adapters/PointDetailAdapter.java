package com.nnmedia.read.ui.adapters;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.nnmedia.novel.R;
import com.nnmedia.read.entity.GoldExpenditureBean;
import com.nnmedia.read.utils.DateTimeUtil;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class PointDetailAdapter extends BaseQuickAdapter<GoldExpenditureBean, BaseViewHolder> implements LoadMoreModule {

    public PointDetailAdapter() {
        super(R.layout.recy_list_item_gold_coin_dt);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, GoldExpenditureBean data) {
        holder.setText(R.id.tv_title_gc_dt, data.getMemo());
        holder.setText(R.id.tv_time_gc_dt, DateTimeUtil.formatDateTime(data.getCreatetime() * 1000));
        int goldCoinCount = data.getScore();
        if (goldCoinCount >= 0) {
            holder.setTextColor(R.id.tv_gold_coin_count_gc_dt, ContextCompat.getColor(getContext(), R.color.txt_red));
            holder.setText(R.id.tv_gold_coin_count_gc_dt, String.format(getContext().getString(R.string.txt_add_point_x), goldCoinCount));
        } else {
            holder.setTextColor(R.id.tv_gold_coin_count_gc_dt, ContextCompat.getColor(getContext(), R.color.green));
            holder.setText(R.id.tv_gold_coin_count_gc_dt, String.format(getContext().getString(R.string.txt_add_point_x), goldCoinCount));
            holder.setText(R.id.tv_gold_coin_count_gc_dt, String.format(getContext().getString(R.string.txt_del_point_x), Math.abs(goldCoinCount)));
        }
    }
}
