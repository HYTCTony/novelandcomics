package com.huli.foxread.ui.adapters;

import androidx.core.content.ContextCompat;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import androidx.annotation.NonNull;
import com.huli.foxread.R;
import com.huli.foxread.utils.GlideUtil;

public class HotTodayAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public HotTodayAdapter() {
        super(R.layout.recy_grid_item_hot_today);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, String item) {
        GlideUtil.loadRoundRect(getContext(), helper.getView(R.id.iv_book_cover), "url", 0);
        helper.setText(R.id.tv_book_title, "猎能者猎能者猎能者");
        helper.setText(R.id.tv_book_heat_rate, "2895万热度");
        TextView tvRank = helper.getView(R.id.tv_ranking_hot);
        tvRank.setText(String.valueOf(helper.getLayoutPosition() + 1));
        if (helper.getLayoutPosition() < 3) {
            tvRank.setTextColor(ContextCompat.getColor(getContext(), R.color.txt_orange_ff6600));
        } else {
            tvRank.setTextColor(ContextCompat.getColor(getContext(), R.color.txt_black));
        }
    }
}
