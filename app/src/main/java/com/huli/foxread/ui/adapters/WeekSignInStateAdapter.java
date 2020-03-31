package com.huli.foxread.ui.adapters;

import android.graphics.drawable.Drawable;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.R;
import com.huli.foxread.entity.tab.SignInDay;
import com.huli.foxread.utils.DateTimeUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class WeekSignInStateAdapter extends BaseQuickAdapter<Integer, BaseViewHolder> {

    private boolean isSignInToday;        //今天是否已签到

    public WeekSignInStateAdapter() {
        super(R.layout.recy_grid_item_sign_in_state);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, Integer data) {
        TextView tvFlag = holder.getView(R.id.tv_sign_in_flag);


        Drawable dbTop;
        if (holder.getLayoutPosition() > 0) {
            Date date = new Date();//取时间
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            calendar.add(calendar.DATE, holder.getLayoutPosition());         //把日期往后增加一天.整数往后推,负数往前移动
            date = calendar.getTime(); //这个时间就是日期往后推一天的结果
            tvFlag.setText(DateTimeUtil.formatDateTime(date, "MM-dd"));
            tvFlag.setTextColor(ContextCompat.getColor(getContext(), R.color.txt_red));
            dbTop = ContextCompat.getDrawable(getContext(), R.drawable.ic_un_sign_in_ws);
        } else {
            tvFlag.setText(R.string.txt_today);
            if (isSignInToday) {
                dbTop = ContextCompat.getDrawable(getContext(), R.drawable.ic_sign_in_ws);
                tvFlag.setTextColor(ContextCompat.getColor(getContext(), R.color.txt_gray));
            } else {
                dbTop = ContextCompat.getDrawable(getContext(), R.drawable.ic_un_sign_in_ws);
                tvFlag.setTextColor(ContextCompat.getColor(getContext(), R.color.txt_red));
            }
        }
        tvFlag.setCompoundDrawablesWithIntrinsicBounds(null, dbTop, null, null);

        holder.setText(R.id.tv_reward, String.valueOf(data));
    }

    public void setSignInChange(boolean isSign){
        isSignInToday = isSign;
        notifyItemChanged(0);
    }
}
