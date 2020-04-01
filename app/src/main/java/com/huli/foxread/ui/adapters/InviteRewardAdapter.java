package com.huli.foxread.ui.adapters;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huli.foxread.R;
import com.huli.foxread.entity.InviteRewardBean;
import com.huli.foxread.utils.MoneyUtil;

import java.util.List;

public class InviteRewardAdapter extends BaseQuickAdapter<InviteRewardBean, BaseViewHolder> {

    public InviteRewardAdapter(List<InviteRewardBean> data) {
        super(R.layout.recy_gird_item_invite_reward, data);
    }

    @Override
    protected void convert(BaseViewHolder holder, InviteRewardBean data) {
        holder.setText(R.id.tv_invite_reward_name, data.getType_name());
        holder.setText(R.id.tv_invite_reward_tips, data.getContent());
        holder.setText(R.id.tv_reward_money,   MoneyUtil.formatYuan(data.getMoney()));
    }
}
