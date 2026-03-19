package com.nnmedia.read.ui.adapters;

import android.text.TextUtils;

import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.nnmedia.novel.R;
import com.nnmedia.read.entity.BookEntity;
import com.nnmedia.read.entity.sections.NEbookSection;
import com.nnmedia.read.utils.FigureProcessor;
import com.nnmedia.read.utils.GlideUtil;

import java.util.List;

import androidx.annotation.NonNull;

/**
 * 完结/新书（分组的）
 */
public class SectionNeBookAdapter extends BaseSectionQuickAdapter<NEbookSection<BookEntity>, BaseViewHolder> {

    public SectionNeBookAdapter() {
        super(R.layout.recy_section_head_view_newbook);
        setNormalLayout(R.layout.recy_list_item_book_normal);
        addChildClickViewIds(R.id.tv_asBtn_get_more);
    }

    @Override
    protected void convertHeader(@NonNull BaseViewHolder holder, NEbookSection<BookEntity> item) {
        holder.setText(R.id.tv_section_group_title, item.getName());
        holder.setGone(R.id.tv_asBtn_get_more, !item.isMore());
        if (holder.getLayoutPosition() == 0) {
            holder.setGone(R.id.view_group_title_separator, true);
        } else {
            holder.setVisible(R.id.view_group_title_separator, true);
        }
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, NEbookSection<BookEntity> item) {
        BookEntity book = item.getObject();
        GlideUtil.loadRoundRect(getContext(), helper.getView(R.id.iv_book_cover), book.getHttp_image());
        helper.setText(R.id.tv_book_title, book.getName());
        helper.setText(R.id.tv_book_score, book.getScore() + getContext().getString(R.string.unit_score));
        helper.setText(R.id.tv_book_description, book.getIntroduce());
        helper.setText(R.id.tv_book_author_pen_name_and_book_kind, book.getAuthor());
        helper.setText(R.id.tv_book_word_count, FigureProcessor.formatWordNum(getContext(), book.getWord()));

        List<String> tags = book.getTag();
        if (tags != null && tags.size() > 0) {
            String str = tags.get(0);
            if (!TextUtils.isEmpty(str)) {
                helper.setVisible(R.id.tv_book_be_over, true);
                helper.setText(R.id.tv_book_be_over, str);
            } else {
                helper.setGone(R.id.tv_book_be_over, true);
            }
        } else {
            helper.setGone(R.id.tv_book_be_over, true);
        }
    }

}
