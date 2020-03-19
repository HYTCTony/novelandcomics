package com.huli.foxread.entity;

import com.huli.foxread.entity.tab.SignInDay;

import java.util.List;

public class SignDetailEntity {
    private int sign_successions;
    private int reward;
    private List<SignInDay> list;
    private int frequency;
    private WelfareTaskEntity welfare;

    public int getSign_successions() {
        return sign_successions;
    }

    public void setSign_successions(int sign_successions) {
        this.sign_successions = sign_successions;
    }

    public int getReward() {
        return reward;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }

    public List<SignInDay> getList() {
        return list;
    }

    public void setList(List<SignInDay> list) {
        this.list = list;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public WelfareTaskEntity getWelfare() {
        return welfare;
    }

    public void setWelfare(WelfareTaskEntity welfare) {
        this.welfare = welfare;
    }
}
