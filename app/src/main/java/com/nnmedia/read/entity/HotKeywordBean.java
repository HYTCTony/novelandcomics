package com.nnmedia.read.entity;

import android.text.TextUtils;

import com.nnmedia.read.utils.AESCBCUtil;

/**
 * 搜索热门关键词
 */
public class HotKeywordBean {
    private String id;
    private String keyword;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKeyword() {
        String ss = AESCBCUtil.decrypt2(keyword, AESCBCUtil.getKey(), AESCBCUtil.getIv());
        if (TextUtils.isEmpty(ss)) {
            return keyword;
        } else {
            return ss;
        }
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
