package com.huli.foxread.entity;

public class RvTitleEntity {

    private String id;
    private String title;
    private String subTitle;
    private String label;

    public RvTitleEntity() {
    }

    public RvTitleEntity(String id, String title, String subTitle, String label) {
        this.id = id;
        this.title = title;
        this.subTitle = subTitle;
        this.label = label;
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

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
