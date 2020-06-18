package com.huli.foxread.entity;

import java.io.Serializable;

public class FUser implements Serializable {
    private String id = "";
    private String username = "";
    private int gender;                     //性别:1=男,2=女 （未填写性别为0）
    private String distribution = "";       //红包码(邀请码)
    private String mobile = "";             //手机号码
    private int super_vip;                  //1是2不是
    private boolean is_vip;                 //是否为vip
    private long vip_end;                   //会员结束时间
    private boolean is_tourist = true;      //是否为游客
    private String http_avatar = "";        //头像
    private boolean is_invited;             //是否已填写邀请码
    private boolean is_new;                 //是否为新用户
    private boolean is_wx;                  //是否绑定微信
    private int like;                       //喜欢（1男性向书籍，2女性向书籍）

    //游客
    public FUser() {
    }

    public FUser(String id, String username, int gender, String distribution, String mobile, int super_vip, boolean is_vip, long vip_end, boolean is_tourist, String http_avatar, boolean is_invited, boolean is_new, boolean is_wx, int like) {
        this.id = id;
        this.username = username;
        this.gender = gender;
        this.distribution = distribution;
        this.mobile = mobile;
        this.super_vip = super_vip;
        this.is_vip = is_vip;
        this.vip_end = vip_end;
        this.is_tourist = is_tourist;
        this.http_avatar = http_avatar;
        this.is_invited = is_invited;
        this.is_new = is_new;
        this.is_wx = is_wx;
        this.like = like;
    }

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

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getDistribution() {
        return distribution;
    }

    public void setDistribution(String distribution) {
        this.distribution = distribution;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getSuper_vip() {
        return super_vip;
    }

    public void setSuper_vip(int super_vip) {
        this.super_vip = super_vip;
    }

    public boolean isIs_vip() {
        return is_vip;
    }

    public void setIs_vip(boolean is_vip) {
        this.is_vip = is_vip;
    }

    public long getVip_end() {
        return vip_end;
    }

    public void setVip_end(long vip_end) {
        this.vip_end = vip_end;
    }

    public boolean isIs_tourist() {
        return is_tourist;
    }

    public void setIs_tourist(boolean is_tourist) {
        this.is_tourist = is_tourist;
    }

    public String getHttp_avatar() {
        return http_avatar;
    }

    public void setHttp_avatar(String http_avatar) {
        this.http_avatar = http_avatar;
    }

    public boolean isIs_invited() {
        return is_invited;
    }

    public void setIs_invited(boolean is_invited) {
        this.is_invited = is_invited;
    }

    public boolean isIs_new() {
        return is_new;
    }

    public void setIs_new(boolean is_new) {
        this.is_new = is_new;
    }

    public boolean isIs_wx() {
        return is_wx;
    }

    public void setIs_wx(boolean is_wx) {
        this.is_wx = is_wx;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }
}
