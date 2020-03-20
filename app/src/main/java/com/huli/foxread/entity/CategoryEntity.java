package com.huli.foxread.entity;

import java.io.Serializable;

/**
 * 分类（子分类也适用）
 */
public class CategoryEntity implements Serializable {
    private int id;
    private String name;
    private int pid;
    private String status;
    private int novel_sum;
    private String http_image;
    private int grade;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }
}
