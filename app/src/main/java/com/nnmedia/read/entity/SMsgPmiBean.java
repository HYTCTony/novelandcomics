package com.nnmedia.read.entity;

import android.text.TextUtils;

import com.nnmedia.read.utils.AESCBCUtil;

public class SMsgPmiBean {
    private String id;
    private String name;
    private String content;
    private String http_image;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        String ss = AESCBCUtil.decrypt2(name, AESCBCUtil.getKey(), AESCBCUtil.getIv());
        if (TextUtils.isEmpty(ss)) {
            return name;
        } else {
            return ss;
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        String ss = AESCBCUtil.decrypt2(content, AESCBCUtil.getKey(), AESCBCUtil.getIv());
        if (TextUtils.isEmpty(ss)) {
            return content;
        } else {
            return ss;
        }
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getHttp_image() {
        return http_image;
    }

    public void setHttp_image(String http_image) {
        this.http_image = http_image;
    }
}
