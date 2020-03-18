package com.huli.foxread.entity;

import java.util.List;

public class InviteFriendsPageBean {
    private int sum_man;                            //我邀请的好友人数
    private double money;                           //我的RMB余额
    private List<InviteRewardBean> list;            //奖励（第几天）

    public int getSum_man() {
        return sum_man;
    }

    public void setSum_man(int sum_man) {
        this.sum_man = sum_man;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public List<InviteRewardBean> getList() {
        return list;
    }

    public void setList(List<InviteRewardBean> list) {
        this.list = list;
    }
}
