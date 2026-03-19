package com.nnmedia.read.entity;

import android.text.TextUtils;

import com.nnmedia.read.utils.AESCBCUtil;

import java.util.List;

/**
 * 完结书籍|新书  分组
 */
public class EndNewBookGroupEntity {
    private String id;
    private String name;
    private List<BookEntity> novelColumnAccess;

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

    public List<BookEntity> getNovelColumnAccess() {
        return novelColumnAccess;
    }

    public void setNovelColumnAccess(List<BookEntity> novelColumnAccess) {
        this.novelColumnAccess = novelColumnAccess;
    }
}
