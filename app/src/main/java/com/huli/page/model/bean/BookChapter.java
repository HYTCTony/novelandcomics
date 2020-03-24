package com.huli.page.model.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

import java.io.Serializable;

@Entity
public class BookChapter implements Serializable {
    private static final long serialVersionUID = 56423411313L;

    @Id
    private String id;
    private String novel_id;
    private String name;
    private int chapter;
    private String content_url;
    private String http_links;

    //所属的下载任务
    private String taskName;

    //所属的书籍
    @Index
    private String bookId;

    //在书籍文件中的起始位置
    private long start;

    //在书籍文件中的终止位置
    private long end;

    @Generated(hash = 1564474551)
    public BookChapter(String id, String novel_id, String name, int chapter,
            String content_url, String http_links, String taskName, String bookId, long start,
            long end) {
        this.id = id;
        this.novel_id = novel_id;
        this.name = name;
        this.chapter = chapter;
        this.content_url = content_url;
        this.http_links = http_links;
        this.taskName = taskName;
        this.bookId = bookId;
        this.start = start;
        this.end = end;
    }

    @Generated(hash = 1481387400)
    public BookChapter() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
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
        return this.chapter;
    }

    public void setChapter(int chapter) {
        this.chapter = chapter;
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

    public String getTaskName() {
        return this.taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getBookId() {
        return this.bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public long getStart() {
        return this.start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return this.end;
    }

    public void setEnd(long end) {
        this.end = end;
    }
}