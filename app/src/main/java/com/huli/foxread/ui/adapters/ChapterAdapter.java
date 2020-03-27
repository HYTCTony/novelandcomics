package com.huli.foxread.ui.adapters;

import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.R;
import com.huli.page.model.bean.BookChapter;
import com.huli.page.utils.FileUtils;

import java.util.List;

import androidx.core.content.ContextCompat;

public class ChapterAdapter extends BaseQuickAdapter<BookChapter, BaseViewHolder> {

    public ChapterAdapter(List<BookChapter> data) {
        super(R.layout.item_category, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BookChapter item) {
        TextView tv = helper.getView(R.id.category_tv_chapter);
        if (item.getNovel_id() != null && FileUtils.isChapterCached(item.getNovel_id(), item.getName())) {
            helper.setText(R.id.category_tv_type, "已下载");
            helper.setTextColor(R.id.category_tv_type, ContextCompat.getColor(getContext(), R.color.txt_gray_999));
        } else {
            helper.setText(R.id.category_tv_type, "未下载");
            helper.setTextColor(R.id.category_tv_type, ContextCompat.getColor(getContext(), R.color.txt_gray_b2));
        }
        tv.setText(item.getName());
    }
}
