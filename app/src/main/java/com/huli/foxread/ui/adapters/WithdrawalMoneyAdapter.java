package com.huli.foxread.ui.adapters;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.R;
import com.huli.foxread.entity.WithdrawalOptionEntity;

import java.text.DecimalFormat;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;

/**
 * 金币提现选项
 */
public class WithdrawalMoneyAdapter extends BaseQuickAdapter<WithdrawalOptionEntity, BaseViewHolder> {

    private DecimalFormat df;
    private int checkPos = 0;

    public WithdrawalMoneyAdapter() {
        super(R.layout.recy_grid_item_withdrawal_money);
        df = new DecimalFormat("提现" + "#####,00" + "元");
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, WithdrawalOptionEntity data) {
        holder.setText(R.id.tv_withdrawal_money, df.format(data.getNeed_money()));
        AppCompatCheckBox checkBox = holder.getView(R.id.cb_withdrawal_amount);
        checkBox.setEnabled(false);

        int position = holder.getLayoutPosition();
        if (checkPos == position) {
            checkBox.setChecked(true);
            holder.setTextColorRes(R.id.tv_withdrawal_money, R.color.txt_red);
        } else {
            checkBox.setChecked(false);
            holder.setTextColorRes(R.id.tv_withdrawal_money, R.color.txt_black);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPos == position) {
                    checkBox.setClickable(false);
                } else {
                    checkPos = position;
                    checkBox.setChecked(true);
                    selectPlanId = data.getId();
                }
                notifyDataSetChanged();
            }
        });
    }

    private String selectPlanId;

    public String getSelectPlanId() {
        return selectPlanId;
    }
}
