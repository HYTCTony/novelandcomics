package com.huli.foxread.ui.adapters;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.R;
import com.huli.foxread.entity.BookEntity;
import com.huli.foxread.utils.FigureProcessor;
import com.huli.foxread.utils.GlideUtil;

import androidx.annotation.NonNull;

public class ClassifyBookListAdapter extends BaseQuickAdapter<BookEntity, BaseViewHolder> implements LoadMoreModule {

    public ClassifyBookListAdapter() {
        super(R.layout.recy_list_item_classify_book);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, BookEntity item) {
        int position = holder.getLayoutPosition();

        GlideUtil.loadRoundRect(getContext(), holder.getView(R.id.iv_book_cover), item.getHttp_image(), 0);
        holder.setText(R.id.tv_book_title, item.getName());
        holder.setText(R.id.tv_book_description, item.getIntroduce());
        holder.setText(R.id.tv_book_author_pen_name_and_book_kind, item.getAuthor());
        holder.setText(R.id.tv_book_word_count, FigureProcessor.formatWordNum(getContext(), item.getWord()));
        holder.setText(R.id.tv_book_be_over, item.getIs_end() == 1 ? R.string.txt_end : R.string.txt_serialize);
        holder.setText(R.id.tv_book_popularity, FigureProcessor.formatGreet(getContext(), item.getGreet()));
        if (position <= 2) {
            holder.setVisible(R.id.tv_book_ranking, true);
            holder.setText(R.id.tv_book_ranking, "TOP" + (position + 3));
        } else {
            holder.setGone(R.id.tv_book_ranking, true);
        }
    }
}
