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
import com.huli.foxread.entity.ReChargeSetEntity;

import java.text.NumberFormat;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;

public class VipComboAdapter extends BaseQuickAdapter<ReChargeSetEntity, BaseViewHolder> {

    private NumberFormat nf;
    private int checkPos = 0;

    public VipComboAdapter(OnVipComboSelectListenr onVipComboSelectListenr) {
        super(R.layout.recy_grid_item_vip_combo);
        this.onVipComboSelectListenr = onVipComboSelectListenr;
        nf = NumberFormat.getCurrencyInstance(Locale.CHINA);
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(0);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, ReChargeSetEntity data) {
        nf.setMinimumFractionDigits(2);
        SpannableString spaRealPrice = new SpannableString(nf.format(data.getDiscount_price()));
        spaRealPrice.setSpan(new RelativeSizeSpan(0.65f), 0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        nf.setMinimumFractionDigits(0);
        SpannableString spaOriginalPrice = new SpannableString(nf.format(data.getInitial_price()));
        spaOriginalPrice.setSpan(new RelativeSizeSpan(0.7f), 0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spaOriginalPrice.setSpan(new StrikethroughSpan(), 0, spaOriginalPrice.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        holder.setText(R.id.tv_combo_real_price, spaRealPrice);
        holder.setText(R.id.tv_original_price, spaOriginalPrice);

        holder.setText(R.id.tv_combo_duration, data.getTitle());

        AppCompatCheckBox checkBox = holder.getView(R.id.cb_vip_combo);
        checkBox.setEnabled(false);

        int position = holder.getLayoutPosition();
        if (checkPos == position) {
            if(onVipComboSelectListenr!=null){
                onVipComboSelectListenr.onVipComboSelect(data.getId(), data.getDiscount_price());
            }
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
                    if(onVipComboSelectListenr!=null){
                        onVipComboSelectListenr.onVipComboSelect(data.getId(), data.getDiscount_price());
                    }
                }
                notifyDataSetChanged();
            }
        });
    }


    public void setOnVipComboSelectListenr(OnVipComboSelectListenr onVipComboSelectListenr) {
        this.onVipComboSelectListenr = onVipComboSelectListenr;
    }
    private OnVipComboSelectListenr onVipComboSelectListenr;
    public interface OnVipComboSelectListenr{
        void onVipComboSelect(String comboId, double discountPrice);
    }
}
