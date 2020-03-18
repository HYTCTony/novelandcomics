package com.huli.foxread.entity;

public class NormalSignInTaskEntity extends WelfareTaskEntity{
    private int sign_successions;           //签到成功1，未签到0

    public int getSign_successions() {
        return sign_successions;
    }

    public void setSign_successions(int sign_successions) {
        this.sign_successions = sign_successions;
    }
}
