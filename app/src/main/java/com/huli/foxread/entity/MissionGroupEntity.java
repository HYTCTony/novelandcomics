package com.huli.foxread.entity;

import java.util.List;

/**
 * 福利（分组）任务
 */
public class MissionGroupEntity {
    private String id;
    private String title;       //分组title
    private List<MissionEntity> welfare;

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

    public List<MissionEntity> getWelfare() {
        return welfare;
    }

    public void setWelfare(List<MissionEntity> welfare) {
        this.welfare = welfare;
    }
}
