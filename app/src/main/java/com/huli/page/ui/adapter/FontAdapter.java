package com.huli.page.ui.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.R;
import com.huli.page.model.bean.Font;

import java.util.List;

public class FontAdapter extends BaseQuickAdapter<Font, BaseViewHolder> {

    public FontAdapter(List<Font> data) {
        super(R.layout.item_font, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Font item) {
        helper.setText(R.id.tv_font_name, item.getFontName());
        if (item.isSelect()) {
            helper.setGone(R.id.iv_select, true);
        } else {
            helper.setGone(R.id.iv_select, false);
        }
    }
}