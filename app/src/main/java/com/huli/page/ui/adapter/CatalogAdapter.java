package com.huli.page.ui.adapter;

import android.graphics.drawable.Drawable;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.R;
import com.huli.page.utils.FileUtils;
import com.huli.page.widget.page.TxtChapter;

import java.util.List;

import androidx.core.content.ContextCompat;

public class CatalogAdapter extends BaseQuickAdapter<TxtChapter, BaseViewHolder> {

    public CatalogAdapter(List<TxtChapter> data) {
        super(R.layout.item_category, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, TxtChapter item) {
        TextView tv = helper.getView(R.id.category_tv_chapter);
        //首先判断是否该章已下载
        Drawable drawable = null;

        //TODO:目录显示设计的有点不好，需要靠成员变量是否为null来判断。
        //如果没有链接地址表示是本地文件
        if (item.getLink() == null) {
            drawable = ContextCompat.getDrawable(getContext(), R.drawable.selector_category_load);
        } else {
            if (item.getBookId() != null && FileUtils.isChapterCached(item.getBookId(), item.getTitle())) {
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.selector_category_load);
            } else {
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.selector_category_unload);
            }
        }
        if (item.isSelect()) {
            tv.setTextColor(ContextCompat.getColor(getContext(), R.color.light_red));
            tv.setSelected(true);
        } else {
            tv.setSelected(false);
            tv.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        }
        tv.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        tv.setText(item.getTitle());

    }

}