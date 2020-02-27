package com.huli.foxread.ui.adapters;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.R;
import com.huli.foxread.utils.GlideUtil;

import androidx.annotation.NonNull;

public class BooksListAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public BooksListAdapter() {
        super(R.layout.recy_list_item_book_normal);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, String item) {
        GlideUtil.loadRoundRect(getContext(), helper.getView(R.id.iv_book_cover), "url", 0);
        helper.setText(R.id.tv_book_title, "猎能者");
        helper.setText(R.id.tv_book_score, 9.3f + getContext().getString(R.string.unit_score));
        helper.setText(R.id.tv_book_description, "岳母鄙视，妻子瞧不起，亲人嘲讽所有人都觉得，自己能骑在废物赘婿发现他的手机上多");
        helper.setText(R.id.tv_book_author_pen_name, "不吃西红柿");
        helper.setText(R.id.tv_book_tag, "热血");
        helper.setText(R.id.tv_book_word_count, 128.9f + getContext().getString(R.string.unit_w));
    }
}
