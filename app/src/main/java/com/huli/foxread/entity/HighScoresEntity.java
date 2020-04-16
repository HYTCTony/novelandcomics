package com.huli.foxread.entity;

/**
 * 高分精选
 */
public class HighScoresEntity {
    private String id;
    private String novel_id;
    private float score;
    private int status;
    private int weigh;
    private int type;
    private String novel_name;
    private String novel_author;
    private String http_image;
    private String novel_tag;
    private int novel_word;
    private String novel_introduce;

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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getWeigh() {
        return weigh;
    }

    public void setWeigh(int weigh) {
        this.weigh = weigh;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getNovel_name() {
        return novel_name;
    }

    public void setNovel_name(String novel_name) {
        this.novel_name = novel_name;
    }

    public String getNovel_author() {
        return novel_author;
    }

    public void setNovel_author(String novel_author) {
        this.novel_author = novel_author;
    }

    public String getHttp_image() {
        return http_image;
    }

    public void setHttp_image(String http_image) {
        this.http_image = http_image;
    }

    public String getNovel_tag() {
        return novel_tag;
    }

    public void setNovel_tag(String novel_tag) {
        this.novel_tag = novel_tag;
    }

    public int getNovel_word() {
        return novel_word;
    }

    public void setNovel_word(int novel_word) {
        this.novel_word = novel_word;
    }

    public String getNovel_introduce() {
        return novel_introduce;
    }

    public void setNovel_introduce(String novel_introduce) {
        this.novel_introduce = novel_introduce;
    }
}
