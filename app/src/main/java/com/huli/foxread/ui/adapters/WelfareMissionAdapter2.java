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
import com.huli.foxread.entity.sections.MissionSection2;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class WelfareMissionAdapter2 extends BaseSectionQuickAdapter<MissionSection2, BaseViewHolder> {

    private boolean isVip = false;

    public void setVipMode(boolean vip) {
        isVip = vip;
    }

    public WelfareMissionAdapter2() {
        super(R.layout.recy_section_head_view_welfare);
        addItemType(MissionSection2.TYPE_MISSION_7DAY, R.layout.recy_list_item_reading_mission);
        addItemType(MissionSection2.TYPE_MISSION_NOR, R.layout.recy_list_item_welfare_mission_nor);

        addChildClickViewIds(R.id.btn_welfare_mission_action);
    }

    @Override
    protected void convertHeader(BaseViewHolder holder, MissionSection2 data) {
        holder.setText(R.id.tv_section_group_title, (String) data.getObject());
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, MissionSection2 data) {
        MissionEntity missionEntity = (MissionEntity) data.getObject();
        switch (holder.getItemViewType()) {
            case MissionSection2.TYPE_MISSION_7DAY:
                showNorMission(holder, missionEntity);
                showNewBie7DayMission(holder, missionEntity);
                break;

            case MissionSection2.TYPE_MISSION_NOR:
                showNorMission(holder, missionEntity);
                break;
            default:
                break;
        }

    }

    private void showNewBie7DayMission(@NonNull BaseViewHolder holder, MissionEntity missionEntity) {
        HorizontalStepView setpview = holder.getView(R.id.step_view_newbie_mission);
        setpview.setVisibility(View.VISIBLE);
        int signSuccCount = missionEntity.getSign_successions();
        List<StepBean> stepsBeanList = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            if (i < signSuccCount) {
                StepBean stepBean = new StepBean(String.format(getContext().getResources().getString(R.string.txt_day_x), i), StepBean.STEP_COMPLETED);
                stepsBeanList.add(stepBean);
            } else if (i == signSuccCount) {
                if (missionEntity.getStatus() == 2) {
                    StepBean stepBean = new StepBean(String.format(getContext().getResources().getString(R.string.txt_day_x), i), StepBean.STEP_COMPLETED);
                    stepsBeanList.add(stepBean);
                } else {
                    StepBean stepBean = new StepBean(String.format(getContext().getResources().getString(R.string.txt_day_x), i), StepBean.STEP_CURRENT);
                    stepsBeanList.add(stepBean);
                }
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
                .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(getContext(), R.drawable.ic_stepview_attention_red_packet));//设置StepsViewIndicator AttentionIcon
    }

    /**
     * 显示普通的任务
     *
     * @param holder
     * @param missionEntity
     */
    private void showNorMission(@NonNull BaseViewHolder holder, MissionEntity missionEntity) {
        holder.setText(R.id.btn_welfare_mission_action, missionEntity.getProgress());
        holder.setText(R.id.tv_welfare_mission_title, missionEntity.getType_name());
        holder.setText(R.id.tv_welfare_mission_explain, setNumColor(getContext(), missionEntity.getContent()));
        int reward;
        if (isVip) {
            reward = missionEntity.getVip_reward();
        } else {
            reward = missionEntity.getReward();
        }
        if (reward > 0) {
            holder.setText(R.id.tv_reward_of_mission, String.format(getContext().getString(R.string.txt_add_gold_coin_x), reward));
            holder.setVisible(R.id.tv_reward_of_mission, true);
        }else {
            holder.setGone(R.id.tv_reward_of_mission, true);
        }

        if (missionEntity.getStatus() == 0 || missionEntity.getStatus() == -1) {
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
