package com.huli.foxread.entity;

/**
 * 实时热搜
 */
public class HotSearchEntity {
    private String id;
    private String novel_id;
    private int number;          //搜索次数
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

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public BookEntity2 getProfileNovel() {
        return profileNovel;
    }

    public void setProfileNovel(BookEntity2 profileNovel) {
        this.profileNovel = profileNovel;
    }
}
