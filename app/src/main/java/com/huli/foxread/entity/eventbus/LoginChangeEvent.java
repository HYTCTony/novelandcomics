package com.huli.foxread.entity.eventbus;

public class LoginChangeEvent {
    private boolean isLogin;

    public LoginChangeEvent() {
    }

    public LoginChangeEvent(boolean isLogin) {
        this.isLogin = isLogin;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }
}
