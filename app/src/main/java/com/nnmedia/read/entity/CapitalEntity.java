package com.nnmedia.read.entity;

/**
 * 我的资产
 */
public class CapitalEntity {
    private int score;
    private int today_score;
    private int score_sum;
    private int hpoint;        //阅读点
    private double money;
    private int point;          //实时推广点
    private int point_sum;      //累计推广点
    private int proportion;     //金币兑换RMB比例  ： 如1000就是1000金币兑换1RMB
    private String account;     //提现（到）银行卡号

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getToday_score() {
        return today_score;
    }

    public void setToday_score(int today_score) {
        this.today_score = today_score;
    }

    public int getScore_sum() {
        return score_sum;
    }

    public void setScore_sum(int score_sum) {
        this.score_sum = score_sum;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public int getProportion() {
        return proportion;
    }

    public void setProportion(int proportion) {
        this.proportion = proportion;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public int getPoint_sum() {
        return point_sum;
    }

    public void setPoint_sum(int point_sum) {
        this.point_sum = point_sum;
    }

    public int getH_point() {
        return hpoint;
    }

    public void setH_point(int h_point) {
        this.hpoint = h_point;
    }
}