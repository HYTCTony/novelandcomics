package com.huli.foxread.entity;

/**
 * 福利任务---子任务（阅读任务）
 */
public class ReadSubTaskBean {
    private String id;
    private int duration;
    private String name;
    private int reward;
    private int complete_task;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getReward() {
        return reward;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getComplete_task() {
        return complete_task;
    }

    public void setComplete_task(int complete_task) {
        this.complete_task = complete_task;
    }
}
