package com.nnmedia.read.ui.adapters;

import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.nnmedia.novel.R;
import com.nnmedia.read.entity.BookEntity;
import com.nnmedia.read.utils.FigureProcessor;
import com.nnmedia.read.utils.GlideUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 创建时间：2020/7/18  18:04
 * 备注：更多
 */
public class MoreBooksAdapter extends BaseQuickAdapter<BookEntity, BaseViewHolder> implements LoadMoreModule {

    public MoreBooksAdapter() {
        super(R.layout.recy_list_item_book_normal);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, BookEntity book) {
        GlideUtil.loadRoundRect(getContext(), holder.getView(R.id.iv_book_cover), book.getHttp_image());
        holder.setText(R.id.tv_book_title, book.getName());
        holder.setText(R.id.tv_book_score, book.getScore() + getContext().getString(R.string.unit_score));
        holder.setText(R.id.tv_book_description, book.getIntroduce());
        holder.setText(R.id.tv_book_author_pen_name_and_book_kind, book.getAuthor());
        holder.setText(R.id.tv_book_word_count, FigureProcessor.formatWordNum(getContext(), book.getWord()));

        List<String> tags = book.getTag();
        if (tags != null && tags.size() > 0) {
            String str = tags.get(0);
            if (!TextUtils.isEmpty(str)) {
                holder.setVisible(R.id.tv_book_be_over, true);
                holder.setText(R.id.tv_book_be_over, str);
            } else {
                holder.setGone(R.id.tv_book_be_over, true);
            }
        } else {
            holder.setGone(R.id.tv_book_be_over, true);
        }
    }
}
