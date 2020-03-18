package com.huli.foxread.ui.adapters;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.R;
import com.huli.foxread.entity.RankBookEntity;
import com.huli.foxread.utils.GlideUtil;

import androidx.annotation.NonNull;

public class RankingSubAdapter extends BaseQuickAdapter<RankBookEntity, BaseViewHolder> {

    public RankingSubAdapter() {
        super(R.layout.recy_list_item_ranking_sub);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, RankBookEntity item) {
        GlideUtil.loadRoundRect(getContext(), holder.getView(R.id.iv_book_cover), item.getHttp_image(), 0);
        holder.setText(R.id.tv_book_name, item.getName());
        holder.setText(R.id.tv_book_type, item.getAuthor());
        holder.setText(R.id.tv_book_heat, String.valueOf(item.getHot()));

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
