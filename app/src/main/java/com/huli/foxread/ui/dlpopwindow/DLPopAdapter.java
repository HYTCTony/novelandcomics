package com.huli.foxread.ui.dlpopwindow;

import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 项目名称：FoxRead
 * 创建人：Bill
 * 创建时间：2020/7/7  11:25
 * 备注：
 */
public class DLPopAdapter extends BaseQuickAdapter<DLPopItem, BaseViewHolder> {

    public DLPopAdapter() {
        super(R.layout.pop_item);
    }

    public DLPopAdapter(@Nullable List<DLPopItem> data) {
        super(R.layout.pop_item, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, DLPopItem mItem) {
        // 设置图标
        ImageView ivIcon = holder.getView(R.id.img_icon);
        ivIcon.setImageResource(mItem.getIcon());
        // 设置文本
        TextView tvTxt = holder.getView(R.id.txt);
        holder.setText(R.id.txt, mItem.getText());
        // 设置图标颜色
        int cd = mItem.getColor();
        if (cd < 0x01000000) {
            cd = 0xff000000 + cd;
        }
        ivIcon.setColorFilter(cd);
        // 设置下拉线颜色
        int cc = cd & 0x33ffffff;
        holder.setBackgroundColor(R.id.view_line, cc);
        // 设置文本颜色
        tvTxt.setTextColor(cd);
        // 如果是最后一项就不显示下划线
        if (holder.getLayoutPosition() == getItemCount() - 1) {
            holder.setGone(R.id.view_line, true);
        } else {
            holder.setVisible(R.id.view_line, true);
        }
        // 如果没有图标文字就居中显示
        if (mItem.getIcon() == 0) {
            tvTxt.setGravity(Gravity.CENTER);
            ivIcon.setVisibility(View.GONE);
        } else {
            tvTxt.setGravity(Gravity.CENTER_VERTICAL);
            ivIcon.setVisibility(View.VISIBLE);
        }
    }
}
