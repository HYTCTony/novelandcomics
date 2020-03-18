package com.huli.foxread.entity;

import java.util.List;

public class WelfareNewBieTaskEntity {
    private NewBieSignInTaskEntity new_sign_in;
    private List<WelfareTaskEntity> other_new;

    public NewBieSignInTaskEntity getNew_sign_in() {
        return new_sign_in;
    }

    public void setNew_sign_in(NewBieSignInTaskEntity new_sign_in) {
        this.new_sign_in = new_sign_in;
    }

    public List<WelfareTaskEntity> getOther_new() {
        return other_new;
    }

    public void setOther_new(List<WelfareTaskEntity> other_new) {
        this.other_new = other_new;
    }
}
