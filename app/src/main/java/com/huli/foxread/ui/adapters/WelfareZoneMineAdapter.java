package com.huli.foxread.ui.adapters;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.R;
import com.huli.foxread.utils.GlideUtil;

import androidx.annotation.NonNull;

public class WelfareZoneMineAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public WelfareZoneMineAdapter() {
        super(R.layout.recy_h_list_item_welfare_zone_mine);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, String item) {
        View contentView = helper.getView(R.id.ctl_welfare_zone);
        try {
//            GradientDrawable aDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{Color.parseColor(item.getColor_l()), Color.parseColor(item.getColor_r())});
            GradientDrawable aDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{Color.parseColor("#FC4545"), Color.parseColor("#FC6D6D")});
            contentView.setBackground(aDrawable);
        } catch (Exception e) {
        }

        helper.setText(R.id.tv_title_welfare, "新人提现（1元）");
        helper.setText(R.id.tv_people_number_of_get_welfare, "123456已领");

        GlideUtil.loadRoundSquare(getContext(), helper.getView(R.id.iv_welfare_type_icon), "url");
    }
}
