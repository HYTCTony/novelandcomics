package com.huli.foxread.ui.adapters;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.GlideApp;
import com.huli.foxread.R;
import com.huli.foxread.entity.WelfareReadTaskEntity;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class WelfareReadMissionAdapter extends BaseQuickAdapter<WelfareReadTaskEntity, BaseViewHolder> {

    public WelfareReadMissionAdapter() {
        super(R.layout.recy_list_item_reading_mission);
        addChildClickViewIds(R.id.btn_welfare_mission_action);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, WelfareReadTaskEntity data) {
        holder.setText(R.id.tv_welfare_mission_title, data.getName());

        int reward = data.getReward();
        if (reward > 0) {
            String subTaskId = data.getSubTaskId();
            if (TextUtils.isEmpty(subTaskId)) {
                int frequency = data.getFrequency();        //累计阅读时长任务可完成总次数
                if (frequency > 0) {
                    holder.setText(R.id.tv_reward_of_mission, String.format(getContext().getString(R.string.txt_most_gold_coin_x), reward * frequency));
                }
            } else {
                holder.setText(R.id.tv_reward_of_mission, String.format(getContext().getString(R.string.txt_add_gold_coin_x), reward));
            }
        } else {
            holder.setGone(R.id.tv_reward_of_mission, true);
        }
        holder.setText(R.id.tv_welfare_mission_explain, setNumColor(getContext(), data.getContent()));
//        holder.setText(R.id.btn_welfare_mission_action, R.string.txt_go2_reading);
        if (data.getState() == 0) {
            holder.setEnabled(R.id.btn_welfare_mission_action, true);
            holder.setTextColorRes(R.id.btn_welfare_mission_action, R.color.txt_red);
            holder.setText(R.id.btn_welfare_mission_action, R.string.txt_go2_reading);
            holder.setBackgroundResource(R.id.btn_welfare_mission_action, R.drawable.shape_btn_bg_semicircle_border_red);
        } else if (data.getState() == 1) {
            holder.setEnabled(R.id.btn_welfare_mission_action, true);
            holder.setTextColorRes(R.id.btn_welfare_mission_action, R.color.txt_white);
            holder.setText(R.id.btn_welfare_mission_action, R.string.txt_can_receive);
            holder.setBackgroundResource(R.id.btn_welfare_mission_action, R.drawable.ripple_semicircle_btn_gradual_bg_red);
        } else {
            holder.setEnabled(R.id.btn_welfare_mission_action, false);
            holder.setTextColorRes(R.id.btn_welfare_mission_action, R.color.txt_black);
            holder.setText(R.id.btn_welfare_mission_action, R.string.txt_already_received);
            holder.setBackgroundResource(R.id.btn_welfare_mission_action, R.drawable.shape_btn_semicircle_bg_disabled);
        }

        GlideApp.with(getContext())
                .load(data.getHttp_logo_image())
                .into((ImageView) holder.getView(R.id.iv_mission_icon));
    }


    private static SpannableStringBuilder setNumColor(Context context, String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        SpannableStringBuilder style = new SpannableStringBuilder(str);
        for (int i = 0; i < str.length(); i++) {
            char a = str.charAt(i);
            if (a >= '0' && a <= '9') {
                style.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.txt_red)), i, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return style;
    }

}
