package com.huli.foxread.ui.adapters;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.GlideApp;
import com.huli.foxread.R;
import com.huli.foxread.entity.NbSection;
import com.huli.foxread.entity.NewBookEntity;

import androidx.annotation.NonNull;

public class SectionNewBookAdapter extends BaseSectionQuickAdapter<NbSection<NewBookEntity>, BaseViewHolder> {

    public SectionNewBookAdapter() {
        super(R.layout.recy_section_head_view_newbook);
        setNormalLayout(R.layout.recy_section_grid_item_book_img_name);
        addChildClickViewIds(R.id.tv_asBtn_get_changed);
    }

    @Override
    protected void convertHeader(@NonNull BaseViewHolder holder, NbSection<NewBookEntity> item) {
        holder.setText(R.id.tv_section_group_title, item.getTitle());
        holder.setGone(R.id.tv_asBtn_get_changed, !item.isMore());
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, NbSection<NewBookEntity> item) {
        helper.setText(R.id.tv_book_name, item.getObject().getTitle());
        GlideApp.with(getContext())
                .load(item.getObject().getCoverImgUrl())
                .placeholder(R.mipmap.img_holder_rect)
                .error(R.mipmap.img_holder_rect)
                .into((ImageView) helper.getView(R.id.iv_book_cover));
    }

}
