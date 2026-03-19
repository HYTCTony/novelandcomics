package com.nnmedia.read.ui.adapters;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.nnmedia.novel.R;
import com.nnmedia.read.entity.MineWelfareZoneEntity;
import com.nnmedia.read.utils.GlideUtil;

import androidx.annotation.NonNull;

public class WelfareZoneMineAdapter extends BaseQuickAdapter<MineWelfareZoneEntity, BaseViewHolder> {

    public WelfareZoneMineAdapter() {
        super(R.layout.recy_h_list_item_welfare_zone_mine);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, MineWelfareZoneEntity data) {
        View contentView = helper.getView(R.id.ctl_welfare_zone);
        try {
//            GradientDrawable aDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{Color.parseColor(item.getColor_l()), Color.parseColor(item.getColor_r())});
            GradientDrawable aDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{Color.parseColor(data.getGradation_start()), Color.parseColor(data.getGradation_end())});
            contentView.setBackground(aDrawable);
        } catch (Exception e) {
        }

        helper.setText(R.id.tv_title_welfare, data.getName());
        helper.setText(R.id.tv_people_number_of_get_welfare, data.getNumber() + "人已领");

        GlideUtil.loadRoundSquare(getContext(), helper.getView(R.id.iv_welfare_type_icon), data.getHttp_image());
    }
}
