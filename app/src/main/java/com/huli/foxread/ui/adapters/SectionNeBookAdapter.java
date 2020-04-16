package com.huli.foxread.ui.adapters;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.GlideApp;
import com.huli.foxread.R;
import com.huli.foxread.entity.EndNewBookEntity;
import com.huli.foxread.entity.sections.NEbookSection;

import androidx.annotation.NonNull;

public class SectionNeBookAdapter extends BaseSectionQuickAdapter<NEbookSection<EndNewBookEntity>, BaseViewHolder> {

    public SectionNeBookAdapter() {
        super(R.layout.recy_section_head_view_newbook);
        setNormalLayout(R.layout.recy_section_grid_item_book_img_name);
        addChildClickViewIds(R.id.tv_asBtn_get_more);
    }

    @Override
    protected void convertHeader(@NonNull BaseViewHolder holder, NEbookSection<EndNewBookEntity> item) {
        holder.setText(R.id.tv_section_group_title, item.getName());
        holder.setGone(R.id.tv_asBtn_get_more, !item.isMore());
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, NEbookSection<EndNewBookEntity> item) {
        helper.setText(R.id.tv_book_name, item.getObject().getName());
        float score = item.getObject().getScore();
        if (score > 0) {
            helper.setText(R.id.tv_book_score, score + getContext().getString(R.string.unit_score));
            helper.setVisible(R.id.tv_book_score, true);
        } else {
            helper.setGone(R.id.tv_book_score, true);
        }
        GlideApp.with(getContext())
                .load(item.getObject().getHttp_image())
                .placeholder(R.mipmap.img_holder_rect)
                .error(R.mipmap.img_holder_rect)
                .into((ImageView) helper.getView(R.id.iv_book_cover));
    }

}
