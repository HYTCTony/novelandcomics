package com.huli.foxread.ui.adapters;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.R;
import com.huli.foxread.entity.WithdrawalRecordBean;

import java.text.DecimalFormat;

import androidx.annotation.NonNull;

public class WithdrawalRecordAdapter extends BaseQuickAdapter<WithdrawalRecordBean, BaseViewHolder> implements LoadMoreModule {

    private DecimalFormat df;

    public WithdrawalRecordAdapter() {
        super(R.layout.recy_list_item_withdrawal_record);
        df = new DecimalFormat("#######.00" + "元");
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, WithdrawalRecordBean data) {
        holder.setText(R.id.tv_withdrawal_title, data.getSource());
        holder.setText(R.id.tv_withdrawal_time, data.getCreatetime());
        holder.setText(R.id.tv_money_yuan, df.format(data.getMoney()));
        holder.setText(R.id.tv_withdrawal_status, data.getStatus());
    }
}
