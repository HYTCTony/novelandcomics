package com.huli.foxread.entity;

import java.util.List;

public class SignInDetailEntity {
    private NormalSignInEntity sign_in;
    private List<Integer> list;
    private WelfareTaskEntity welfare;

    public NormalSignInEntity getSign_in() {
        return sign_in;
    }

    public void setSign_in(NormalSignInEntity sign_in) {
        this.sign_in = sign_in;
    }

    public List<Integer> getList() {
        return list;
    }

    public void setList(List<Integer> list) {
        this.list = list;
    }

    public WelfareTaskEntity getWelfare() {
        return welfare;
    }

    public void setWelfare(WelfareTaskEntity welfare) {
        this.welfare = welfare;
    }
}
