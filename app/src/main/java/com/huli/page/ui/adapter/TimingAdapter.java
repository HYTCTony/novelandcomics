package com.huli.page.ui.adapter;

import android.widget.RadioButton;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.R;
import com.huli.page.model.bean.Timing;

public class TimingAdapter extends BaseQuickAdapter<Timing, BaseViewHolder> {

    public TimingAdapter() {
        super(R.layout.item_timing);
    }

    @Override
    protected void convert(BaseViewHolder helper, Timing item) {
        RadioButton radioButton = helper.getView(R.id.tv_timer_name);
        radioButton.setText(item.name);
        if (item.isSelect) {
            radioButton.setChecked(true);
        } else {
            radioButton.setChecked(false);
        }
    }
}
