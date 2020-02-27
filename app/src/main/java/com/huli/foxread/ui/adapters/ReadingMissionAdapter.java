package com.huli.foxread.ui.adapters;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.R;

import androidx.annotation.NonNull;

public class ReadingMissionAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public ReadingMissionAdapter() {
        super(R.layout.recy_list_item_reading_mission);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, String s) {
        holder.setText(R.id.tv_reading_mission_title, "阅读5分钟");
        holder.setText(R.id.tv_reward_of_mission, "+100金币");
        holder.setText(R.id.tv_reading_mission_explain, "完成阅读任务获得奖励");
        holder.setText(R.id.btn_reading_mission_action, R.string.txt_go2_reading);
        holder.setBackgroundResource(R.id.btn_reading_mission_action, R.drawable.shape_btn_bg_semicircle_border_red);
    }
}
