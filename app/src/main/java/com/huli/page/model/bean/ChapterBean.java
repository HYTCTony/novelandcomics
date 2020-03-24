package com.huli.page.model.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;

@Entity
public class ChapterBean implements Serializable {
    private static final long serialVersionUID = 56423411313L;

    @Id
    private String id;
    private String content;
    private String links;
    private String name;
    private String novel_id;
    private int chapter_id;
    private String content_url;
    private String http_links;


    @Generated(hash = 313392147)
    public ChapterBean(String id, String content, String links, String name,
                       String novel_id, int chapter_id, String content_url,
                       String http_links) {
        this.id = id;
        this.content = content;
        this.links = links;
        this.name = name;
        this.novel_id = novel_id;
        this.chapter_id = chapter_id;
        this.content_url = content_url;
        this.http_links = http_links;
    }

    @Generated(hash = 1028095945)
    public ChapterBean() {
    }


    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLinks() {
        return this.links;
    }

    public void setLinks(String links) {
        this.links = links;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNovel_id() {
        return this.novel_id;
    }

    public void setNovel_id(String novel_id) {
        this.novel_id = novel_id;
    }

    public int getChapter() {
        return this.chapter_id;
    }

    public void setChapter(int chapter) {
        this.chapter_id = chapter;
    }

    public String getContent_url() {
        return this.content_url;
    }

    public void setContent_url(String content_url) {
        this.content_url = content_url;
    }

    public String getHttp_links() {
        return this.http_links;
    }

    public void setHttp_links(String http_links) {
        this.http_links = http_links;
    }

    public int getChapter_id() {
        return this.chapter_id;
    }

    public void setChapter_id(int chapter_id) {
        this.chapter_id = chapter_id;
    }

}