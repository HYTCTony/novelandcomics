package com.nnmedia.read.ui.adapters;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.nnmedia.novel.R;
import com.nnmedia.read.entity.CEYOrderEntity;
import com.nnmedia.read.utils.DateTimeUtil;

import java.text.NumberFormat;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class CDKEYOrderAdapter extends BaseQuickAdapter<CEYOrderEntity, BaseViewHolder> implements LoadMoreModule {

    public CDKEYOrderAdapter() {
        super(R.layout.recy_list_item_bill_detail);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, CEYOrderEntity data) {
        String orderStatu;
        switch (data.getOrder_state()) {
            case 0:
                orderStatu = "订单已取消";
                holder.setTextColor(R.id.tv_title_order_statu, ContextCompat.getColor(getContext(), R.color.txt_gray_8f9dac));
                break;
            case 10:
                orderStatu = "未付款";
                holder.setTextColor(R.id.tv_title_order_statu, ContextCompat.getColor(getContext(), R.color.txt_gray_8f9dac));
                break;
            case 20:
                orderStatu = "购买成功";
                holder.setTextColor(R.id.tv_title_order_statu, ContextCompat.getColor(getContext(), R.color.green));
                break;
            default:
                holder.setTextColor(R.id.tv_title_order_statu, ContextCompat.getColor(getContext(), R.color.txt_gray_8f9dac));
                orderStatu = "未付款";
        }
        String payStatu;
        switch (data.getPayment_code()) {
            case 0:
                payStatu = "";
                break;
            case 4:
                payStatu = "（微信）";
                break;
            case 5:
                payStatu = "（支付宝）";
                break;
            default:
                payStatu = "";
        }
        holder.setText(R.id.tv_title_order_statu, orderStatu);
        holder.setText(R.id.tv_title_gc_dt, data.getBody() + payStatu);
        holder.setText(R.id.tv_time_gc_dt, DateTimeUtil.formatDateTime(data.getCreatetime() * 1000));
        double goldCoinCount = Double.parseDouble(data.getPayment_money());
        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.CHINA);
        holder.setText(R.id.tv_gold_coin_count_gc_dt, nf.format(goldCoinCount));
    }
}
