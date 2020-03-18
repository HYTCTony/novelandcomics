package com.huli.foxread.ui.adapters;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.R;

import androidx.annotation.NonNull;

public class HasInvitedFriendsAdapter extends BaseQuickAdapter<String, BaseViewHolder> {


    public HasInvitedFriendsAdapter() {
        super(R.layout.recy_list_item_has_invited_friend);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, String s) {
        holder.setText(R.id.tv_invitee_nickname, "孟德斯鸠");
        holder.setText(R.id.tv_invitee_tel, "177****9999");
        holder.setText(R.id.tv_invitation_time, "2020-02-15 15:30:35");
        holder.setText(R.id.tv_state_of_the_invitation, "邀请成功");

    }
}
