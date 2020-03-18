package com.huli.foxread.ui.adapters;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.R;
import com.huli.foxread.entity.GoldExpenditureBean;
import com.huli.foxread.utils.DateTimeUtil;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class GoldCoinDetailAdapter extends BaseQuickAdapter<GoldExpenditureBean, BaseViewHolder> implements LoadMoreModule {

    public GoldCoinDetailAdapter() {
        super(R.layout.recy_list_item_gold_coin_dt);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, GoldExpenditureBean data) {
        holder.setText(R.id.tv_title_gc_dt, data.getMemo());
        holder.setText(R.id.tv_time_gc_dt, DateTimeUtil.formatDateTime(data.getCreatetime()));
        int goldCoinCount = data.getScore();
        if (goldCoinCount >= 0) {
            holder.setTextColor(R.id.tv_gold_coin_count_gc_dt, ContextCompat.getColor(getContext(), R.color.txt_red));
            holder.setText(R.id.tv_gold_coin_count_gc_dt, String.format(getContext().getString(R.string.txt_add_gold_coin_x), goldCoinCount));
        } else {
            holder.setTextColor(R.id.tv_gold_coin_count_gc_dt, ContextCompat.getColor(getContext(), R.color.green));
            holder.setText(R.id.tv_gold_coin_count_gc_dt, String.format(getContext().getString(R.string.txt_add_gold_coin_x), goldCoinCount));
            holder.setText(R.id.tv_gold_coin_count_gc_dt, String.format(getContext().getString(R.string.txt_del_gold_coin_x), Math.abs(goldCoinCount)));
        }
    }
}
