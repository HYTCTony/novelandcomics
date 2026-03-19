package com.nnmedia.page.ui.adapter;

import android.widget.RadioButton;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.nnmedia.novel.R;
import com.nnmedia.page.model.bean.Timing;

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
