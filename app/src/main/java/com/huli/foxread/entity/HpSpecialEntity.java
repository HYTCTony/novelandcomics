package com.huli.foxread.entity;

import java.io.Serializable;

public class HpSpecialEntity implements Serializable {
    private String id;
    private String novel_id;
    private String title;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHttp_image() {
        return http_image;
    }

    public void setHttp_image(String http_image) {
        this.http_image = http_image;
    }
}
