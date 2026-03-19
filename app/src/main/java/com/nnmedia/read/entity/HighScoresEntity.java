package com.nnmedia.read.entity;

import android.text.TextUtils;

import com.nnmedia.read.utils.AESCBCUtil;

/**
 * 高分精选
 */
public class HighScoresEntity {
    private String id;
    private String novel_id;
    private float score;
    private int weigh;
    private int is_end;
    private String novel_name;
    private String author;
    private int word;
    private String introduce;
    private String http_image;
    private String tag;

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

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public int getWeigh() {
        return weigh;
    }

    public void setWeigh(int weigh) {
        this.weigh = weigh;
    }

    public int getIs_end() {
        return is_end;
    }

    public void setIs_end(int is_end) {
        this.is_end = is_end;
    }

    public String getNovel_name() {
        String ss = AESCBCUtil.decrypt2(novel_name, AESCBCUtil.getKey(), AESCBCUtil.getIv());
        if (TextUtils.isEmpty(ss)) {
            return novel_name;
        } else {
            return ss;
        }
    }

    public void setNovel_name(String novel_name) {
        this.novel_name = novel_name;
    }

    public String getAuthor() {
        String ss = AESCBCUtil.decrypt2(author, AESCBCUtil.getKey(), AESCBCUtil.getIv());
        if (TextUtils.isEmpty(ss)) {
            return author;
        } else {
            return ss;
        }
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getWord() {
        return word;
    }

    public void setWord(int word) {
        this.word = word;
    }

    public String getIntroduce() {
        String ss = AESCBCUtil.decrypt2(introduce, AESCBCUtil.getKey(), AESCBCUtil.getIv());
        if (TextUtils.isEmpty(ss)) {
            return introduce;
        } else {
            return ss;
        }
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getHttp_image() {
        return http_image;
    }

    public void setHttp_image(String http_image) {
        this.http_image = http_image;
    }

    public String getTag() {
        String ss = AESCBCUtil.decrypt2(tag, AESCBCUtil.getKey(), AESCBCUtil.getIv());
        if (TextUtils.isEmpty(ss)) {
            return tag;
        } else {
            return ss;
        }
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
