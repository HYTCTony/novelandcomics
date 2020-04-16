package com.huli.foxread.entity.multi;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.huli.foxread.entity.ForestallNewEntity;

/**
 * 抢先新书多布局
 */
public class ForestallNewMultiEntity extends ForestallNewEntity implements MultiItemEntity {

    public static final int DETAILED = 1;
    public static final int ITEM_FIRST = 2;
    public static final int SPAN_SIZE_4 = 4;
    public static final int SPAN_SIZE_1 = 1;
    private int spanSize;
    private int itemType;


    public int getSpanSize() {
        return spanSize;
    }

    public void setSpanSize(int spanSize) {
        this.spanSize = spanSize;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    @Override
    public int getItemType() {
        return itemType;
    }
}
