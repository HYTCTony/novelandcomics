package com.huli.foxread.entity;

/**
 * 福利任务
 */
public class MissionEntity {
    private String id;
    private String welfare_id;
    private String progress;
    private int status;
    private int reward;
    private String type_name;
    private int vip_reward;
    private String http_logo_image;
    private String welfare_category_id;
    private String content;
    private String link;
    private int need_login;
    private int jump;
    private int sign_successions;       //（新人特有）领取签到奖励次数（-1不显示UI）

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWelfare_id() {
        return welfare_id;
    }

    public void setWelfare_id(String welfare_id) {
        this.welfare_id = welfare_id;
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

    public int getReward() {
        return reward;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }

    public String getType_name() {
        return type_name;
    }

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }

    public int getVip_reward() {
        return vip_reward;
    }

    public void setVip_reward(int vip_reward) {
        this.vip_reward = vip_reward;
    }

    public String getHttp_logo_image() {
        return http_logo_image;
    }

    public void setHttp_logo_image(String http_logo_image) {
        this.http_logo_image = http_logo_image;
    }

    public String getWelfare_category_id() {
        return welfare_category_id;
    }

    public void setWelfare_category_id(String welfare_category_id) {
        this.welfare_category_id = welfare_category_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
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

    public int getSign_successions() {
        return sign_successions;
    }

    public void setSign_successions(int sign_successions) {
        this.sign_successions = sign_successions;
    }
}
