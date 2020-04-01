package com.huli.foxread.ui.adapters;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.R;
import com.huli.foxread.entity.InvitedFriendBean;
import com.huli.foxread.entity.InvitedFriendInfo;
import com.huli.foxread.utils.DateTimeUtil;
import com.huli.foxread.utils.GlideUtil;

import androidx.annotation.NonNull;

public class InvitedFriendsAdapter extends BaseQuickAdapter<InvitedFriendInfo, BaseViewHolder> implements LoadMoreModule {

    public InvitedFriendsAdapter() {
        super(R.layout.recy_list_item_invited_friends);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, InvitedFriendInfo data) {

        holder.setText(R.id.tv_invited_time_friend, DateTimeUtil.formatDateTime(data.getCreatetime() * 1000, "yyyy年MM月dd日 HH:mm:ss"));

        InvitedFriendBean profileUser = data.getProfileUser();
        if (profileUser != null) {
            holder.setText(R.id.tv_username_friend, profileUser.getUsername());
            GlideUtil.loadCircle(getContext(), holder.getView(R.id.iv_headImg_friend), profileUser.getHttp_avatar());
        }
    }
}
