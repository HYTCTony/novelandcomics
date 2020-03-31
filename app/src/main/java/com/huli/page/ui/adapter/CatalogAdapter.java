package com.huli.page.ui.adapter;

import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.R;
import com.huli.page.model.local.ReadSettingManager;
import com.huli.page.utils.FileUtils;
import com.huli.page.widget.page.PageStyle;
import com.huli.page.widget.page.TxtChapter;

import java.util.List;

import androidx.core.content.ContextCompat;

public class CatalogAdapter extends BaseQuickAdapter<TxtChapter, BaseViewHolder> {

    public CatalogAdapter(List<TxtChapter> data) {
        super(R.layout.item_category, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, TxtChapter item) {
        PageStyle mPageStyle = ReadSettingManager.getInstance().getPageStyle();
        boolean isNightMode = ReadSettingManager.getInstance().isNightMode();
        TextView tv = helper.getView(R.id.category_tv_chapter);
        if (item.getLink() == null) {
            helper.setText(R.id.category_tv_type, "已下载");
        } else {
            if (item.getBookId() != null && FileUtils.isChapterCached(item.getBookId(), item.getTitle())) {
                helper.setText(R.id.category_tv_type, "已下载");
                helper.setTextColor(R.id.category_tv_type, ContextCompat.getColor(getContext(), isNightMode ? R.color.hl_read_font_night :
                        mPageStyle.getFontColor()));
            } else {
                helper.setText(R.id.category_tv_type, "未下载");
                helper.setTextColor(R.id.category_tv_type, ContextCompat.getColor(getContext(), isNightMode ? R.color.txt_gray :
                        R.color.txt_gray_b2));
            }
        }
        if (item.isSelect()) {
            tv.setSelected(true);
            tv.setTextColor(ContextCompat.getColor(getContext(), isNightMode ? R.color.light_pink : R.color.light_red));
            helper.setTextColor(R.id.category_tv_type, ContextCompat.getColor(getContext(), isNightMode ? R.color.light_pink : R.color.light_red));
        } else {
            tv.setSelected(false);
            tv.setTextColor(ContextCompat.getColor(getContext(), isNightMode ? R.color.hl_read_font_night : mPageStyle.getFontColor()));
        }
        tv.setText(item.getTitle());
    }

}