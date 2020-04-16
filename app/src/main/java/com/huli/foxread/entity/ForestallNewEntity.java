package com.huli.foxread.entity;

/**
 * 新书抢先
 */
public class ForestallNewEntity {
    private String id;
    private String novel_id;
    private int status;
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

    public BookEntity2 getProfileNovel() {
        return profileNovel;
    }

    public void setProfileNovel(BookEntity2 profileNovel) {
        this.profileNovel = profileNovel;
    }
}
