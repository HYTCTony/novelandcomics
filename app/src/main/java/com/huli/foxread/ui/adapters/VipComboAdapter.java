package com.huli.foxread.ui.adapters;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.R;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;

public class VipComboAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    private int checkPos = 0;

    public VipComboAdapter() {
        super(R.layout.recy_grid_item_vip_combo);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, String s) {

        SpannableString spaRealPrice = new SpannableString("￥12.00");
        spaRealPrice.setSpan(new RelativeSizeSpan(0.65f), 0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        SpannableString spaOriginalPrice = new SpannableString("￥20");
        spaOriginalPrice.setSpan(new RelativeSizeSpan(0.7f), 0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spaOriginalPrice.setSpan(new StrikethroughSpan(), 0, spaOriginalPrice.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        holder.setText(R.id.tv_combo_real_price, spaRealPrice);
        holder.setText(R.id.tv_original_price, spaOriginalPrice);

        holder.setText(R.id.tv_combo_duration, "1个月");

        AppCompatCheckBox checkBox = holder.getView(R.id.cb_vip_combo);
        checkBox.setEnabled(false);

        int position = holder.getLayoutPosition();
        if (checkPos == position) {
            checkBox.setChecked(true);
            holder.setTextColorRes(R.id.tv_combo_duration, R.color.txt_brownness_8b7342);
            holder.setTextColorRes(R.id.tv_combo_real_price, R.color.txt_brownness_8b7342);
            holder.setTextColorRes(R.id.tv_original_price, R.color.txt_brownness_8b7342);
        } else {
            checkBox.setChecked(false);
            holder.setTextColorRes(R.id.tv_combo_duration, R.color.txt_gray_999);
            holder.setTextColorRes(R.id.tv_combo_real_price, R.color.txt_black);
            holder.setTextColorRes(R.id.tv_original_price, R.color.txt_gray_999);
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
