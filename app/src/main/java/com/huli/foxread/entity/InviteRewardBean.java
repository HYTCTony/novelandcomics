package com.huli.foxread.entity;

public class InviteRewardBean {
    private String id;
    private String type_name;               //第几天
    private String content;                 //阅读时间
    private double money;                   //奖励
    private String http_logo_image;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType_name() {
        return type_name;
    }

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public String getHttp_logo_image() {
        return http_logo_image;
    }

    public void setHttp_logo_image(String http_logo_image) {
        this.http_logo_image = http_logo_image;
    }
}
