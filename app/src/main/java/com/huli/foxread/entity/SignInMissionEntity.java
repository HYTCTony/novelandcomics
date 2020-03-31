package com.huli.foxread.entity;

public class SignInMissionEntity {
    private String id;
    private String progress;            //按钮文字
    private int status;                 //1可以去签到
    private String http_logo_image;
    private String link;
    private String type_name;
    private String content;
    private int vip_reward;
    private int need_login;
    private int jump;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getHttp_logo_image() {
        return http_logo_image;
    }

    public void setHttp_logo_image(String http_logo_image) {
        this.http_logo_image = http_logo_image;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
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

    public int getVip_reward() {
        return vip_reward;
    }

    public void setVip_reward(int vip_reward) {
        this.vip_reward = vip_reward;
    }

    public int getNeed_login() {
        return need_login;
    }

    public void setNeed_login(int need_login) {
        this.need_login = need_login;
    }

    public int getJump() {
        return jump;
    }

    public void setJump(int jump) {
        this.jump = jump;
    }
}
