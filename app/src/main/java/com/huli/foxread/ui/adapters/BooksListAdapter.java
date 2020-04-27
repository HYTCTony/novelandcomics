package com.huli.foxread.ui.adapters;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.R;
import com.huli.foxread.entity.BookEntity;
import com.huli.foxread.utils.FigureProcessor;
import com.huli.foxread.utils.GlideUtil;

import java.util.List;

import androidx.annotation.NonNull;

public class BooksListAdapter extends BaseQuickAdapter<BookEntity, BaseViewHolder> implements LoadMoreModule {

    public BooksListAdapter() {
        super(R.layout.recy_list_item_book_normal);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, BookEntity item) {
        GlideUtil.loadRoundRect(getContext(), helper.getView(R.id.iv_book_cover), item.getHttp_image(), 0);
        helper.setText(R.id.tv_book_title, item.getName());
        helper.setText(R.id.tv_book_score, item.getScore() + getContext().getString(R.string.unit_score));
        helper.setText(R.id.tv_book_description, item.getIntroduce());
        helper.setText(R.id.tv_book_author_pen_name_and_book_kind, item.getAuthor());
        helper.setText(R.id.tv_book_word_count, FigureProcessor.formatWordNum(getContext(), item.getWord()));

        List<String> tags = item.getTag();
        if (tags != null && tags.size() > 0) {
            helper.setVisible(R.id.tv_book_tag, true);
            helper.setText(R.id.tv_book_tag, tags.get(0));
        } else {
            helper.setGone(R.id.tv_book_tag, true);
        }
    }
}
