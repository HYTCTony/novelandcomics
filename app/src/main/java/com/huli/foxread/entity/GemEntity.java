package com.huli.foxread.entity;

import java.io.Serializable;

/**
 * 佳作
 */
public class GemEntity implements Serializable {
    private String id;
    private String novel_classify_id;
    private int reading_size;
    private String name;
    private String http_image;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHttp_image() {
        return http_image;
    }

    public void setHttp_image(String http_image) {
        this.http_image = http_image;
    }
}
