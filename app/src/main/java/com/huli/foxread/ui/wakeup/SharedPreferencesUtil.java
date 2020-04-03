package com.huli.foxread.ui.wakeup;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 项目名称：BatteryRent
 * 创建人：Bill
 * 创建时间：2019/5/16  16:49
 */
public class SharedPreferencesUtil {
    private SharedPreferences sp;
    private Context context;

    public SharedPreferencesUtil(Context context) {
        this.context = context;
    }

    /**
     * 保存唤醒APP的对象
     */
    public void saveAwakeAPPBean(AwakePageInfoBean awakePageInfoBean) {
        sp = context.getSharedPreferences("AwakeAPPBeanINFO", Context.MODE_PRIVATE);
        sp.edit().putString("awakeType", awakePageInfoBean.getType()).apply();

    }

    /**
     * 获取唤醒APP的对象
     */
    public AwakePageInfoBean getAwakeAPPBean() {
        sp = context.getSharedPreferences("AwakeAPPBeanINFO", Context.MODE_PRIVATE);
        AwakePageInfoBean awakePageInfoBean = new AwakePageInfoBean();
        awakePageInfoBean.setType(sp.getString("awakeType", ""));

        return awakePageInfoBean;
    }

    /*
     * 清空
     */
    public void deleteBrowserOpenAppBean(String name) {
        sp = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        sp.edit().clear().apply();
    }

}