package com.nnmedia.read.entity;

public class InvitedFriendInfo {
    private String id;
    private String user_id;
    private long date;
    private long createtime;
    private InvitedFriendBean profileUser;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }

    public InvitedFriendBean getProfileUser() {
        return profileUser;
    }

    public void setProfileUser(InvitedFriendBean profileUser) {
        this.profileUser = profileUser;
    }
}
