package com.huli.foxread.entity;

import java.util.List;

/**
 * 书籍分组
 */
public class HpBGPraiseNvET {
    private String id;
    private String name;
    private String theme_id;
    private int layout;                 //为0是默认布局；1是多布局---第一个item占一行
    private List<BookEntity> novel;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public int getLayout() {
        return layout;
    }

    public void setLayout(int layout) {
        this.layout = layout;
    }

    public List<BookEntity> getNovel() {
        return novel;
    }

    public void setNovel(List<BookEntity> novel) {
        this.novel = novel;
    }
}
