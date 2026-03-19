package com.nnmedia.read.ui.adapters;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.nnmedia.novel.R;
import com.nnmedia.read.entity.ReExchangeSetEntity;

import java.text.NumberFormat;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;

public class VipExchangeAdapter extends BaseQuickAdapter<ReExchangeSetEntity, BaseViewHolder> {

    private NumberFormat nf;
    private int checkPos = 0;

    public VipExchangeAdapter(OnVipComboSelectListenr onVipComboSelectListenr) {
        super(R.layout.recy_grid_item_vip_combo);
        this.onVipComboSelectListenr = onVipComboSelectListenr;
        nf = NumberFormat.getCurrencyInstance(Locale.CHINA);
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(0);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, ReExchangeSetEntity data) {
        nf.setMinimumFractionDigits(2);
        SpannableString spaRealPrice = new SpannableString(nf.format(data.getGold()));
        spaRealPrice.setSpan(new RelativeSizeSpan(0.65f), 0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        nf.setMinimumFractionDigits(0);
        SpannableString spaOriginalPrice = new SpannableString(nf.format(data.getGold()));
        spaOriginalPrice.setSpan(new RelativeSizeSpan(0.7f), 0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spaOriginalPrice.setSpan(new StrikethroughSpan(), 0, spaOriginalPrice.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        switch (data.getPay_type()) {
            case 1:
                holder.setText(R.id.tv_combo_real_price, "" + data.getGold());
                holder.setText(R.id.tv_original_price, R.string.txt_currency_gold);
                break;
            case 2:
                holder.setText(R.id.tv_combo_real_price, "" + data.getPoint());
                holder.setText(R.id.tv_original_price, R.string.txt_currency_point);
                break;
            default:
                holder.setText(R.id.tv_combo_real_price, "" + data.getDiscount_price());
                if (TextUtils.isEmpty(data.getDescription())) {
                    holder.setText(R.id.tv_original_price, R.string.txt_currency_cny);
                } else {
                    holder.setText(R.id.tv_original_price, data.getDescription());
                }
        }

        holder.setText(R.id.tv_combo_duration, data.getTitle());

        AppCompatCheckBox checkBox = holder.getView(R.id.cb_vip_combo);
        checkBox.setEnabled(false);

        int position = holder.getLayoutPosition();
        if (checkPos == position) {
            if (onVipComboSelectListenr != null) {
                onVipComboSelectListenr.onVipComboSelect(data.getId(), data.getAlipay(), data.getWeixin(), data.getCdkey());
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
                    if (onVipComboSelectListenr != null) {
//                        if (data.getPay_type() == 1) {
//                            onVipComboSelectListenr.onVipComboSelect(data.getId(), data.getAlipay(), data.getWeixin());
//                        } else {
                        onVipComboSelectListenr.onVipComboSelect(data.getId(), data.getAlipay(), data.getWeixin(), data.getCdkey());
//                        }
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

    public interface OnVipComboSelectListenr {
        void onVipComboSelect(String comboId, String alipay, String weixin, int cdkey);
    }
}