package com.huli.foxread.ui.adapters;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.R;
import com.huli.foxread.entity.SMsgBean;
import com.huli.foxread.utils.GlideUtil;

import androidx.annotation.NonNull;

public class MsgNotifyAdapter extends BaseQuickAdapter<SMsgBean, BaseViewHolder> {


    public MsgNotifyAdapter() {
        super(R.layout.recy_list_item_msg_notify);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, SMsgBean item) {
        GlideUtil.loadCircle(getContext(), holder.findView(R.id.iv_sender_headImg), item.getHttp_image());
        holder.setText(R.id.tv_sender_nickname, item.getName());
        holder.setText(R.id.tv_msg_content, "一代仙君为天道不容，强行超脱之...");
        holder.setText(R.id.tv_msg_send_time, "20:26");
        holder.setText(R.id.tv_sub_msg_count, "12");
    }
}
