package com.nnmedia.comics.model;

/**
 * 漫画分类数据模型
 */
public class CategoryBean {
    /**
     * 分类ID
     */
    private String id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 分类图标URL
     */
    private String icon;

    /**
     * 排序
     */
    private int order;

    /**
     * 是否选中
     */
    private boolean selected;

    public CategoryBean() {
    }

    public CategoryBean(String id, String name) {
        this.id = id;
        this.name = name;
    }

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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
