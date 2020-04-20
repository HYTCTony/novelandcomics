package com.huli.foxread.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 分类佳作(分组)
 */
public class GemGroupEntity implements Serializable {
    private String id;
    private String name;
    private List<GemEntity> profile_novel_masterpiece;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<GemEntity> getProfile_novel_masterpiece() {
        return profile_novel_masterpiece;
    }

    public void setProfile_novel_masterpiece(List<GemEntity> profile_novel_masterpiece) {
        this.profile_novel_masterpiece = profile_novel_masterpiece;
    }
}
