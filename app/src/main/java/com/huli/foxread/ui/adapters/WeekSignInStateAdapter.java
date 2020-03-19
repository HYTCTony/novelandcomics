package com.huli.foxread.ui.adapters;

import android.graphics.drawable.Drawable;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.R;
import com.huli.foxread.entity.tab.SignInDay;
import com.huli.foxread.utils.DateTimeUtil;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class WeekSignInStateAdapter extends BaseQuickAdapter<SignInDay, BaseViewHolder> {

    private int signNum;        //一周内的签到次数

    public WeekSignInStateAdapter() {
        super(R.layout.recy_grid_item_sign_in_state);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, SignInDay data) {
        TextView tvFlag = holder.getView(R.id.tv_sign_in_flag);
//        tvFlag.setText(R.string.txt_non_arrival);
        tvFlag.setText(DateTimeUtil.formatDateTime(data.getDay(), "MM-dd"));
        tvFlag.setTextColor(ContextCompat.getColor(getContext(), R.color.txt_brownness_8b7342));

        holder.setText(R.id.tv_reward, String.valueOf(data.getReward()));

        Drawable dbTop = ContextCompat.getDrawable(getContext(), R.drawable.ic_un_sign_in_ws);
        tvFlag.setCompoundDrawablesWithIntrinsicBounds(null, dbTop, null, null);
    }
}
