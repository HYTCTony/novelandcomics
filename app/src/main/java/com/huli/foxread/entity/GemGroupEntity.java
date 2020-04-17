package com.huli.foxread.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 分类佳作(分组)
 */
public class GemGroupEntity implements Serializable {
    private String novel_classify_id;
    private int sum;
    private String name;
    private List<GemEntity> novel;

    public String getNovel_classify_id() {
        return novel_classify_id;
    }

    public void setNovel_classify_id(String novel_classify_id) {
        this.novel_classify_id = novel_classify_id;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<GemEntity> getNovel() {
        return novel;
    }

    public void setNovel(List<GemEntity> novel) {
        this.novel = novel;
    }
}
