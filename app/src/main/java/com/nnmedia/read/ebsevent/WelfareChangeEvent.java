package com.nnmedia.read.ebsevent;

/**
 * 福利列表变化事件
 */
public class WelfareChangeEvent {

    //是否立即刷新
    private boolean refreshImmediately;

    public WelfareChangeEvent() {
    }

    public WelfareChangeEvent(boolean refreshImmediately) {
        this.refreshImmediately = refreshImmediately;
    }

    public boolean isRefreshImmediately() {
        return refreshImmediately;
    }

    public void setRefreshImmediately(boolean refreshImmediately) {
        this.refreshImmediately = refreshImmediately;
    }
}
