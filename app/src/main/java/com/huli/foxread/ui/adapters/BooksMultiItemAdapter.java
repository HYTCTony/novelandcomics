package com.huli.foxread.ui.adapters;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.R;
import com.huli.foxread.entity.BookMultiEntity;
import com.huli.foxread.utils.GlideUtil;

import java.util.List;

import androidx.annotation.NonNull;

public class BooksMultiItemAdapter extends BaseMultiItemQuickAdapter<BookMultiEntity, BaseViewHolder> {

    public BooksMultiItemAdapter(List<BookMultiEntity> data) {
        super(data);
        // 绑定 layout 对应的 type
        addItemType(BookMultiEntity.DETAILED, R.layout.recy_grid_multi_item_book_firstitem);
        addItemType(BookMultiEntity.SUCCINCT, R.layout.recy_grid_multi_item_book);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, BookMultiEntity item) {
        switch (helper.getItemViewType()) {
            case BookMultiEntity.DETAILED:
                GlideUtil.loadRoundRect(getContext(), helper.getView(R.id.iv_book_cover), "url", 0);
                helper.setText(R.id.tv_book_title, "猎能者");
                helper.setText(R.id.tv_book_score, 9.3f + getContext().getString(R.string.unit_score));
                helper.setText(R.id.tv_book_description, "岳母鄙视，妻子瞧不起，亲人嘲讽所有人都觉得，自己能骑在废物赘婿发现他的手机上多");
                helper.setText(R.id.tv_book_author_pen_name, "不吃西红柿");
                helper.setText(R.id.tv_book_tag, "热血");
                helper.setText(R.id.tv_book_word_count, 128.9f + getContext().getString(R.string.unit_w));
                break;
            case BookMultiEntity.SUCCINCT:
                GlideUtil.loadRoundRect(getContext(), helper.getView(R.id.iv_book_cover), "url", 0);
                helper.setText(R.id.tv_book_name, "猎能者");
                helper.setText(R.id.tv_authorName, "不吃西红柿");
                break;
        }
    }
}
