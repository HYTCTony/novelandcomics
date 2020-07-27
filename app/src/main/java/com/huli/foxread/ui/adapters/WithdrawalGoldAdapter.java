package com.huli.foxread.ui.adapters;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.R;
import com.huli.foxread.entity.WithdrawalOptionEntity;

import androidx.annotation.NonNull;

/**
 * 金币提现选项
 */
public class WithdrawalGoldAdapter extends BaseQuickAdapter<WithdrawalOptionEntity, BaseViewHolder> {

    public WithdrawalGoldAdapter() {
        super(R.layout.recy_grid_item_withdrawal_gold_coin);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, WithdrawalOptionEntity data) {
        holder.setText(R.id.tv_withdrawal_money, data.getTitle());
        holder.setText(R.id.tv_withdrawal_need_gold_coin, data.getRemarks());
    }
}
