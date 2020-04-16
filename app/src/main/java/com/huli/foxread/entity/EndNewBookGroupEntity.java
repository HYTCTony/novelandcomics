package com.huli.foxread.entity;

import java.util.List;

/**
 * 完结书籍|新书  分组
 */
public class EndNewBookGroupEntity {
    private String id;
    private String name;
    private List<EndNewBookEntity> novelColumnAccess;

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

    public List<EndNewBookEntity> getNovelColumnAccess() {
        return novelColumnAccess;
    }

    public void setNovelColumnAccess(List<EndNewBookEntity> novelColumnAccess) {
        this.novelColumnAccess = novelColumnAccess;
    }
}
