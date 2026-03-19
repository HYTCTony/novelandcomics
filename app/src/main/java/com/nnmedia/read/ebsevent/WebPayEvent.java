package com.nnmedia.read.ebsevent;

public class WebPayEvent {
    private String orderId;

    public WebPayEvent() {
    }

    public WebPayEvent(String orderId) {
        this.orderId = orderId;
    }

    public String getCode() {
        return orderId;
    }

    public void setCode(String orderId) {
        this.orderId = orderId;
    }
}
