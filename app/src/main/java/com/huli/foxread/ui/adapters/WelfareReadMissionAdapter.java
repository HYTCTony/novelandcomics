package com.huli.foxread.ui.adapters;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;

import com.baoyachi.stepview.HorizontalStepView;
import com.baoyachi.stepview.bean.StepBean;
import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.GlideApp;
import com.huli.foxread.R;
import com.huli.foxread.entity.MissionEntity;
import com.huli.foxread.entity.sections.MissionSection;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class WelfareReadMissionAdapter extends BaseSectionQuickAdapter<MissionSection<MissionEntity>, BaseViewHolder> {

    private boolean isVip = false;

    public void setVipMode(boolean vip) {
        isVip = vip;
    }

    public WelfareReadMissionAdapter() {
        super(R.layout.recy_section_head_view_welfare);
        setNormalLayout(R.layout.recy_list_item_reading_mission);
        addChildClickViewIds(R.id.btn_welfare_mission_action);
    }

    @Override
    protected void convertHeader(BaseViewHolder holder, MissionSection<MissionEntity> data) {
        holder.setText(R.id.tv_section_group_title, data.getTitle());
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, MissionSection<MissionEntity> data) {
        MissionEntity missionEntity = data.getObject();
        holder.setText(R.id.btn_welfare_mission_action, missionEntity.getProgress());
        holder.setText(R.id.tv_welfare_mission_title, missionEntity.getType_name());
        holder.setText(R.id.tv_welfare_mission_explain, setNumColor(getContext(), missionEntity.getContent()));
        int reward;
        if (isVip) {
            reward = missionEntity.getVip_reward();
        } else {
            reward = missionEntity.getReward();
        }
        holder.setText(R.id.tv_reward_of_mission, String.format(getContext().getString(R.string.txt_add_gold_coin_x), reward));

        if (missionEntity.getStatus() == 0) {
            holder.setEnabled(R.id.btn_welfare_mission_action, true);
            holder.setTextColorRes(R.id.btn_welfare_mission_action, R.color.txt_red);
            holder.setBackgroundResource(R.id.btn_welfare_mission_action, R.drawable.shape_btn_bg_semicircle_border_red);
        } else if (missionEntity.getStatus() == 1) {
            holder.setEnabled(R.id.btn_welfare_mission_action, true);
            holder.setTextColorRes(R.id.btn_welfare_mission_action, R.color.txt_white);
            holder.setBackgroundResource(R.id.btn_welfare_mission_action, R.drawable.ripple_semicircle_btn_gradual_bg_red);
        } else {
            holder.setEnabled(R.id.btn_welfare_mission_action, false);
            holder.setTextColorRes(R.id.btn_welfare_mission_action, R.color.txt_black);
            holder.setBackgroundResource(R.id.btn_welfare_mission_action, R.drawable.shape_btn_semicircle_bg_disabled);
        }

        GlideApp.with(getContext())
                .load(missionEntity.getHttp_logo_image())
                .error(R.drawable.ic_clock_red)
                .into((ImageView) holder.getView(R.id.iv_mission_icon));




        HorizontalStepView setpview = holder.getView(R.id.step_view_newbie_mission);
        int signSuccCount = missionEntity.getSign_successions();
        if (signSuccCount > 0 && signSuccCount <= 7) {
            setpview.setVisibility(View.VISIBLE);
            List<StepBean> stepsBeanList = new ArrayList<>();
            for (int i = 1; i <= 7; i++) {
                if (i < signSuccCount) {
                    StepBean stepBean = new StepBean(String.format(getContext().getResources().getString(R.string.txt_day_x), i), StepBean.STEP_COMPLETED);
                    stepsBeanList.add(stepBean);
                } else {
                    StepBean stepBean = new StepBean(String.format(getContext().getResources().getString(R.string.txt_day_x), i), StepBean.STEP_UNDO);
                    stepsBeanList.add(stepBean);
                }
            }
            setpview.setStepViewTexts(stepsBeanList)//总步骤
                    .setTextSize(10)//set textSize
                    .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(getContext(), R.color.txt_red))//设置StepsViewIndicator完成线的颜色
                    .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(getContext(), R.color.txt_gray))//设置StepsViewIndicator未完成线的颜色
                    .setStepViewComplectedTextColor(ContextCompat.getColor(getContext(), R.color.txt_red))//设置StepsView text完成的颜色
                    .setStepViewUnComplectedTextColor(ContextCompat.getColor(getContext(), R.color.txt_gray))//设置StepsView text未完成的颜色
                    .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_stepview_complted_red_packet))//设置StepsViewIndicator CompleteIcon
                    .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_stepview_default_red_packet))//设置StepsViewIndicator DefaultIcon
                    .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_stepview_default_red_packet));//设置StepsViewIndicator AttentionIcon
        } else {
            setpview.setVisibility(View.GONE);
        }

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
