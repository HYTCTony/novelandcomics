package com.huli.foxread.ui.adapters;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.R;
import com.huli.foxread.entity.BookMultiEntity;
import com.huli.foxread.utils.FigureProcessor;
import com.huli.foxread.utils.GlideUtil;

import java.util.List;

import androidx.annotation.NonNull;

public class BooksMultiItemAdapter extends BaseMultiItemQuickAdapter<BookMultiEntity, BaseViewHolder> {

    public BooksMultiItemAdapter(List<BookMultiEntity> data) {
        super(data);
        // 绑定 layout 对应的 type
        addItemType(BookMultiEntity.DETAILED, R.layout.recy_grid_multi_item_book_firstitem);
        addItemType(BookMultiEntity.ITEM_FIRST, R.layout.recy_grid_multi_item_book);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, BookMultiEntity item) {
        switch (helper.getItemViewType()) {
            case BookMultiEntity.DETAILED:
                GlideUtil.loadRoundRect(getContext(), helper.getView(R.id.iv_book_cover), item.getHttp_image(), 0);
                helper.setText(R.id.tv_book_title, item.getName());
                helper.setText(R.id.tv_book_score, item.getScore() + getContext().getString(R.string.unit_score));
                helper.setText(R.id.tv_book_description, item.getIntroduce());
                helper.setText(R.id.tv_book_author_pen_name, item.getAuthor());

                List<String> tags = item.getTag();
                if (tags != null && tags.size() > 0) {
                    helper.setVisible(R.id.tv_book_tag, true);
                    helper.setText(R.id.tv_book_tag, tags.get(0));
                } else {
                    helper.setGone(R.id.tv_book_tag, true);
                }

                helper.setText(R.id.tv_book_word_count, FigureProcessor.formatWordNum(getContext(), item.getWord()));
                break;
            case BookMultiEntity.ITEM_FIRST:
                GlideUtil.loadRoundRect(getContext(), helper.getView(R.id.iv_book_cover), item.getHttp_image(), 0);
                helper.setText(R.id.tv_book_name, item.getName());
                helper.setText(R.id.tv_authorName, item.getAuthor());
                break;
        }
    }
}
