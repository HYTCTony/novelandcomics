package com.huli.foxread.entity;

import java.util.List;

public class WelfareIndexEntity {
    private NormalSignInTaskEntity sign_in;                 //签到任务
    private WelfareNewBieTaskEntity new_man;                //新人任务
    private List<WelfareTaskEntity> day;                    //日常任务
    private List<WelfareReadTaskEntity> read;               //阅读任务
//    private List<WelfareTaskEntity> advanced;             //扩展任务

    public NormalSignInTaskEntity getSign_in() {
        return sign_in;
    }

    public void setSign_in(NormalSignInTaskEntity sign_in) {
        this.sign_in = sign_in;
    }

    public WelfareNewBieTaskEntity getNew_man() {
        return new_man;
    }

    public void setNew_man(WelfareNewBieTaskEntity new_man) {
        this.new_man = new_man;
    }

    public List<WelfareTaskEntity> getDay() {
        return day;
    }

    public void setDay(List<WelfareTaskEntity> day) {
        this.day = day;
    }

    public List<WelfareReadTaskEntity> getRead() {
        return read;
    }

    public void setRead(List<WelfareReadTaskEntity> read) {
        this.read = read;
    }

}
