package com.huli.foxread.entity.sections;

import com.chad.library.adapter.base.entity.JSectionEntity;

/**
 * 新书|完结 分组
 */
public class NEbookSection<T> extends JSectionEntity {
    // 你的数据内容
    private boolean isHeader;
    private boolean isMore;
    private String id;
    private String theme_id;
    private String name;
    private T object;

    public NEbookSection() {
    }

    public NEbookSection(boolean isHeader, T object) {
        this.isHeader = isHeader;
        this.object = object;
    }

    public NEbookSection(boolean isHeader, boolean isMore, String id, String theme_id, String name, T object) {
        this.isHeader = isHeader;
        this.isMore = isMore;
        this.id = id;
        this.theme_id = theme_id;
        this.name = name;
        this.object = object;
    }

    public void setHeader(boolean header) {
        isHeader = header;
    }

    public boolean isMore() {
        return isMore;
    }

    public void setMore(boolean more) {
        isMore = more;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTheme_id() {
        return theme_id;
    }

    public void setTheme_id(String theme_id) {
        this.theme_id = theme_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

    @Override
    public boolean isHeader() {
        return isHeader;
    }
}
