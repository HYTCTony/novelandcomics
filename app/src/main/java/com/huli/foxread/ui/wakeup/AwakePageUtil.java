package com.huli.foxread.ui.wakeup;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

/**
 * 创建人：Bill
 * 唤醒页面的工具
 */
public class AwakePageUtil {

    public static void awakePage(Context context, AwakePageInfoBean bean) {

        try {
            if (bean != null && !TextUtils.isEmpty(bean.getType()) && context != null) {
                //类型不为空。
                String type = bean.getType();
                Intent intent = null;

                /*if (TextUtils.equals("1", type)) {
                    //去展示webView的界面
                    intent = new Intent(context, Activity_1.class);
                } else if (TextUtils.equals("2", type)) {
                    //去展示webView的界面
                    intent = new Intent(context, Activity_2.class);
                } else if (TextUtils.equals("3", type)) {
                    //去展示webView的界面
                    intent = new Intent(context, Activity_3.class);
                }

                if (intent != null) {
                    context.startActivity(intent);
                }*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}