package com.nnmedia.read.entity;

import android.text.TextUtils;

import com.nnmedia.read.utils.AESCBCUtil;

import java.io.Serializable;

public class ChapterBean implements Serializable {
    private int id;
    private String name;                    //章节名
    private String novel_id;                //小说id
    private String content_url;
    private int chapter;                    //章节
    private String number;
    private String links;
    private String http_links;
    private String http_content_url;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        String ss = AESCBCUtil.decrypt2(name, AESCBCUtil.getKey(), AESCBCUtil.getIv());
        if (TextUtils.isEmpty(ss)) {
            return name;
        } else {
            return ss;
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNovel_id() {
        return novel_id;
    }

    public void setNovel_id(String novel_id) {
        this.novel_id = novel_id;
    }

    public String getContent_url() {
        return content_url;
    }

    public void setContent_url(String content_url) {
        this.content_url = content_url;
    }

    public int getChapter() {
        return chapter;
    }

    public void setChapter(int chapter) {
        this.chapter = chapter;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getLinks() {
        return links;
    }

    public void setLinks(String links) {
        this.links = links;
    }

    public String getHttp_links() {
        return http_links;
    }

    public void setHttp_links(String http_links) {
        this.http_links = http_links;
    }

    public String getHttp_content_url() {
        return http_content_url;
    }

    public void setHttp_content_url(String http_content_url) {
        this.http_content_url = http_content_url;
    }
}
