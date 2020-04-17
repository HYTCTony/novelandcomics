package com.huli.foxread.entity;

/**
 * 书的热度
 */
public class BookHeat {
    private String id;
    private String novel_id;
    private int heat;               //热度值

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

    public int getHeat() {
        return heat;
    }

    public void setHeat(int heat) {
        this.heat = heat;
    }
}
