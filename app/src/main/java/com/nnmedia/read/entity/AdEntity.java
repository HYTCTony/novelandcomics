package com.nnmedia.read.entity;

import java.io.Serializable;

public class AdEntity implements Serializable {
    private int id;
    private String advertiser;//发布者
    private String name;//广告名称
    private String introduce;//描述
    private int type;  //视频1  图片2
    private int jump;  //应用外跳转1  应用内跳转2
    private String video;  //视频地址
    private String imageText;  //文件地址
    private String link;  //跳转地址

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAdvertiser() {
        return advertiser;
    }

    public void setAdvertiser(String advertiser) {
        this.advertiser = advertiser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getJump() {
        return jump;
    }

    public void setJump(int jump) {
        this.jump = jump;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getImageText() {
        return imageText;
    }

    public void setImageText(String imageText) {
        this.imageText = imageText;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
