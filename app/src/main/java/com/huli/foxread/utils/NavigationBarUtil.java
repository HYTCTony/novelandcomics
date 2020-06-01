package com.huli.foxread.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;

/**
 * 虚拟导航栏
 */
public class NavigationBarUtil {
    /**
     * 判断底部导航栏是否显示
     *
     * @param act
     * @return
     */
    public static boolean isNavigationBarShow(Activity act) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display display = act.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            Point realSize = new Point();
            display.getSize(size);
            display.getRealSize(realSize);
            return realSize.y != size.y;
        } else {
            boolean menu = ViewConfiguration.get(act).hasPermanentMenuKey();
            boolean back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            if (menu || back) {
                return false;
            } else {
                return true;
            }
        }
    }
    /**
     * 如果有底部导航栏 获取底部导航栏高度
     * @param context
     * @return
     */
    public static int getBottomNavigatorHeight(Context context) {
        int rid = context.getResources().getIdentifier("config_showNavigationBar", "bool", "android");
        if (0 != rid) {
            int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            return context.getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

}
