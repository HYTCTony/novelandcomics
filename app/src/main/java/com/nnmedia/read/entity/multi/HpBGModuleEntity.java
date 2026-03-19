package com.nnmedia.read.entity.multi;

import android.text.TextUtils;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.nnmedia.read.entity.BookEntity;
import com.nnmedia.read.utils.AESCBCUtil;

import java.io.Serializable;
import java.util.List;

public class HpBGModuleEntity implements MultiItemEntity, Serializable {
    public static final int TYPE_HOT_BILLBOARD = 1;             //（一行两个）布局
    public static final int TYPE_CATE_EXC_WORKS = 2;            //分类佳作布局
    public static final int TYPE_SPECIAL = 3;                   //专题布局
    public static final int TYPE_GRID_4 = 4;                    //四网格
    public static final int TYPE_LIST = 5;                      //列表
    public static final int TYPE_FIRST_MONOPOLIZE = 6;          //首本书独占一行---四网格
    public static final int TYPE_HOT_SEARCH = 7;                //热搜布局

    private String id;
    private String name;
    private String introduce;
    private int layout;
    private List<BookEntity> novel;
    private String label;

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

    public String getIntroduce() {
        String ss = AESCBCUtil.decrypt2(introduce, AESCBCUtil.getKey(), AESCBCUtil.getIv());
        if (TextUtils.isEmpty(ss)) {
            return introduce;
        } else {
            return ss;
        }
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public int getLayout() {
        return layout;
    }

    public void setLayout(int layout) {
        this.layout = layout;
    }

    public List<BookEntity> getNovel() {
        return novel;
    }

    public void setNovel(List<BookEntity> novel) {
        this.novel = novel;
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

    @Override
    public int getItemType() {
        return getLayout();
    }
}
