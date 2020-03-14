package com.huli.foxread.ui.adapters;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.R;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;

public class WithdrawalAmountAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    private int checkPos = 0;

    public WithdrawalAmountAdapter() {
        super(R.layout.recy_grid_item_withdrawal_amount);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, String s) {
        holder.setText(R.id.tv_withdrawal_money, "提现15元");
        holder.setText(R.id.tv_withdrawal_need_gold_coin, "需150,000金币");
        AppCompatCheckBox checkBox = holder.getView(R.id.cb_withdrawal_amount);
        checkBox.setEnabled(false);

        int position = holder.getLayoutPosition();
        if (checkPos == position) {
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPos == position) {
                    checkBox.setClickable(false);
                } else {
                    checkPos = position;
                    checkBox.setChecked(true);
                }
                notifyDataSetChanged();
            }
        });
    }
}
