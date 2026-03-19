package com.nnmedia.read.entity;

import android.text.TextUtils;

import com.nnmedia.read.utils.AESCBCUtil;

/**
 * 排行榜书籍
 */
public class RankBookEntity {
    private String id;
    private String novel_id;
    private String heat;                   //热度
    private int sort;
    private int category;
    private int type;
    private int status;
    private String novel_name;
    private String author;
    private String http_image;
    private String introduce;
    private String reason;
    private String classify_name;
    private int is_end;
    private int word;

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

    public String getHeat() {
        return heat;
    }

    public void setHeat(String heat) {
        this.heat = heat;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public String getHttp_image() {
        return http_image;
    }

    public void setHttp_image(String http_image) {
        this.http_image = http_image;
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

    public String getReason() {
        String ss = AESCBCUtil.decrypt2(reason, AESCBCUtil.getKey(), AESCBCUtil.getIv());
        if (TextUtils.isEmpty(ss)) {
            return reason;
        } else {
            return ss;
        }
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getClassify_name() {
        String ss = AESCBCUtil.decrypt2(classify_name, AESCBCUtil.getKey(), AESCBCUtil.getIv());
        if (TextUtils.isEmpty(ss)) {
            return classify_name;
        } else {
            return ss;
        }
    }

    public void setClassify_name(String classify_name) {
        this.classify_name = classify_name;
    }

    public int getIs_end() {
        return is_end;
    }

    public void setIs_end(int is_end) {
        this.is_end = is_end;
    }

    public int getWord() {
        return word;
    }

    public void setWord(int word) {
        this.word = word;
    }
}
