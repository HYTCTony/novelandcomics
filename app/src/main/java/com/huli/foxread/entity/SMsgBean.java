package com.huli.foxread.entity;

public class SMsgBean {
    private String id;
    private String message_id;
    private String user_id;
    private int status;
    private long createtime;
    private SMsgPmiBean profileMessageIssue;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }

    public SMsgPmiBean getProfileMessageIssue() {
        return profileMessageIssue;
    }

    public void setProfileMessageIssue(SMsgPmiBean profileMessageIssue) {
        this.profileMessageIssue = profileMessageIssue;
    }
}
