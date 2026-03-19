package com.nnmedia.page.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferencesUtils {
    public static boolean isAutologon = false;

    /**
     * @param context 保存用户id
     */
    public static void setUserId(Context context, int userId) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putInt("user_id", userId);
        editor.apply();
    }

    /**
     * 获取用户id
     *
     * @param context
     * @return
     */
    public static int getUserId(Context context) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        return sp.getInt("user_id", 0);
    }

    /**
     * @param context 清除用户数据
     */
    public static void clearUser(Context context) {
        SharedPreferences sp = context.getSharedPreferences("user", context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.clear();
        editor.apply();
        isAutologon = true;
    }

    /**
     * @param context 只退出登录 不删除用户信息
     */
    public static void exitLogin(Context context) {
        isAutologon = true;
    }

    /**
     * 获取token
     *
     * @param context
     * @return
     */
    public static String getToken(Context context) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        return sp.getString("token", "");
    }

    /**
     * 获取token
     *
     * @param context
     * @return
     */
    public static void setToken(Context context, String token) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString("token", token);
        editor.apply();
    }

    /**
     * 获取上一次登录成功的账号
     *
     * @param context
     * @return
     */
    public static String getUserName(Context context) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        return sp.getString("user_name", "");
    }

    /**
     * 保存上一次登录成功的账号
     *
     * @param context
     * @return
     */
    public static void setUserName(Context context, String m) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString("user_name", m);
        editor.apply();
    }

    /**
     * @param context
     * @return
     */
    public static int getWatchLimit(Context context) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        return sp.getInt("limit", 0);
    }

    /**
     * @param context
     * @return
     */
    public static void setWatchLimit(Context context, int m) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putInt("limit", m);
        editor.apply();
    }

    /**
     * @param context
     * @return
     */
    public static int getWatchNum(Context context) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        return sp.getInt("num", 0);
    }

    /**
     * @param context
     * @return
     */
    public static void setWatchNum(Context context, int m) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putInt("num", m);
        editor.apply();
    }

    /**
     * @param context
     * @return
     */
    public static String getPostsId(Context context) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        return sp.getString("postId", "[]");
    }

    /**
     * @param context
     * @return
     */
    public static void setPostsId(Context context, String postId) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString("postId", postId);
        editor.apply();
    }

    /**
     * @param context
     */
    public static void setUserLevel(Context context, int level) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putInt("user_level", level);
        editor.apply();
    }

    /**
     * @param context
     * @return
     */
    public static int getUserLevel(Context context) {
        SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        return sp.getInt("user_level", 0);
    }

}
