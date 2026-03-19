package com.nnmedia.read.entity;

import android.text.TextUtils;

import com.nnmedia.read.utils.AESCBCUtil;

/**
 * 实时热搜
 */
public class HotSearchEntity {
    private String id;
    private String novel_id;
    private int number;          //搜索次数
    private int status;
    private int type;
    private String novel_name;
    private String http_image;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNovel_id() {
        return novel_id;
    }

    public void setNovel_id(String novel_id) {
        this.novel_id = novel_id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getNovel_name() {
        String ss = AESCBCUtil.decrypt2(novel_name, AESCBCUtil.getKey(), AESCBCUtil.getIv());
        if (TextUtils.isEmpty(ss)) {
            return novel_name;
        } else {
            return ss;
        }
    }

    public void setNovel_name(String novel_name) {
        this.novel_name = novel_name;
    }

    public String getHttp_image() {
        return http_image;
    }

    public void setHttp_image(String http_image) {
        this.http_image = http_image;
    }
}
