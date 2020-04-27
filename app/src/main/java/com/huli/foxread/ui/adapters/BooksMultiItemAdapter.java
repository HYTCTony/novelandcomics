package com.huli.foxread.ui.adapters;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.R;
import com.huli.foxread.entity.multi.BookMultiEntity;
import com.huli.foxread.utils.GlideUtil;

import java.util.List;

import androidx.annotation.NonNull;

public class BooksMultiItemAdapter extends BaseMultiItemQuickAdapter<BookMultiEntity, BaseViewHolder> {

    public BooksMultiItemAdapter() {
        // 绑定 layout 对应的 type
        addItemType(BookMultiEntity.ITEM_FIRST, R.layout.recy_grid_multi_item_book_firstitem);
        addItemType(BookMultiEntity.DETAILED, R.layout.recy_grid_multi_item_book);
    }

    public BooksMultiItemAdapter(List<BookMultiEntity> data) {
        super(data);
        // 绑定 layout 对应的 type
        addItemType(BookMultiEntity.ITEM_FIRST, R.layout.recy_grid_multi_item_book_firstitem);
        addItemType(BookMultiEntity.DETAILED, R.layout.recy_grid_multi_item_book);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, BookMultiEntity novel) {
        switch (helper.getItemViewType()) {
            case BookMultiEntity.ITEM_FIRST:
                GlideUtil.loadRoundRect(getContext(), helper.getView(R.id.iv_book_cover), novel.getHttp_image(), 0);
                helper.setText(R.id.tv_book_title, novel.getNovel_name());
                helper.setText(R.id.tv_book_score, novel.getScore() + getContext().getString(R.string.unit_score));
                helper.setText(R.id.tv_book_description, novel.getIntroduce());
                break;
            case BookMultiEntity.DETAILED:
                GlideUtil.loadRoundRect(getContext(), helper.getView(R.id.iv_book_cover), novel.getHttp_image(), 0);
                helper.setText(R.id.tv_book_name, novel.getNovel_name());
                helper.setText(R.id.tv_authorName, novel.getAuthor());
                break;
        }
    }
}
