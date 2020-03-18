package com.huli.foxread.ui.adapters;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.R;
import com.huli.foxread.entity.WithdrawalOptionEntity;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;

/**
 * 金币提现选项
 */
public class WithdrawalGoldAdapter extends BaseQuickAdapter<WithdrawalOptionEntity, BaseViewHolder> {

    private int checkPos = 0;

    public WithdrawalGoldAdapter() {
        super(R.layout.recy_grid_item_withdrawal_gold_coin);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, WithdrawalOptionEntity data) {
        holder.setText(R.id.tv_withdrawal_money, data.getTitle());
        holder.setText(R.id.tv_withdrawal_need_gold_coin, data.getRemarks());
        AppCompatCheckBox checkBox = holder.getView(R.id.cb_withdrawal_amount);
        checkBox.setEnabled(false);

        int position = holder.getLayoutPosition();
        if (checkPos == position) {
            checkBox.setChecked(true);
            holder.setTextColorRes(R.id.tv_withdrawal_money, R.color.txt_red);
            holder.setTextColorRes(R.id.tv_withdrawal_need_gold_coin, R.color.txt_red);
        } else {
            checkBox.setChecked(false);
            holder.setTextColorRes(R.id.tv_withdrawal_money, R.color.txt_black);
            holder.setTextColorRes(R.id.tv_withdrawal_need_gold_coin, R.color.txt_black);
        }

        holder.itemView.setOnClickListener(v -> {
            if (checkPos == position) {
                checkBox.setClickable(false);
            } else {
                checkPos = position;
                checkBox.setChecked(true);
                selectPlanId = data.getId();
            }
            notifyDataSetChanged();
        });
    }

    private String selectPlanId;

    public String getSelectPlanId() {
        return selectPlanId;
    }
}
