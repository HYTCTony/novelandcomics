package com.huli.foxread.entity.sections;

import com.chad.library.adapter.base.entity.JSectionEntity;

/**
 * 任务分组---组头
 */
public class MissionSection<T> extends JSectionEntity {
    private String title;
    private T object;

    public MissionSection() {
    }


    public MissionSection(String title, T object) {
        this.title = title;
        this.object = object;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

    @Override
    public boolean isHeader() {
        return object == null;
    }
}
