package com.huli.foxread.ui.adapters;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.R;
import com.huli.foxread.utils.GlideUtil;

import androidx.annotation.NonNull;

public class SHotBooksAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public SHotBooksAdapter() {
        super(R.layout.recy_list_item_search_hot_book);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, String s) {
        GlideUtil.loadRoundRect(getContext(), holder.getView(R.id.iv_book_cover), "url", 0);
        holder.setText(R.id.tv_book_name, "撩心游戏");
        holder.setText(R.id.tv_book_introduction, "岳母鄙视，妻子瞧不起，亲人嘲讽所有人都觉得，自己能骑在废物赘婿发现他的手机上多...");
    }
}
