package com.huli.foxread.ui.adapters;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.R;
import com.huli.foxread.entity.BookEntity2;
import com.huli.foxread.entity.multi.ForestallNewMultiEntity;
import com.huli.foxread.utils.FigureProcessor;
import com.huli.foxread.utils.GlideUtil;

import java.util.List;

import androidx.annotation.NonNull;

/**
 * 抢先新书
 */
public class ForestBookMultiItemAdapter extends BaseMultiItemQuickAdapter<ForestallNewMultiEntity, BaseViewHolder> {

    public ForestBookMultiItemAdapter() {
        // 绑定 layout 对应的 type
        addItemType(ForestallNewMultiEntity.DETAILED, R.layout.recy_grid_multi_item_book_firstitem);
        addItemType(ForestallNewMultiEntity.ITEM_FIRST, R.layout.recy_grid_multi_item_book);
    }

    public ForestBookMultiItemAdapter(List<ForestallNewMultiEntity> data) {
        super(data);
        // 绑定 layout 对应的 type
        addItemType(ForestallNewMultiEntity.DETAILED, R.layout.recy_grid_multi_item_book_firstitem);
        addItemType(ForestallNewMultiEntity.ITEM_FIRST, R.layout.recy_grid_multi_item_book);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, ForestallNewMultiEntity item) {
        BookEntity2 novel = item.getProfileNovel();
        switch (helper.getItemViewType()) {
            case ForestallNewMultiEntity.DETAILED:
                GlideUtil.loadRoundRect(getContext(), helper.getView(R.id.iv_book_cover), novel.getHttp_image(), 0);
                helper.setText(R.id.tv_book_title, novel.getName());
                helper.setText(R.id.tv_book_score, novel.getScore() + getContext().getString(R.string.unit_score));
                helper.setText(R.id.tv_book_description, novel.getIntroduce());
                helper.setText(R.id.tv_book_author_pen_name, novel.getAuthor());

                List<String> tags = novel.getTag();
                if (tags != null && tags.size() > 0) {
                    helper.setVisible(R.id.tv_book_tag, true);
                    helper.setText(R.id.tv_book_tag, tags.get(0));
                } else {
                    helper.setGone(R.id.tv_book_tag, true);
                }

                helper.setText(R.id.tv_book_word_count, FigureProcessor.formatWordNum(getContext(), novel.getWord()));
                break;
            case ForestallNewMultiEntity.ITEM_FIRST:
                GlideUtil.loadRoundRect(getContext(), helper.getView(R.id.iv_book_cover), novel.getHttp_image(), 0);
                helper.setText(R.id.tv_book_name, novel.getName());
                helper.setText(R.id.tv_authorName, novel.getAuthor());
                break;
        }
    }
}
