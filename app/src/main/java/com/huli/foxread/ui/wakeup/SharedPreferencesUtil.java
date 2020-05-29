package com.huli.foxread.ui.wakeup;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 创建人：Bill
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