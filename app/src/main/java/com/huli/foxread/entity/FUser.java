package com.huli.foxread.entity;

import java.io.Serializable;

public class FUser implements Serializable {
    private String id = "";
    private String username = "";
    private String mobile = "";
    private String avatar = "";
    private String http_avatar = "";    //头像
    private int gender = -1;            //性别:0=男,1=女 （未填写性别为-1）
    private String distribution = "";   //红包码(邀请码)
    private int is_new_man = 0;         //是否为新用户:0=不是,1=是
    private int is_vip = 0;             //是否为vip:0=不是,1=是
    private long vip_end = 0;           //会员结束时间
    private int is_visitor = 1;         //是否为游客:0=不是,1=是
    private int is_invited = -1;        //是否已填写邀请码:0未被邀请，1已被邀请，-1游客身份无法邀请
    private int is_wx = 0;               //是否绑定微信:0=不是,1=是

    //游客
    public FUser() {
    }

    public FUser(String id, String username, String mobile, String avatar, String http_avatar, int gender, String distribution, int is_new_man, int is_vip, long vip_end, int is_visitor, int is_invited, int is_wx) {
        this.id = id;
        this.username = username;
        this.mobile = mobile;
        this.avatar = avatar;
        this.http_avatar = http_avatar;
        this.gender = gender;
        this.distribution = distribution;
        this.is_new_man = is_new_man;
        this.is_vip = is_vip;
        this.vip_end = vip_end;
        this.is_visitor = is_visitor;
        this.is_invited = is_invited;
        this.is_wx = is_wx;
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

    public int getIs_wx() {
        return is_wx;
    }

    public void setIs_wx(int is_wx) {
        this.is_wx = is_wx;
    }
}
