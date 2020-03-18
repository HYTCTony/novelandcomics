package com.huli.foxread.entity;

import java.io.Serializable;

public class GoldCoinInfoBean implements Serializable {
    private int score;              //金币余额
    private int score_today;        //金币
    private int score_total;        //累计金币
    private double score_money;     //金币余额转换成RMB

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore_today() {
        return score_today;
    }

    public void setScore_today(int score_today) {
        this.score_today = score_today;
    }

    public int getScore_total() {
        return score_total;
    }

    public void setScore_total(int score_total) {
        this.score_total = score_total;
    }

    public double getScore_money() {
        return score_money;
    }

    public void setScore_money(double score_money) {
        this.score_money = score_money;
    }
}
