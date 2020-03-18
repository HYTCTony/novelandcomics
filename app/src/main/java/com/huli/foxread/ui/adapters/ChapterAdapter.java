package com.huli.foxread.ui.adapters;

import android.graphics.drawable.Drawable;
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
        //首先判断是否该章已下载
        Drawable drawable = null;

        if (item.getBookId() != null && FileUtils.isChapterCached(item.getBookId(), item.getName())) {
            drawable = ContextCompat.getDrawable(getContext(), R.drawable.selector_category_load);
        } else {
            drawable = ContextCompat.getDrawable(getContext(), R.drawable.selector_category_unload);
        }
        tv.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        tv.setText(item.getName());
    }
}
