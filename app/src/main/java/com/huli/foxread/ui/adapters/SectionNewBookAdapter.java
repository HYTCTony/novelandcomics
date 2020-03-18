package com.huli.foxread.ui.adapters;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.GlideApp;
import com.huli.foxread.R;
import com.huli.foxread.entity.BookEntity;
import com.huli.foxread.entity.sections.NEbookSection;

import androidx.annotation.NonNull;

public class SectionNewBookAdapter extends BaseSectionQuickAdapter<NEbookSection<BookEntity>, BaseViewHolder> {

    public SectionNewBookAdapter() {
        super(R.layout.recy_section_head_view_newbook);
        setNormalLayout(R.layout.recy_section_grid_item_book_img_name);
        addChildClickViewIds(R.id.tv_asBtn_get_more);
    }

    @Override
    protected void convertHeader(@NonNull BaseViewHolder holder, NEbookSection<BookEntity> item) {
        holder.setText(R.id.tv_section_group_title, item.getName());
        holder.setGone(R.id.tv_asBtn_get_more, !item.isMore());
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, NEbookSection<BookEntity> item) {
        helper.setText(R.id.tv_book_name, item.getObject().getName());
        GlideApp.with(getContext())
                .load(item.getObject().getHttp_image())
                .placeholder(R.mipmap.img_holder_rect)
                .error(R.mipmap.img_holder_rect)
                .into((ImageView) helper.getView(R.id.iv_book_cover));
    }

}
