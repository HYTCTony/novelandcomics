package com.nnmedia.page.ui.adapter;

import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.nnmedia.novel.R;
import com.nnmedia.page.model.local.ReadSettingManager;
import com.nnmedia.page.utils.FileUtils;
import com.nnmedia.page.widget.page.PageStyle;
import com.nnmedia.page.widget.page.TxtChapter;

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

        if (item.isSelect()) {
            tv.setSelected(true);
            tv.setTextColor(ContextCompat.getColor(getContext(), isNightMode ? PageStyle.NIGHT.getSelectFont() : mPageStyle.getSelectFont()));
            helper.setTextColor(R.id.category_tv_type, ContextCompat.getColor(getContext(), isNightMode ? PageStyle.NIGHT.getSelectFont() :
                    mPageStyle.getSelectFont()));
            helper.setVisible(R.id.iv_category_type, false);
        } else {
            tv.setSelected(false);
            if (item.getLink() != null) {
//                helper.setText(R.id.category_tv_type, "已下载");
                helper.setVisible(R.id.iv_category_type, false);
            } else {
                if (item.getBookId() != null && FileUtils.isChapterCached(item.getBookId(), item.getTitle())) {
//                    helper.setText(R.id.category_tv_type, "已下载");
//                    helper.setTextColor(R.id.category_tv_type, ContextCompat.getColor(getContext(), isNightMode ? PageStyle.NIGHT.getFontColor() :
//                            mPageStyle.getFontColor()));
                    if (item.isLock())
                        tv.setTextColor(ContextCompat.getColor(getContext(), isNightMode ? PageStyle.NIGHT.getTipsColor() : mPageStyle.getTipsColor()));
                    else
                        tv.setTextColor(ContextCompat.getColor(getContext(), isNightMode ? PageStyle.NIGHT.getFontColor() : mPageStyle.getFontColor()));
                    helper.setVisible(R.id.iv_category_type, false);
                } else {
//                    helper.setText(R.id.category_tv_type, "未下载");
//                    helper.setTextColor(R.id.category_tv_type, ContextCompat.getColor(getContext(), isNightMode ? R.color.txt_gray : R.color.txt_gray_b2));
                    tv.setTextColor(ContextCompat.getColor(getContext(), isNightMode ? PageStyle.NIGHT.getTipsColor() : mPageStyle.getTipsColor()));
                    helper.setVisible(R.id.iv_category_type, true);
                }
            }
        }
        if (item.isLock()) {
            helper.setVisible(R.id.iv_category_type, true);
            helper.setImageResource(R.id.iv_category_type, R.mipmap.icon_lock);
        } else {
            helper.setImageResource(R.id.iv_category_type, R.mipmap.icon_download);
        }
        tv.setText(item.getTitle());
    }

}