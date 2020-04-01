package com.huli.foxread.ui.adapters;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.R;
import com.huli.foxread.entity.SMsgBean;
import com.huli.foxread.ui.activities.SMsgPmiBean;
import com.huli.foxread.utils.DateTimeUtil;
import com.huli.foxread.utils.GlideUtil;

import androidx.annotation.NonNull;

public class MsgNotifyAdapter extends BaseQuickAdapter<SMsgBean, BaseViewHolder> implements LoadMoreModule {


    public MsgNotifyAdapter() {
        super(R.layout.recy_list_item_msg_notify);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, SMsgBean item) {
        SMsgPmiBean issue = item.getProfileMessageIssue();
        GlideUtil.loadCircle(getContext(), holder.findView(R.id.iv_sender_headImg), issue.getHttp_image());
        holder.setText(R.id.tv_sender_nickname, issue.getName());
        holder.setText(R.id.tv_msg_content, issue.getContent());
        holder.setText(R.id.tv_msg_send_time, DateTimeUtil.formatDateTime(item.getCreatetime() * 1000, DateTimeUtil.DF_YYYY_MM_DD_HH_MM));
//        holder.setText(R.id.tv_sub_msg_count, "12");
    }
}
