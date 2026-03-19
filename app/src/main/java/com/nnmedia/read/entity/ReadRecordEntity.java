package com.nnmedia.read.entity;

import android.text.TextUtils;

import com.nnmedia.read.utils.AESCBCUtil;

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
    private int exist_bookshelf;            // 1在书架  0不在书架
    private BookEntity profileNovel;

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
        String ss = AESCBCUtil.decrypt2(chapter_name, AESCBCUtil.getKey(), AESCBCUtil.getIv());
        if (TextUtils.isEmpty(ss)) {
            return chapter_name;
        } else {
            return ss;
        }
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

    public int getExist_bookshelf() {
        return exist_bookshelf;
    }

    public void setExist_bookshelf(int exist_bookshelf) {
        this.exist_bookshelf = exist_bookshelf;
    }

    public BookEntity getProfileNovel() {
        return profileNovel;
    }

    public void setProfileNovel(BookEntity profileNovel) {
        this.profileNovel = profileNovel;
    }
}
