package com.huli.foxread.ebsevent;

/**
 * 项目名称：SkyfameAlliance
 * 创建人：Bill
 * 创建时间：2018/12/26  16:58
 * 微信的code
 */

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
