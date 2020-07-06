package com.huli.page.ui.adapter;

import android.graphics.Typeface;
import android.widget.TextView;

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
        TextView fontName = helper.getView(R.id.tv_font_name);
        fontName.setText(item.getFontName());
        fontName.setTypeface(item.getFontPath().equals("DEFAULT") ? Typeface.DEFAULT : Typeface.createFromAsset(getContext().getAssets(), item.getFontPath()));
        if (item.isSelect()) {
            helper.setBackgroundResource(R.id.iv_select,R.mipmap.icon_select_red);
        } else {
            helper.setBackgroundResource(R.id.iv_select,R.mipmap.icon_select_white);
        }
    }
}