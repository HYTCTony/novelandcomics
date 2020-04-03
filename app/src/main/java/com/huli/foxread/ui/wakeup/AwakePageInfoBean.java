package com.huli.foxread.ui.wakeup;

import java.io.Serializable;

/**
 * 项目名称：BatteryRent
 * 创建人：Bill
 * 创建时间：2019/5/16  16:48
 * 唤醒页面，所需内容封装bean
 */
public class AwakePageInfoBean implements Serializable {


    //跳转类型
    /**
     * 1:去Activity_1
     * 2:去Activity_2
     * 3:去Activity_3
     */
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}