package com.huli.foxread.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class BookMultiEntity implements MultiItemEntity {

    public static final int DETAILED = 1;
    public static final int SUCCINCT = 2;
    public static final int SPAN_SIZE_4 = 4;
    public static final int SPAN_SIZE_1 = 1;

    private String img;
    private String name;
    private float score;
    private String introduction;
    private String authorName;
    private String wordCount;
    private String bookTag;
    private int spanSize;
    private int itemType;

    public BookMultiEntity() {
    }

    public BookMultiEntity(String img, String name, float score, String introduction, String authorName, String wordCount, String bookTag, int spanSize, int itemType) {
        this.img = img;
        this.name = name;
        this.score = score;
        this.introduction = introduction;
        this.authorName = authorName;
        this.wordCount = wordCount;
        this.bookTag = bookTag;
        this.spanSize = spanSize;
        this.itemType = itemType;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getWordCount() {
        return wordCount;
    }

    public void setWordCount(String wordCount) {
        this.wordCount = wordCount;
    }

    public String getBookTag() {
        return bookTag;
    }

    public void setBookTag(String bookTag) {
        this.bookTag = bookTag;
    }

    public int getSpanSize() {
        return spanSize;
    }

    public void setSpanSize(int spanSize) {
        this.spanSize = spanSize;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    @Override
    public int getItemType() {
        return itemType;
    }
}
