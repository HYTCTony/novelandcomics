package com.huli.foxread.entity;

import java.io.Serializable;

public class SearchEntity implements Serializable {

    private String id;
    private String novel_id;
    private int number;
    private int is_top;
    private BookEntity2 profile_novel;

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

    public int getIs_top() {
        return is_top;
    }

    public void setIs_top(int is_top) {
        this.is_top = is_top;
    }

    public BookEntity2 getProfile_novel() {
        return profile_novel;
    }

    public void setProfile_novel(BookEntity2 profile_novel) {
        this.profile_novel = profile_novel;
    }
}
