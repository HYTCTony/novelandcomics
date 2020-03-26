package com.huli.foxread.cache;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.huli.foxread.contact.Common;
import com.huli.foxread.entity.FUser;
import com.huli.foxread.utils.SPFUtils;

public class UserInfoCache2 {

    public static void saveUserInfo(Context context, FUser info) {
        String userStr = JSONObject.toJSONString(info);
        SPFUtils.put(context, Common.USER_DTO, userStr);
    }

    public static FUser getUserInfo(Context context) {
        String userStr = (String) SPFUtils.get(context, Common.USER_DTO, "");
        FUser fUser = JSONObject.parseObject(userStr, FUser.class);
        if (fUser == null) {
            fUser = new FUser();
        }
        return fUser;
    }

    public static FUser saveUserId(Context context, String userId) {
        FUser info = getUserInfo(context);
        info.setId(userId);
        saveUserInfo(context, info);
        return info;
    }

    public static FUser saveUserName(Context context, String userName) {
        FUser info = getUserInfo(context);
        info.setUsername(userName);
        saveUserInfo(context, info);
        return info;
    }

    public static FUser saveMobile(Context context, String mobile) {
        FUser info = getUserInfo(context);
        info.setMobile(mobile);
        saveUserInfo(context, info);
        return info;
    }

    public static FUser saveHeadPic(Context context, String headPic) {
        FUser info = getUserInfo(context);
        info.setHttp_avatar(headPic);
        saveUserInfo(context, info);
        return info;
    }

    public static FUser saveGender(Context context, int gender) {
        FUser info = getUserInfo(context);
        info.setGender(gender);
        saveUserInfo(context, info);
        return info;
    }

    public static FUser saveDistribution(Context context, String distribution) {
        FUser info = getUserInfo(context);
        info.setDistribution(distribution);
        saveUserInfo(context, info);
        return info;
    }

    public static FUser saveIsNewMan(Context context, int flagNewMan) {
        FUser info = getUserInfo(context);
        info.setIs_new_man(flagNewMan);
        saveUserInfo(context, info);
        return info;
    }

    public static FUser saveIsVip(Context context, int flagIsVip) {
        FUser info = getUserInfo(context);
        info.setIs_vip(flagIsVip);
        saveUserInfo(context, info);
        return info;
    }

    public static FUser saveVipEndtime(Context context, long vipEndtime) {
        FUser info = getUserInfo(context);
        info.setVip_end(vipEndtime);
        saveUserInfo(context, info);
        return info;
    }

    public static FUser saveIsVisitor(Context context, int flagIsVisitor) {
        FUser info = getUserInfo(context);
        info.setIs_visitor(flagIsVisitor);
        saveUserInfo(context, info);
        return info;
    }

    public static FUser saveIsInvited(Context context, int flagIsInvited) {
        FUser info = getUserInfo(context);
        info.setIs_invited(flagIsInvited);
        saveUserInfo(context, info);
        return info;
    }

    public static String getUserId(Context context) {
        FUser info = getUserInfo(context);
        return info.getId();
    }

    public static String getUserName(Context context) {
        FUser info = getUserInfo(context);
        return info.getUsername();
    }

    public static String getMobile(Context context) {
        FUser info = getUserInfo(context);
        return info.getMobile();
    }

    public static String getHeadPic(Context context) {
        FUser info = getUserInfo(context);
        return info.getHttp_avatar();
    }

    public static int getGender(Context context) {
        FUser info = getUserInfo(context);
        return info.getGender();
    }

    public static String getDistribution(Context context) {
        FUser info = getUserInfo(context);
        return info.getDistribution();
    }

    public static boolean getIsNewMan(Context context) {
        FUser info = getUserInfo(context);
        return info.getIs_new_man() == 1;
    }

    public static boolean getIsVip(Context context) {
        FUser info = getUserInfo(context);
        return info.getIs_vip() == 1;
    }

    public static long getVipEndtime(Context context) {
        FUser info = getUserInfo(context);
        return info.getVip_end();
    }

    public static boolean getIsVisitor(Context context) {
        FUser info = getUserInfo(context);
        return info.getIs_visitor() == 1;
    }

    public static boolean getIsInvited(Context context) {
        FUser info = getUserInfo(context);
        return info.getIs_invited() > 0;
    }


    public static void clearCache(Context context) {
        SPFUtils.remove(context, Common.USER_DTO);
    }
}