package com.nnmedia.read.entity;

import android.text.TextUtils;

import com.nnmedia.read.utils.AESCBCUtil;

import java.io.Serializable;

public class BookReview implements Serializable {
    private String id;              //评价id
    private String novel_id;        //小说id
    private String user_id;         //用户id
    private String username;        //用户名
    private String http_avatar;     //用户头像
    private String content;         //评价内容
    private int prefer;             //点赞数
    private float score;            //评分
    private int condition;          //是否已点赞:1=已点赞,0=未点赞
    private long createtime;

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

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        String ss = AESCBCUtil.decrypt2(username, AESCBCUtil.getKey(), AESCBCUtil.getIv());
        if (TextUtils.isEmpty(ss)) {
            return username;
        } else {
            return ss;
        }
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHttp_avatar() {
        return http_avatar;
    }

    public void setHttp_avatar(String http_avatar) {
        this.http_avatar = http_avatar;
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

    public int getPrefer() {
        return prefer;
    }

    public void setPrefer(int prefer) {
        this.prefer = prefer;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public int getCondition() {
        return condition;
    }

    public void setCondition(int condition) {
        this.condition = condition;
    }

    public long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }
}
