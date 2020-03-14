package com.huli.foxread.ui.adapters;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.R;

import androidx.annotation.NonNull;

public class GoldCoinDetailAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public GoldCoinDetailAdapter() {
        super(R.layout.recy_list_item_gold_coin_dt);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, String s) {
        holder.setText(R.id.tv_title_gc_dt, "每日30秒计时累计奖励");
        holder.setText(R.id.tv_time_gc_dt, "2020-02-12 13:15:33");
        holder.setText(R.id.tv_gold_coin_count_gc_dt, "+50元");
    }
}
