package com.nnmedia.read.ui.adapters;

import android.text.SpannableStringBuilder;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.nnmedia.novel.R;
import com.nnmedia.page.utils.SpanUtils;
import com.nnmedia.read.entity.RankBookEntity;
import com.nnmedia.read.utils.FigureProcessor;
import com.nnmedia.read.utils.GlideUtil;

import androidx.annotation.NonNull;

import static com.nnmedia.page.utils.StringUtils.getString;

public class MustReadAdapter extends BaseQuickAdapter<RankBookEntity, BaseViewHolder> implements LoadMoreModule {

    private int typeRank = 1;

    public void setTypeRank(int typeRank) {
        this.typeRank = typeRank;
    }

    public MustReadAdapter() {
        super(R.layout.recy_list_item_must_read);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, RankBookEntity item) {
        SpannableStringBuilder bookTagStr = new SpanUtils(getContext())
                .append(((item.getIs_end() == 1) ? getString(R.string.txt_end) : getString(R.string.txt_serialize)))
                .appendLine(" · " + FigureProcessor.formatWordNum(getContext(), item.getWord()))
                .create();

        SpannableStringBuilder reason = new SpanUtils(getContext())
                .appendImage(R.mipmap.icon_must_read_text, SpanUtils.ALIGN_BASELINE)
                .appendLine("  " + item.getReason())
                .create();

        GlideUtil.loadRoundRect(getContext(), holder.getView(R.id.iv_book_cover), item.getHttp_image());
        holder.setText(R.id.tv_book_name, item.getNovel_name());
        holder.setText(R.id.tv_book_type, bookTagStr);
        holder.setText(R.id.tv_book_classify, item.getClassify_name());
        holder.setText(R.id.tv_book_introduction, reason);

        holder.setText(R.id.tv_book_heat, item.getHeat());
        holder.setText(R.id.tv_book_heat_label, "必读指数");

        int pos = holder.getLayoutPosition();
        holder.setText(R.id.tv_ranking, String.valueOf(pos + 1));
        switch (pos) {
            case 0:
                holder.setVisible(R.id.iv_top_three_icon, true);
                holder.setVisible(R.id.tv_ranking, false);
                holder.setImageResource(R.id.iv_top_three_icon, R.drawable.ic_ranking_first_place);
                break;
            case 1:
                holder.setVisible(R.id.iv_top_three_icon, true);
                holder.setVisible(R.id.tv_ranking, false);
                holder.setImageResource(R.id.iv_top_three_icon, R.drawable.ic_ranking_second_place);
                break;
            case 2:
                holder.setVisible(R.id.iv_top_three_icon, true);
                holder.setVisible(R.id.tv_ranking, false);
                holder.setImageResource(R.id.iv_top_three_icon, R.drawable.ic_ranking_third_place);
                break;

            default:
                holder.setVisible(R.id.iv_top_three_icon, false);
                holder.setVisible(R.id.tv_ranking, true);
                break;
        }
    }
}