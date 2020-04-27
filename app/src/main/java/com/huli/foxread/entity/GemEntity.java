package com.huli.foxread.entity;

import java.io.Serializable;

/**
 * 首页精选---分类佳作
 */
public class GemEntity extends BookEntity implements Serializable {
    private String categoryName;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
