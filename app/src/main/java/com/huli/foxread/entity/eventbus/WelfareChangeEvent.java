package com.huli.foxread.entity.eventbus;

/**
 * 福利列表变化事件
 */
public class WelfareChangeEvent {

    public WelfareChangeEvent() {
    }

    public WelfareChangeEvent(boolean hasChange) {
        this.hasChange = hasChange;
    }

    private boolean hasChange;

    public boolean isHasChange() {
        return hasChange;
    }

    public void setHasChange(boolean hasChange) {
        this.hasChange = hasChange;
    }
}
