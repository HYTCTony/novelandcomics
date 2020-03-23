package com.huli.foxread.entity;

public class BannerADEntity {
    private String id;
    private String title;
    private String link;            //链接
    private String imageText;       //图片
    private int jump;               //0打开浏览器， 1应用内跳转
    private int need_login;         //0不需要登录，1需要登录才有效

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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImageText() {
        return imageText;
    }

    public void setImageText(String imageText) {
        this.imageText = imageText;
    }

    public int getJump() {
        return jump;
    }

    public void setJump(int jump) {
        this.jump = jump;
    }

    public int getNeed_login() {
        return need_login;
    }

    public void setNeed_login(int need_login) {
        this.need_login = need_login;
    }
}
