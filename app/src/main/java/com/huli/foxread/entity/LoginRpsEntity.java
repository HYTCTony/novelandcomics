package com.huli.foxread.entity;

public class LoginRpsEntity {
    private String token;
    private long time_out;      //token过期时间
    private String id;          //用户ID

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getTime_out() {
        return time_out;
    }

    public void setTime_out(long time_out) {
        this.time_out = time_out;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
