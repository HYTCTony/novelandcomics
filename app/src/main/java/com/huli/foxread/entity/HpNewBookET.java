package com.huli.foxread.entity;

import java.io.Serializable;
import java.util.List;

public class HpNewBookET implements Serializable {
    private String name;
    private String theme_id;
    private String id;
    private List<BookEntity> novel;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTheme_id() {
        return theme_id;
    }

    public void setTheme_id(String theme_id) {
        this.theme_id = theme_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<BookEntity> getNovel() {
        return novel;
    }

    public void setNovel(List<BookEntity> novel) {
        this.novel = novel;
    }
}
