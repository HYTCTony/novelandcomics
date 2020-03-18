package com.huli.foxread.entity;

/**
 * 提现（现金余额）选项
 */
public class WithdrawalOptionEntity {
    private String id;
    private String title;           //金币提现的---"提现10元"
    private double money;           //金币提现的
    private String remarks;         //金币提现的---"需100.000金币"
    private int need_score;         //金币提现的
    private double need_money;      //现金余额提现 显示

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public int getNeed_score() {
        return need_score;
    }

    public void setNeed_score(int need_score) {
        this.need_score = need_score;
    }

    public double getNeed_money() {
        return need_money;
    }

    public void setNeed_money(double need_money) {
        this.need_money = need_money;
    }
}
