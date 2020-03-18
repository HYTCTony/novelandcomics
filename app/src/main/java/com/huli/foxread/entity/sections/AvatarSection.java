package com.huli.foxread.entity.sections;

import com.chad.library.adapter.base.entity.JSectionEntity;

/**
 * 系统头像分组
 */
public class AvatarSection<T> extends JSectionEntity {
    private boolean isHeader;
    private boolean isMore;
    private String title;
    // 你的数据内容
    private T object;

    public AvatarSection() {
    }

    public AvatarSection(boolean isHeader, String title, T object) {
        this.isHeader = isHeader;
        this.title = title;
        this.object = object;
    }

    public AvatarSection(boolean isHeader, boolean isMore, String title, T object) {
        this.isHeader = isHeader;
        this.isMore = isMore;
        this.title = title;
        this.object = object;
    }

    public boolean isMore() {
        return isMore;
    }

    public void setMore(boolean more) {
        isMore = more;
    }

    public void setHeader(boolean header) {
        isHeader = header;
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
        return isHeader;
    }
}
