package com.nnmedia.read.entity;

/**
 * 福利任务
 */
public class WelfareTaskEntity {
    private String id;                      //福利任务id
    private String name;                    //福利任务名称
    private int type;
    private String content;                 //福利任务描述
    private int status;
    private String welfare_category_id;
    private int reward;                     //福利任务奖励金币
    private int is_new_man;                 //是否新人任务
    private int frequency;                  //每日可完成任务次数
    private String number;
    private String link;                    //点击链接:h5页面填写api路径,小说内部跳转填写关键字
    private String http_logo_image;              //图标
    private int complete_task;              //完成次数，-1未完成，1已完成(
    private String buttonText;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getWelfare_category_id() {
        return welfare_category_id;
    }

    public void setWelfare_category_id(String welfare_category_id) {
        this.welfare_category_id = welfare_category_id;
    }

    public int getReward() {
        return reward;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }

    public int getIs_new_man() {
        return is_new_man;
    }

    public void setIs_new_man(int is_new_man) {
        this.is_new_man = is_new_man;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getHttp_logo_image() {
        return http_logo_image;
    }

    public void setHttp_logo_image(String http_logo_image) {
        this.http_logo_image = http_logo_image;
    }

    public int getComplete_task() {
        return complete_task;
    }

    public void setComplete_task(int complete_task) {
        this.complete_task = complete_task;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }
}
