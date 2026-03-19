package com.nnmedia.read.ui.adapters;

import android.graphics.drawable.Drawable;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.nnmedia.novel.R;
import com.nnmedia.read.entity.SMsgBean;
import com.nnmedia.read.entity.SMsgPmiBean;
import com.nnmedia.read.utils.DateTimeUtil;
import com.nnmedia.read.utils.GlideUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class MsgNotifyAdapter extends BaseQuickAdapter<SMsgBean, BaseViewHolder> implements LoadMoreModule {


    public MsgNotifyAdapter() {
        super(R.layout.recy_list_item_msg_notify);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, SMsgBean item) {
        SMsgPmiBean msgIssue = item.getProfileMessageIssue();
        GlideUtil.loadCircle(getContext(), holder.getView(R.id.iv_sender_headImg), msgIssue.getHttp_image());
        TextView tvTitle = holder.getView(R.id.tv_sender_nickname);
        tvTitle.setText(msgIssue.getName());
        holder.setText(R.id.tv_msg_content, msgIssue.getContent());
        holder.setText(R.id.tv_msg_send_time, DateTimeUtil.formatDateTime(item.getCreatetime() * 1000, DateTimeUtil.DF_YYYY_MM_DD_HH_MM));
//        holder.setText(R.id.tv_sub_msg_count, "12");
        if (item.getStatus() == 1) {
            tvTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        } else {
            Drawable drawableLeft = ContextCompat.getDrawable(getContext(), R.drawable.ic_msg_new);
            tvTitle.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null, null);
        }
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, SMsgBean item, @NotNull List<?> payloads) {
        super.convert(holder, item, payloads);
        if (payloads.get(0).equals("read_status")) {
            TextView tvTitle = holder.getView(R.id.tv_sender_nickname);
            if (item.getStatus() == 1) {
                tvTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            } else {
                Drawable drawableLeft = ContextCompat.getDrawable(getContext(), R.drawable.ic_msg_new);
                tvTitle.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null, null);
            }
        }
    }
}
