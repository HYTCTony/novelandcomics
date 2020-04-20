package com.huli.foxread.entity;

import java.io.Serializable;

/**
 * 佳作
 */
public class GemEntity implements Serializable {
    private String id;
    private String novel_id;
    private int status;                 //1上架，2下架
    private int weigh;
    private int type;
    private String novel_classify_id;
    private int reading_size;
    private String novel_name;
    private String http_image;

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

    public String getNovel_classify_id() {
        return novel_classify_id;
    }

    public void setNovel_classify_id(String novel_classify_id) {
        this.novel_classify_id = novel_classify_id;
    }

    public int getReading_size() {
        return reading_size;
    }

    public void setReading_size(int reading_size) {
        this.reading_size = reading_size;
    }

    public String getNovel_name() {
        return novel_name;
    }

    public void setNovel_name(String novel_name) {
        this.novel_name = novel_name;
    }

    public String getHttp_image() {
        return http_image;
    }

    public void setHttp_image(String http_image) {
        this.http_image = http_image;
    }
}
