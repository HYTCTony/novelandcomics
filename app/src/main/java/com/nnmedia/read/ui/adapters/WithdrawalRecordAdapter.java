package com.nnmedia.read.ui.adapters;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.nnmedia.novel.R;
import com.nnmedia.read.entity.WithdrawalRecordBean;

import java.text.DecimalFormat;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class WithdrawalRecordAdapter extends BaseQuickAdapter<WithdrawalRecordBean, BaseViewHolder> implements LoadMoreModule {

    private DecimalFormat df;

    public WithdrawalRecordAdapter() {
        super(R.layout.recy_list_item_withdrawal_record);
        df = new DecimalFormat("#######.00" + "元");
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, WithdrawalRecordBean data) {
        holder.setText(R.id.tv_withdrawal_title, "提现类型：" + data.getSource());
        holder.setText(R.id.tv_withdrawal_time, data.getCreatetime());
        holder.setText(R.id.tv_money_yuan, df.format(data.getMoney()));
        holder.setText(R.id.tv_withdrawal_status, data.getStatus());
        if (data.getStatus().equals("通过")) {
            holder.setTextColor(R.id.tv_money_yuan, ContextCompat.getColor(getContext(), R.color.txt_red));

        } else if (data.getStatus().equals("申请中")) {
            holder.setTextColor(R.id.tv_money_yuan, ContextCompat.getColor(getContext(), R.color.txt_dark_gold));
        } else {
            holder.setTextColor(R.id.tv_money_yuan, ContextCompat.getColor(getContext(), R.color.txt_gray));
        }
    }
}
