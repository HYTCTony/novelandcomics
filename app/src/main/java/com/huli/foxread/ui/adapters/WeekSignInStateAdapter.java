package com.huli.foxread.ui.adapters;

import android.graphics.drawable.Drawable;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.R;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class WeekSignInStateAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public WeekSignInStateAdapter() {
        super(R.layout.recy_grid_item_sign_in_state);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, String s) {
        TextView tvFlag = holder.getView(R.id.tv_sign_in_flag);
        tvFlag.setText(R.string.txt_non_arrival);
        tvFlag.setTextColor(ContextCompat.getColor(getContext(), R.color.txt_brownness_8b7342));
        Drawable dbTop = ContextCompat.getDrawable(getContext(), R.drawable.ic_un_sign_in_ws);
        tvFlag.setCompoundDrawablesWithIntrinsicBounds(null, dbTop , null, null);
    }
}
