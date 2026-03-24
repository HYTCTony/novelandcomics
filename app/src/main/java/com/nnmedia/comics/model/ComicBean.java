package com.nnmedia.comics.model;

/**
 * 漫画数据模型
 */
public class ComicBean {
    /**
     * 漫画ID
     */
    private String id;

    /**
     * 漫画名称
     */
    private String title;

    /**
     * 漫画封面URL
     */
    private String cover;

    /**
     * 漫画作者
     */
    private String author;

    /**
     * 漫画简介
     */
    private String description;

    /**
     * 最新章节
     */
    private String latestChapter;

    /**
     * 更新时间
     */
    private String updateTime;

    /**
     * 是否完结
     */
    private boolean isFinished;

    /**
     * 章节数量
     */
    private int chapterCount;

    public ComicBean() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLatestChapter() {
        return latestChapter;
    }

    public void setLatestChapter(String latestChapter) {
        this.latestChapter = latestChapter;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public int getChapterCount() {
        return chapterCount;
    }

    public void setChapterCount(int chapterCount) {
        this.chapterCount = chapterCount;
    }
}
