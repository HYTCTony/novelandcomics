package com.huli.foxread.entity;

import java.io.Serializable;

public class FUser implements Serializable {
    private String id = "";
    private String username = "";
    private String nickname = "";
    private String mobile = "";
    private String avatar = "";
    private String http_avatar = "";
    private int gender = 0;             //性别:0=男,1=女 （未填写性别为-1）
    private double money = 0d;          //邀请人得到的RMB
    private int score = 0;              //积分(就是金币，可以转换成RMB)
    private int today_score = 0;
    private String distribution = "";   //红包码(邀请码)
    private int is_new_man = 0;         //是否为新用户:0=不是,1=是
    private int is_vip = 0;             //是否为vip:0=不是,1=是
    private long vip_end = 0;           //会员结束时间
    private int is_visitor = 1;         //是否为游客:0=不是,1=是
    private int is_invited = -1;        //是否已填写邀请码:0未被邀请，1已被邀请，-1游客身份无法邀请
    private int message_sum = 0;        //通知数量
    private String token = "";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getHttp_avatar() {
        return http_avatar;
    }

    public void setHttp_avatar(String http_avatar) {
        this.http_avatar = http_avatar;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

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

    public String getDistribution() {
        return distribution;
    }

    public void setDistribution(String distribution) {
        this.distribution = distribution;
    }

    public int getIs_new_man() {
        return is_new_man;
    }

    public void setIs_new_man(int is_new_man) {
        this.is_new_man = is_new_man;
    }

    public int getIs_vip() {
        return is_vip;
    }

    public void setIs_vip(int is_vip) {
        this.is_vip = is_vip;
    }

    public long getVip_end() {
        return vip_end;
    }

    public void setVip_end(long vip_end) {
        this.vip_end = vip_end;
    }

    public int getIs_visitor() {
        return is_visitor;
    }

    public void setIs_visitor(int is_visitor) {
        this.is_visitor = is_visitor;
    }

    public int getIs_invited() {
        return is_invited;
    }

    public void setIs_invited(int is_invited) {
        this.is_invited = is_invited;
    }

    public int getMessage_sum() {
        return message_sum;
    }

    public void setMessage_sum(int message_sum) {
        this.message_sum = message_sum;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
