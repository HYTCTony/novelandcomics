package com.nnmedia.read.ui.adapters;

import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.nnmedia.novel.R;
import com.nnmedia.page.model.bean.BookChapter;
import com.nnmedia.page.utils.FileUtils;

import java.util.List;

import androidx.core.content.ContextCompat;

public class ChapterAdapter extends BaseQuickAdapter<BookChapter, BaseViewHolder> {

    public ChapterAdapter(List<BookChapter> data) {
        super(R.layout.item_category, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BookChapter item) {
        TextView tv = helper.getView(R.id.category_tv_chapter);
        if (!item.isCanView()) {
            helper.setVisible(R.id.iv_category_type, true);
            helper.setImageResource(R.id.iv_category_type, R.mipmap.icon_lock);
            helper.setTextColor(R.id.category_tv_chapter, ContextCompat.getColor(getContext(), R.color.txt_gray_b2));
        } else {
            helper.setImageResource(R.id.iv_category_type, R.mipmap.icon_download);
            if (item.getNovel_id() != null && FileUtils.isChapterCached(item.getNovel_id(), item.getName())) {
//                helper.setText(R.id.category_tv_type, "已下载");
//                helper.setTextColor(R.id.category_tv_type, ContextCompat.getColor(getContext(), R.color.txt_col_365565));
                helper.setTextColor(R.id.category_tv_chapter, ContextCompat.getColor(getContext(), R.color.txt_col_365565));
                helper.setVisible(R.id.iv_category_type, false);
            } else {
//                helper.setText(R.id.category_tv_type, "未下载");
//                helper.setTextColor(R.id.category_tv_type, ContextCompat.getColor(getContext(), R.color.txt_gray_b2));
                helper.setTextColor(R.id.category_tv_chapter, ContextCompat.getColor(getContext(), R.color.txt_gray_b2));
                helper.setVisible(R.id.iv_category_type, true);
            }
        }
        helper.setText(R.id.category_tv_chapter, item.getName());
    }
}