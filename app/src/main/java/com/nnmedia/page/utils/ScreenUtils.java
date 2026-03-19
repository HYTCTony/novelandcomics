package com.nnmedia.page.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.DisplayCutout;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;

import com.nnmedia.read.FrApp;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ScreenUtils {


    private static final String TAG = "ScreenUtils";

    public static int dpToPx(int dp) {
        DisplayMetrics metrics = getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
    }

    public static int pxToDp(int px) {
        DisplayMetrics metrics = getDisplayMetrics();
        return (int) (px / metrics.density);
    }

    public static int spToPx(int sp) {
        DisplayMetrics metrics = getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, metrics);
    }

    public static int pxToSp(int px) {
        DisplayMetrics metrics = getDisplayMetrics();
        return (int) (px / metrics.scaledDensity);
    }

    /**
     * 获取手机显示App区域的大小（头部导航栏+ActionBar+根布局），不包括虚拟按钮
     *
     * @return
     */
    public static int[] getAppSize() {
        int[] size = new int[2];
        DisplayMetrics metrics = getDisplayMetrics();
        size[0] = metrics.widthPixels;
        size[1] = metrics.heightPixels;
        return size;
    }

    /**
     * 获取整个手机屏幕的大小(包括虚拟按钮)
     * 必须在onWindowFocus方法之后使用
     *
     * @param context
     * @return
     */
    public static int[] getScreenSize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        float density = dm.density;         // 屏幕密度（0.75 / 1.0 / 1.5）
        int width = dm.widthPixels;         // 屏幕宽度（像素）
        int height = dm.heightPixels;       // 屏幕高度（像素）
        int[] size = new int[2];
        // 屏幕宽度算法:屏幕宽度（像素）/屏幕密度
        size[0] = (int) (width / density);  // 屏幕宽度(dp)
        size[1] = (int) (height / density);// 屏幕高度(dp)
        return size;
    }

    public static void getAndroiodScreenProperty(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;         // 屏幕宽度（像素）
        int height = dm.heightPixels;       // 屏幕高度（像素）
        float density = dm.density;         // 屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = dm.densityDpi;     // 屏幕密度dpi（120 / 160 / 240）
        // 屏幕宽度算法:屏幕宽度（像素）/屏幕密度
        int screenWidth = (int) (width / density);  // 屏幕宽度(dp)
        int screenHeight = (int) (height / density);// 屏幕高度(dp)
        Log.d("h_bl", "屏幕宽度（像素）：" + width);
        Log.d("h_bl", "屏幕高度（像素）：" + height);
        Log.d("h_bl", "屏幕密度（0.75 / 1.0 / 1.5）：" + density);
        Log.d("h_bl", "屏幕密度dpi（120 / 160 / 240）：" + densityDpi);
        Log.d("h_bl", "屏幕宽度（dp）：" + screenWidth);
        Log.d("h_bl", "屏幕高度（dp）：" + screenHeight);
    }

    /**
     * 获取导航栏的高度
     *
     * @return
     */
    public static int getStatusBarHeight() {
        Resources resources = FrApp.getInstance().getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    /**
     * 获取虚拟按键的高度
     *
     * @return
     */
    public static int getNavigationBarHeight() {
        int navigationBarHeight = 0;
        Resources rs = FrApp.getInstance().getResources();
        int id = rs.getIdentifier("navigation_bar_height", "dimen", "android");
        if (id > 0 && hasNavigationBar()) {
            navigationBarHeight = rs.getDimensionPixelSize(id);
        }
        return navigationBarHeight;
    }

    /**
     * 是否存在虚拟按键
     *
     * @return
     */
    private static boolean hasNavigationBar() {
        boolean hasNavigationBar = false;
        Resources rs = FrApp.getInstance().getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
        }
        return hasNavigationBar;
    }

    public static DisplayMetrics getDisplayMetrics() {
        DisplayMetrics metrics = FrApp.getInstance()
                .getResources()
                .getDisplayMetrics();
        return metrics;
    }

    /**
     * 判断是否是刘海屏
     *
     * @return
     */
    public static boolean hasNotchScreen(Activity activity) {
        if (getInt("ro.miui.notch", activity) == 1 || hasNotchAtHuawei(activity) || hasNotchAtOPPO(activity)
                || hasNotchAtVivo(activity) || isAndroidP(activity) != null) { //TODO 各种品牌
            return true;
        }

        return false;
    }

    /**
     * Android P 刘海屏判断
     *
     * @param activity
     * @return
     */
    public static DisplayCutout isAndroidP(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        if (decorView != null && android.os.Build.VERSION.SDK_INT >= 28) {
            WindowInsets windowInsets = decorView.getRootWindowInsets();
            if (windowInsets != null)
                return windowInsets.getDisplayCutout();
        }
        return null;
    }

    /**
     * 小米刘海屏判断.
     *
     * @return 0 if it is not notch ; return 1 means notch
     * @throws IllegalArgumentException if the key exceeds 32 characters
     */
    public static int getInt(String key, Activity activity) {
        int result = 0;
        if (isXiaomi()) {
            try {
                ClassLoader classLoader = activity.getClassLoader();
                @SuppressWarnings("rawtypes")
                Class SystemProperties = classLoader.loadClass("android.os.SystemProperties");
                //参数类型
                @SuppressWarnings("rawtypes")
                Class[] paramTypes = new Class[2];
                paramTypes[0] = String.class;
                paramTypes[1] = int.class;
                Method getInt = SystemProperties.getMethod("getInt", paramTypes);
                //参数
                Object[] params = new Object[2];
                params[0] = new String(key);
                params[1] = new Integer(0);
                result = (Integer) getInt.invoke(SystemProperties, params);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 华为刘海屏判断
     *
     * @return
     */
    public static boolean hasNotchAtHuawei(Context context) {
        boolean ret = false;
        try {
            ClassLoader classLoader = context.getClassLoader();
            Class HwNotchSizeUtil = classLoader.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("hasNotchInScreen");
            ret = (boolean) get.invoke(HwNotchSizeUtil);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "hasNotchAtHuawei ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "hasNotchAtHuawei NoSuchMethodException");
        } catch (Exception e) {
            Log.e(TAG, "hasNotchAtHuawei Exception");
        } finally {
            return ret;
        }
    }

    public static final int VIVO_NOTCH = 0x00000020;//是否有刘海
    public static final int VIVO_FILLET = 0x00000008;//是否有圆角

    /**
     * VIVO刘海屏判断
     *
     * @return
     */
    public static boolean hasNotchAtVivo(Context context) {
        boolean ret = false;
        try {
            ClassLoader classLoader = context.getClassLoader();
            Class FtFeature = classLoader.loadClass("android.util.FtFeature");
            Method method = FtFeature.getMethod("isFeatureSupport", int.class);
            ret = (boolean) method.invoke(FtFeature, VIVO_NOTCH);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "hasNotchAtVivo ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "hasNotchAtVivo NoSuchMethodException");
        } catch (Exception e) {
            Log.e(TAG, "hasNotchAtVivo Exception");
        } finally {
            return ret;
        }
    }

    /**
     * OPPO刘海屏判断
     *
     * @return
     */
    public static boolean hasNotchAtOPPO(Context context) {
        return context.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
    }

    // 是否是小米手机
    public static boolean isXiaomi() {
        return "Xiaomi".equals(Build.MANUFACTURER);
    }
}