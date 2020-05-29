package com.huli.foxread.entity.sections;

import com.chad.library.adapter.base.entity.JSectionEntity;
import com.chad.library.adapter.base.entity.SectionEntity;

/**
 * 福利任务（分组）
 */
public class MissionSection2 extends JSectionEntity {

    public static final int TYPE_MISSION_NOR = 1;             //一般任务
    public static final int TYPE_MISSION_7DAY = 2;            //7天领金币

    private boolean isHeader;
    private int itemType;
    private Object object;

    public MissionSection2(boolean isHeader, Object object) {
        this.isHeader = isHeader;
        this.object = object;
    }

    public MissionSection2(boolean isHeader, int itemType, Object object) {
        this.isHeader = isHeader;
        this.itemType = itemType;
        this.object = object;
    }

    public Object getObject() {
        return object;
    }

    /**
     * 重写此方法，返回 boolen 值，告知是否是header
     */
    @Override
    public boolean isHeader() {
        return isHeader;
    }

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
