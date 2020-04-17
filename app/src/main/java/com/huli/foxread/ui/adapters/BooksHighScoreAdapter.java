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
        helper.setText(R.id.tv_book_description, data.getNovel_introduce());
        helper.setText(R.id.tv_book_author_pen_name, data.getNovel_author());
        helper.setText(R.id.tv_book_word_count, FigureProcessor.formatWordNum(getContext(), data.getNovel_word()));

        String tagStr = data.getNovel_tag();
        if (!TextUtils.isEmpty(tagStr)) {
            helper.setVisible(R.id.tv_book_tag, true);
            helper.setText(R.id.tv_book_tag, tagStr);
        } else {
            helper.setGone(R.id.tv_book_tag, true);
        }
    }
}
