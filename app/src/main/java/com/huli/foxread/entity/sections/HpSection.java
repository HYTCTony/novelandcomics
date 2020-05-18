package com.huli.foxread.entity.sections;

import com.chad.library.adapter.base.entity.JSectionEntity;
import com.chad.library.adapter.base.entity.SectionEntity;

/**
 * 书城精选---分组---多布局
 */
public class HpSection extends JSectionEntity {
    /*这是自己定义的， 这不是后台数据返回的布局类型， 要与HpBGModuleEntity的类型做出区分*/
    public static final int SE_TYPE_HOT_BILLBOARD = 1;             //大热榜类型（二网格）
    public static final int SE_TYPE_CATE_EXC_WORKS = 2;            //分类佳作布局（四网格）
    public static final int SE_TYPE_GRID_NOR = 3;                  //普通的四网格
    public static final int SE_TYPE_LIST = 4;                      //列表（独占一行）
    public static final int SE_TYPE_FIRST_ITEM = 5;                //首本书独占一行
    public static final int SE_TYPE_HOT_SEARCH = 6;                //热搜布局（四网格）


    public static final int SPAN_SIZE_4 = 4;
    public static final int SPAN_SIZE_2 = 2;
    public static final int SPAN_SIZE_1 = 1;


    private boolean isHeader;
    private int itemType;
    private Object object;

    public HpSection(boolean isHeader, Object object) {
        this.isHeader = isHeader;
        this.object = object;
    }

    public HpSection(boolean isHeader, int itemType, Object object) {
        this.isHeader = isHeader;
        this.itemType = itemType;
        this.object = object;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public Object getObject() {
        return object;
    }


    public int getSpanSize() {
        if (isHeader()) {
            return SPAN_SIZE_4;
        } else {
            if (itemType == SE_TYPE_FIRST_ITEM || itemType == SE_TYPE_LIST) {
                return SPAN_SIZE_4;
            } else if (itemType == SE_TYPE_HOT_BILLBOARD) {
                return SPAN_SIZE_2;
            } else {
                return SPAN_SIZE_1;
            }
        }
    }

    /**
     * 重写此方法，返回 boolen 值，告知是否是header
     */
    @Override
    public boolean isHeader() {
        return isHeader;
    }

    /**
     * 重写此方法，返回你的item类型
     */
    @Override
    public int getItemType() {
        if (isHeader()) {
            return SectionEntity.Companion.HEADER_TYPE;
        } else {
            // 重写此处，返回自己的多布局类型
            return itemType;
        }
    }
}
