package com.huli.foxread.ui.adapters;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.R;
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
        switch (helper.getItemViewType()) {
            case ForestallNewMultiEntity.DETAILED:
                GlideUtil.loadRoundRect(getContext(), helper.getView(R.id.iv_book_cover), item.getHttp_image(), 0);
                helper.setText(R.id.tv_book_title, item.getNovel_name());
                helper.setText(R.id.tv_book_score, item.getScore() + getContext().getString(R.string.unit_score));
                helper.setText(R.id.tv_book_description, item.getIntroduce());
                break;
            case ForestallNewMultiEntity.ITEM_FIRST:
                GlideUtil.loadRoundRect(getContext(), helper.getView(R.id.iv_book_cover), item.getHttp_image(), 0);
                helper.setText(R.id.tv_book_name, item.getNovel_name());
                helper.setText(R.id.tv_authorName, item.getAuthor());
                break;
        }
    }
}
