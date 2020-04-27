package com.huli.foxread.ui.adapters;

import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.R;
import com.huli.foxread.entity.HighScoresEntity;
import com.huli.foxread.utils.FigureProcessor;
import com.huli.foxread.utils.GlideUtil;

import androidx.annotation.NonNull;

public class BooksHighScoreAdapter extends BaseQuickAdapter<HighScoresEntity, BaseViewHolder> implements LoadMoreModule {

    public BooksHighScoreAdapter() {
        super(R.layout.recy_list_item_book_normal);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, HighScoresEntity data) {
        GlideUtil.loadRoundRect(getContext(), helper.getView(R.id.iv_book_cover), data.getHttp_image(), 0);
        helper.setText(R.id.tv_book_title, data.getNovel_name());
        helper.setText(R.id.tv_book_score, data.getScore() + getContext().getString(R.string.unit_score));
        helper.setText(R.id.tv_book_description, data.getIntroduce());

        String tag1 = data.getTag();        //作者名字后面的标签
        if (TextUtils.isEmpty(tag1)) {
            helper.setText(R.id.tv_book_author_pen_name_and_book_kind, data.getAuthor());
        }else {
            helper.setText(R.id.tv_book_author_pen_name_and_book_kind, data.getAuthor() + "·" + tag1);
        }
        helper.setText(R.id.tv_book_word_count, FigureProcessor.formatWordNum(getContext(), data.getWord()));
        helper.setText(R.id.tv_book_tag, data.getIs_end() == 1 ? R.string.txt_end : R.string.txt_serialize);
    }
}
