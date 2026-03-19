package com.nnmedia.read.entity;

import android.text.TextUtils;

import com.nnmedia.read.utils.AESCBCUtil;

import java.io.Serializable;
import java.util.List;

/**
 * 分类（子分类也适用）
 */
public class CategoryEntity implements Serializable {
    private int id;
    private String name;
    private int pid;
    private int novel_sum;
    private String http_image;
    private List<CategoryEntity> list;

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getNovel_sum() {
        return novel_sum;
    }

    public void setNovel_sum(int novel_sum) {
        this.novel_sum = novel_sum;
    }

    public String getHttp_image() {
        return http_image;
    }

    public void setHttp_image(String http_image) {
        this.http_image = http_image;
    }

    public List<CategoryEntity> getList() {
        return list;
    }

    public void setList(List<CategoryEntity> list) {
        this.list = list;
    }
}
