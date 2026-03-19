package com.nnmedia.read.ebsevent;


public class WXPaySuccessEvent {
    private int code;

    public WXPaySuccessEvent() {
    }

    public WXPaySuccessEvent(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
