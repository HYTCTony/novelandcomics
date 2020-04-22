package com.huli.foxread.entity;

/**
 * 阅读记录
 */
public class ReadRecordEntity {
    private int id;
    private int user_id;
    private String novel_id;
    private int chapter_id;
    private int chapter;
    private String chapter_name;
    private long createtime;
    private boolean isSelected;
    private BookEntity2 profileNovel;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getNovel_id() {
        return novel_id;
    }

    public void setNovel_id(String novel_id) {
        this.novel_id = novel_id;
    }

    public int getChapter_id() {
        return chapter_id;
    }

    public void setChapter_id(int chapter_id) {
        this.chapter_id = chapter_id;
    }

    public int getChapter() {
        return chapter;
    }

    public void setChapter(int chapter) {
        this.chapter = chapter;
    }

    public String getChapter_name() {
        return chapter_name;
    }

    public void setChapter_name(String chapter_name) {
        this.chapter_name = chapter_name;
    }

    public long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public BookEntity2 getProfileNovel() {
        return profileNovel;
    }

    public void setProfileNovel(BookEntity2 profileNovel) {
        this.profileNovel = profileNovel;
    }
}
