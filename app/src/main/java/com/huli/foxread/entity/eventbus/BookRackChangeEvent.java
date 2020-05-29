package com.huli.foxread.entity.eventbus;

/**
 * 书架变化事件
 */
public class BookRackChangeEvent {

    public BookRackChangeEvent() {
    }

    public BookRackChangeEvent(boolean hasChange) {
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
