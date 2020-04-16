package com.huli.foxread.entity;

/**
 * 好评佳作
 */
public class FavEntity {
    private String id;
    private String novel_id;
    private int status;
    private int weigh;
    private BookEntity2 profileNovel;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNovel_id() {
        return novel_id;
    }

    public void setNovel_id(String novel_id) {
        this.novel_id = novel_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getWeigh() {
        return weigh;
    }

    public void setWeigh(int weigh) {
        this.weigh = weigh;
    }

    public BookEntity2 getProfileNovel() {
        return profileNovel;
    }

    public void setProfileNovel(BookEntity2 profileNovel) {
        this.profileNovel = profileNovel;
    }
}
