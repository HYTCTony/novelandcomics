package com.nnmedia.read.entity;

import android.text.TextUtils;

import com.nnmedia.read.utils.AESCBCUtil;

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
        String ss = AESCBCUtil.decrypt2(title, AESCBCUtil.getKey(), AESCBCUtil.getIv());
        if (TextUtils.isEmpty(ss)) {
            return title;
        } else {
            return ss;
        }
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        String ss = AESCBCUtil.decrypt2(subTitle, AESCBCUtil.getKey(), AESCBCUtil.getIv());
        if (TextUtils.isEmpty(ss)) {
            return subTitle;
        } else {
            return ss;
        }
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getLabel() {
        String ss = AESCBCUtil.decrypt2(label, AESCBCUtil.getKey(), AESCBCUtil.getIv());
        if (TextUtils.isEmpty(ss)) {
            return label;
        } else {
            return ss;
        }
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
