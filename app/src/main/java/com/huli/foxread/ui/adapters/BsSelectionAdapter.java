package com.huli.foxread.ui.adapters;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.R;
import com.huli.foxread.entity.BookEntity;
import com.huli.foxread.entity.RvTitleEntity;
import com.huli.foxread.entity.sections.HpSection;
import com.huli.foxread.utils.DensityUtils;
import com.huli.foxread.utils.FigureProcessor;
import com.huli.foxread.utils.GlideUtil;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

/**
 * 书城精选---分组---多布局
 */
public class BsSelectionAdapter extends BaseSectionQuickAdapter<HpSection, BaseViewHolder> implements LoadMoreModule {

    public BsSelectionAdapter() {
        super(R.layout.recy_module_title_bar_normal);
        // 绑定 layout 对应的 type
        addItemType(HpSection.SE_TYPE_HOT_BILLBOARD, R.layout.recy_bs_grid_item_hot_today);
        addItemType(HpSection.SE_TYPE_CATE_EXC_WORKS, R.layout.recy_bs_grid_item_book_cover_type);
        addItemType(HpSection.SE_TYPE_HOT_SEARCH, R.layout.recy_bs_grid_item_att_top_search);
        addItemType(HpSection.SE_TYPE_GRID_NOR, R.layout.recy_bs_grid_item_book_normal);
        addItemType(HpSection.SE_TYPE_FIRST_ITEM, R.layout.recy_multi_item_book_firstitem);
        addItemType(HpSection.SE_TYPE_LIST, R.layout.recy_bs_list_item_book_normal);
    }

    public BsSelectionAdapter(List<HpSection> datas) {
        super(R.layout.recy_module_title_bar_normal, datas);
        // 绑定 layout 对应的 type
        addItemType(HpSection.SE_TYPE_HOT_BILLBOARD, R.layout.recy_bs_grid_item_hot_today);
        addItemType(HpSection.SE_TYPE_CATE_EXC_WORKS, R.layout.recy_bs_grid_item_book_cover_type);
        addItemType(HpSection.SE_TYPE_HOT_SEARCH, R.layout.recy_bs_grid_item_att_top_search);
        addItemType(HpSection.SE_TYPE_GRID_NOR, R.layout.recy_bs_grid_item_book_normal);
        addItemType(HpSection.SE_TYPE_FIRST_ITEM, R.layout.recy_multi_item_book_firstitem);
        addItemType(HpSection.SE_TYPE_LIST, R.layout.recy_bs_list_item_book_normal);
    }

    @Override
    protected void convertHeader(@NonNull BaseViewHolder holder, HpSection data) {
        Object object = data.getObject();
        if (object instanceof RvTitleEntity) {
            RvTitleEntity entity = (RvTitleEntity) object;
            holder.setText(R.id.iv_title_book_hp, entity.getTitle());
            if (TextUtils.isEmpty(entity.getSubTitle())) {
                holder.setGone(R.id.tv_sub_title, true);
            } else {
                holder.setText(R.id.tv_sub_title, entity.getSubTitle());
                holder.setVisible(R.id.tv_sub_title, true);
            }
            LinearLayout linearLayout = holder.getView(R.id.ll_star_tag_content_view);
            linearLayout.removeAllViews();
            String labelStr = entity.getLabel();
            if (!TextUtils.isEmpty(labelStr)) {
                String[] split = labelStr.split("，");
                for (String str : split) {
                    TextView textView = new TextView(getContext());
                    Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_star_brown_12dp);
                    if (drawable != null) {
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    }
                    textView.setCompoundDrawables(drawable, null, null, null);
                    textView.setCompoundDrawablePadding(DensityUtils.dp2px(getContext(), 4));
                    textView.setTextColor(ContextCompat.getColor(getContext(), R.color.txt_dark_gold));
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                    textView.setGravity(Gravity.CENTER_VERTICAL);
                    textView.setPadding(DensityUtils.dp2px(getContext(), 8), 0, 0, 0);
                    textView.setText(str);
                    linearLayout.addView(textView);
                }
            }
        }
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, HpSection data) {
        Object object = data.getObject();
        if (object instanceof BookEntity) {
            BookEntity novel = (BookEntity) object;
            switch (holder.getItemViewType()) {
                case HpSection.SE_TYPE_HOT_BILLBOARD://今日大热榜类型的
                    GlideUtil.loadRoundRect(getContext(), holder.getView(R.id.iv_book_cover), novel.getHttp_image(), DensityUtils.dp2px(getContext(), 2));
                    holder.setText(R.id.tv_book_title, novel.getName());
                    holder.setText(R.id.tv_book_heat_rate, FigureProcessor.formatHeat(getContext(), novel.getHeat()));
                    holder.setText(R.id.tv_flag_rank, String.valueOf(novel.getRank()));
                    if (novel.getRank() <= 3) {
                        holder.setBackgroundResource(R.id.tv_flag_rank, R.mipmap.icon_rank_top3_txtbg);
                    } else {
                        holder.setBackgroundResource(R.id.tv_flag_rank, R.mipmap.icon_rank_normal_txtbg);
                    }
                    break;
                case HpSection.SE_TYPE_CATE_EXC_WORKS://分类佳作类型的
                    GlideUtil.loadRoundRect(getContext(), holder.getView(R.id.iv_book_cover), novel.getHttp_image());
                    holder.setText(R.id.tv_book_name, novel.getName());
                    holder.setText(R.id.tv_book_type, novel.getClassify_name());
                    break;
                case HpSection.SE_TYPE_HOT_SEARCH://实时热搜类型的
                    GlideUtil.loadRoundRect(getContext(), holder.getView(R.id.iv_book_cover), novel.getHttp_image());
                    holder.setText(R.id.tv_book_name, novel.getName());
                    holder.setText(R.id.tv_search_count, String.format(getContext().getString(R.string.txt_search_count_x), FigureProcessor.formatNum(getContext(), novel.getNumber())));
                    break;
                case HpSection.SE_TYPE_FIRST_ITEM://首本书独占一行类型的
                    GlideUtil.loadRoundRect(getContext(), holder.getView(R.id.iv_book_cover), novel.getHttp_image());
                    holder.setText(R.id.tv_book_title, novel.getName());
                    holder.setText(R.id.tv_book_score, novel.getScore() + getContext().getString(R.string.unit_score));
                    holder.setText(R.id.tv_book_description, novel.getIntroduce());
                    break;
                case HpSection.SE_TYPE_LIST://List类型的
                    GlideUtil.loadRoundRect(getContext(), holder.getView(R.id.iv_book_cover), novel.getHttp_image());
                    holder.setText(R.id.tv_book_title, novel.getName());
                    holder.setText(R.id.tv_book_score, novel.getScore() + getContext().getString(R.string.unit_score));
                    holder.setText(R.id.tv_book_description, novel.getIntroduce());
                    holder.setText(R.id.tv_book_word_count, FigureProcessor.formatWordNum(getContext(), novel.getWord()));
                    holder.setText(R.id.tv_book_be_over, novel.getIs_end() == 1 ? R.string.txt_end : R.string.txt_serialize);
                    holder.setText(R.id.tv_book_author_pen_name_and_book_kind, novel.getAuthor());
                    /*List<String> tags = item.getTag();
                    if (tags != null && tags.size() > 0) {
                        helper.setText(R.id.tv_book_author_pen_name_and_book_kind, item.getAuthor() + "·" + tags.get(0));
                    } else {
                                helper.setText(R.id.tv_book_author_pen_name_and_book_kind, item.getAuthor());
                    }*/
                    break;
                case HpSection.SE_TYPE_GRID_NOR://普通griditem类型的
                    GlideUtil.loadRoundRect(getContext(), holder.getView(R.id.iv_book_cover), novel.getHttp_image());
                    holder.setText(R.id.tv_book_name, novel.getName());
                    holder.setText(R.id.tv_authorName, novel.getAuthor());
                    break;
                default:
                    break;

            }
        }
    }
}
