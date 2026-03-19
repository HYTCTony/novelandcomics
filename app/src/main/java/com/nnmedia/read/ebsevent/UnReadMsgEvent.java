package com.nnmedia.read.ebsevent;

/**
 * 项目名称：FoxRead
 * 创建人：Bill
 * 创建时间：2020/6/19  17:44
 * 备注：未读消息
 */
public class UnReadMsgEvent {
    private int message;

    public UnReadMsgEvent() {
    }

    public UnReadMsgEvent(int message) {
        this.message = message;
    }

    public int getMessage() {
        return message;
    }

    public void setMessage(int message) {
        this.message = message;
    }
}
