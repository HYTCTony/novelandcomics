package com.huli.foxread.ui.adapters;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.GlideApp;
import com.huli.foxread.R;
import com.huli.foxread.contact.Common;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.entity.WelfareTaskEntity;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class WelfareMissionAdapter extends BaseQuickAdapter<WelfareTaskEntity, BaseViewHolder> {

    public WelfareMissionAdapter() {
        super(R.layout.recy_list_item_reading_mission);
        addChildClickViewIds(R.id.btn_welfare_mission_action);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, WelfareTaskEntity data) {
        holder.setText(R.id.tv_welfare_mission_title, data.getName());
        if (data.getReward() > 0) {
            holder.setText(R.id.tv_reward_of_mission, String.format(getContext().getString(R.string.txt_add_gold_coin_x), data.getReward()));
        } else {
            holder.setGone(R.id.tv_reward_of_mission, true);
        }
        holder.setText(R.id.tv_welfare_mission_explain, setNumColor(getContext(), data.getContent()));

        /*if (data.getLink().equals(Consts.INVITATION)) {
            holder.setVisible(R.id.btn_welfare_mission_action, true);
            holder.setText(R.id.btn_welfare_mission_action, "去邀请");
            holder.setTextColorRes(R.id.btn_welfare_mission_action, R.color.txt_red);
            holder.setBackgroundResource(R.id.btn_welfare_mission_action, R.drawable.shape_btn_bg_semicircle_border_red);
        } else if (data.getLink().equals(Consts.BE_INVITATION)) {
            holder.setVisible(R.id.btn_welfare_mission_action, true);
            holder.setText(R.id.btn_welfare_mission_action, "去填写");
            holder.setTextColorRes(R.id.btn_welfare_mission_action, R.color.txt_red);
            holder.setBackgroundResource(R.id.btn_welfare_mission_action, R.drawable.shape_btn_bg_semicircle_border_red);
        } else if (data.getLink().equals(Consts.EVERYDAY_READING)) {
            holder.setVisible(R.id.btn_welfare_mission_action, true);
            holder.setTextColorRes(R.id.btn_welfare_mission_action, R.color.txt_white);
            holder.setText(R.id.btn_welfare_mission_action, R.string.txt_go2_reading);
            holder.setBackgroundResource(R.id.btn_welfare_mission_action, R.drawable.ripple_semicircle_btn_gradual_bg_red);
        } else {
            holder.setGone(R.id.btn_welfare_mission_action, true);
        }*/
        if (data.getLink().equals(Common.INTENT_ACTION_INVITE_FRIENDS)) {
            holder.setVisible(R.id.btn_welfare_mission_action, true);
            holder.setText(R.id.btn_welfare_mission_action, R.string.txt_go2_invite);
            holder.setTextColorRes(R.id.btn_welfare_mission_action, R.color.txt_red);
            holder.setBackgroundResource(R.id.btn_welfare_mission_action, R.drawable.shape_btn_bg_semicircle_border_red);
        } else if (data.getLink().equals(Common.INTENT_ACTION_FILL_INVITECODE)) {
            holder.setVisible(R.id.btn_welfare_mission_action, true);
            holder.setText(R.id.btn_welfare_mission_action, R.string.txt_go2_fill_in);
            holder.setTextColorRes(R.id.btn_welfare_mission_action, R.color.txt_red);
            holder.setBackgroundResource(R.id.btn_welfare_mission_action, R.drawable.shape_btn_bg_semicircle_border_red);
        } else if (data.getLink().equals(Common.SWITCH2_BOOKSTORE)) {
            holder.setVisible(R.id.btn_welfare_mission_action, true);
            holder.setTextColorRes(R.id.btn_welfare_mission_action, R.color.txt_white);
            holder.setText(R.id.btn_welfare_mission_action, R.string.txt_go2_reading);
            holder.setBackgroundResource(R.id.btn_welfare_mission_action, R.drawable.ripple_semicircle_btn_gradual_bg_red);
        } else {
            holder.setGone(R.id.btn_welfare_mission_action, true);
        }

        GlideApp.with(getContext())
                .load(data.getHttp_logo_image())
                .into((ImageView) holder.getView(R.id.iv_mission_icon));
    }


    private static SpannableStringBuilder setNumColor(Context context, String str) {
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
