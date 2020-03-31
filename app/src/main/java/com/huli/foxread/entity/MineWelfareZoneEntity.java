package com.huli.foxread.entity;

public class MineWelfareZoneEntity extends BannerADEntity{
    private String name;
    private String gradation_start;
    private String gradation_end;
    private int number;
    private String http_image;
    private String link;            //链接
    private int jump;               //0打开浏览器， 1应用内跳转
    private int need_login;         //0不需要登录，1需要登录才有效

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGradation_start() {
        return gradation_start;
    }

    public void setGradation_start(String gradation_start) {
        this.gradation_start = gradation_start;
    }

    public String getGradation_end() {
        return gradation_end;
    }

    public void setGradation_end(String gradation_end) {
        this.gradation_end = gradation_end;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getHttp_image() {
        return http_image;
    }

    public void setHttp_image(String http_image) {
        this.http_image = http_image;
    }

    @Override
    public String getLink() {
        return link;
    }

    @Override
    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public int getJump() {
        return jump;
    }

    @Override
    public void setJump(int jump) {
        this.jump = jump;
    }

    @Override
    public int getNeed_login() {
        return need_login;
    }

    @Override
    public void setNeed_login(int need_login) {
        this.need_login = need_login;
    }
}
