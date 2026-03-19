package com.nnmedia.page.model.bean;

import android.text.TextUtils;

import com.nnmedia.read.utils.AESCBCUtil;

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

    private int is_canview;
    private int hpoint;
    private int number;

    @Generated(hash = 18058555)
    public BookChapter(String id, String novel_id, String name, int chapter, String content_url,
            String http_links, String taskName, String bookId, long start, long end, int is_canview,
            int hpoint, int number) {
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
        this.is_canview = is_canview;
        this.hpoint = hpoint;
        this.number = number;
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

    public boolean isCanView() {
        return is_canview == 1;
    }

    public void setCanView(int is_canview) {
        this.is_canview = is_canview;
    }

    public int getIs_canview() {
        return this.is_canview;
    }

    public void setIs_canview(int is_canview) {
        this.is_canview = is_canview;
    }

    public int getHpoint() {
        return this.hpoint;
    }

    public void setHpoint(int hpoint) {
        this.hpoint = hpoint;
    }

    public int getNumber() {
        return this.number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}