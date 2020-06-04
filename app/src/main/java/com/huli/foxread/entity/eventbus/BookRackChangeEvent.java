package com.huli.foxread.entity.eventbus;

/**
 * 书架变化事件
 */
public class BookRackChangeEvent {

    //是否立即刷新
    private boolean refreshImmediately;


    public BookRackChangeEvent() {
    }

    public BookRackChangeEvent(boolean refreshImmediately) {
        this.refreshImmediately = refreshImmediately;
    }


    public boolean isRefreshImmediately() {
        return refreshImmediately;
    }

    public void setRefreshImmediately(boolean refreshImmediately) {
        this.refreshImmediately = refreshImmediately;
    }
}
