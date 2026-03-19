package com.nnmedia.read.ui.adapters;

import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.nnmedia.novel.R;
import com.nnmedia.read.entity.CategoryEntity;
import com.nnmedia.read.entity.sections.CommonSection;
import com.nnmedia.read.utils.DensityUtils;
import com.nnmedia.read.utils.GlideUtil;

import java.util.List;

import androidx.annotation.NonNull;

/**
 * 书籍分类
 */
public class SectionClassifyAdapter extends BaseSectionQuickAdapter<CommonSection<CategoryEntity>, BaseViewHolder> {

    public SectionClassifyAdapter() {
        super(R.layout.recy_section_head_view_book_classify);
        setNormalLayout(R.layout.recy_list_item_book_category);
    }

    @Override
    protected void convertHeader(@NonNull BaseViewHolder holder, CommonSection<CategoryEntity> item) {
        holder.setText(R.id.tv_section_group_title, item.getTitle());
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, CommonSection<CategoryEntity> item) {
        CategoryEntity itemCate = item.getObject();
        helper.setText(R.id.tv_category_name_parent, itemCate.getName());
        GlideUtil.loadRoundRect(getContext(), helper.getView(R.id.iv_parent_category_img), itemCate.getHttp_image(), DensityUtils.dp2px(getContext(), 2));

        List<CategoryEntity> childCateList = itemCate.getList();
        if (childCateList != null) {
            String cstr = "";
            for (int i = 0; i < childCateList.size(); i++) {
                if (i == 0) {
                    cstr = childCateList.get(i).getName();
                    GlideUtil.loadRoundRect(getContext(), helper.getView(R.id.iv_child_category_img_1), childCateList.get(i).getHttp_image(),  DensityUtils.dp2px(getContext(), 2));
                } else if (i == 1) {
                    cstr = cstr + " | " + childCateList.get(i).getName();
                    GlideUtil.loadRoundRect(getContext(), helper.getView(R.id.iv_child_category_img_2), childCateList.get(i).getHttp_image(),  DensityUtils.dp2px(getContext(), 2));
                    break;
                }
            }
            helper.setText(R.id.tv_category_name_child, cstr);
        }
    }

}
