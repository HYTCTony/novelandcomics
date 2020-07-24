package com.huli.page.ui.adapter;

import android.widget.RadioButton;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.R;
import com.huli.page.model.bean.Voicer;

public class CloudVoicersAdapter extends BaseQuickAdapter<Voicer, BaseViewHolder> {

    public CloudVoicersAdapter() {
        super(R.layout.item_cloud_voicer);
    }

    @Override
    protected void convert(BaseViewHolder helper, Voicer item) {
        RadioButton radioButton = helper.getView(R.id.tv_cloud_voicer_name);
        radioButton.setText(item.name);
        if (item.isSelect) {
            radioButton.setChecked(true);
        } else {
            radioButton.setChecked(false);
        }
    }
}