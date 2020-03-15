package com.huli.page.model.bean;

import android.text.TextUtils;

public class ChapterBean {
    private String id;
    private String content;
    private String links;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        if (TextUtils.isEmpty(content)) {
            return "";
        }
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLinks() {
        return links;
    }

    public void setLinks(String links) {
        this.links = links;
    }
}
