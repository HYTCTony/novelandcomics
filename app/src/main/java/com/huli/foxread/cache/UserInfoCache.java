package com.huli.foxread.cache;

import android.content.Context;

import com.huli.foxread.contact.Common;
import com.huli.foxread.entity.FUser;
import com.huli.foxread.utils.SPFUtils;

public class UserInfoCache {

    public static void saveCacheAll(Context context, FUser info) {
        saveUserId(context, info.getId());
        saveUserName(context, info.getUsername());
        saveNickName(context, info.getNickname());
        saveMobile(context, info.getMobile());
        saveHeadPic(context, info.getHttp_avatar());
        saveGender(context, info.getGender());
        saveDistribution(context, info.getDistribution());
        saveIsNewMan(context, info.getIs_new_man());
        saveIsVip(context, info.getIs_vip());
        saveVipEndtime(context, info.getVip_end());
        saveIsVisitor(context, info.getIs_visitor());
        saveIsInvited(context, info.getIs_invited());
        saveIsInvited(context, info.getIs_invited());
        saveMsgNum(context, info.getMessage_sum());
    }

    public static void saveUserId(Context context, String userId) {
        SPFUtils.put(context, Common.KEY_USER_ID, userId);
    }

    public static void saveUserName(Context context, String userName) {
        SPFUtils.put(context, Common.KEY_USERNAME, userName);
    }


    public static void saveNickName(Context context, String nickName) {
        SPFUtils.put(context, Common.KEY_NICKNAME, nickName);
    }

    public static void saveMobile(Context context, String mobile) {
        SPFUtils.put(context, Common.KEY_MOBILE, mobile);
    }

    public static void saveHeadPic(Context context, String headPic) {
        SPFUtils.put(context, Common.KEY_HTTP_AVATAR, headPic);
    }

    public static void saveGender(Context context, int gender) {
        SPFUtils.put(context, Common.KEY_GENDER, gender);
    }

    public static void saveDistribution(Context context, String distribution) {
        SPFUtils.put(context, Common.KEY_DISTRIBUTION, distribution);
    }

    public static void saveIsNewMan(Context context, int flagNewMan) {
        SPFUtils.put(context, Common.KEY_IS_NEW_MAN, flagNewMan == 1);
    }

    public static void saveIsVip(Context context, int flagIsVip) {
        SPFUtils.put(context, Common.KEY_IS_VIP, flagIsVip == 1);
    }

    public static void saveVipEndtime(Context context, long VipEndtime) {
        SPFUtils.put(context, Common.KEY_VIP_ENDTIME, VipEndtime);
    }

    public static void saveIsVisitor(Context context, int flagIsVisitor) {
        SPFUtils.put(context, Common.KEY_IS_VISITOR, flagIsVisitor == 1);
    }

    public static void saveIsInvited(Context context, int flagIsInvited) {
        SPFUtils.put(context, Common.KEY_IS_INVITED, flagIsInvited);
    }

    public static void saveMsgNum(Context context, int msgNum) {
        SPFUtils.put(context, Common.KEY_MSG_NUM, msgNum);
    }


    public static String getUserId(Context context) {
        return (String) SPFUtils.get(context, Common.KEY_USER_ID, "");
    }

    public static String getUserName(Context context) {
        return (String) SPFUtils.get(context, Common.KEY_USERNAME, "");
    }

    public static String getNickName(Context context) {
        return (String) SPFUtils.get(context, Common.KEY_NICKNAME, "");
    }

    public static String getMobile(Context context) {
        return (String) SPFUtils.get(context, Common.KEY_MOBILE, "");
    }

    public static String getHeadPic(Context context) {
        return (String) SPFUtils.get(context, Common.KEY_HTTP_AVATAR, "");
    }

    public static int getGender(Context context) {
        return (int) SPFUtils.get(context, Common.KEY_GENDER, 0);
    }

    public static String getDistribution(Context context) {
        return (String) SPFUtils.get(context, Common.KEY_DISTRIBUTION, "");
    }

    public static boolean getIsNewMan(Context context) {
        return (boolean) SPFUtils.get(context, Common.KEY_IS_NEW_MAN, true);
    }

    public static boolean getIsVip(Context context) {
        return (boolean) SPFUtils.get(context, Common.KEY_IS_VIP, false);
    }

    public static long getVipEndtime(Context context) {
        return (long) SPFUtils.get(context, Common.KEY_VIP_ENDTIME, 0L);
    }

    public static boolean getIsVisitor(Context context) {
        return (boolean) SPFUtils.get(context, Common.KEY_IS_VISITOR, true);
    }

    public static int getIsInvited(Context context) {
        return (int) SPFUtils.get(context, Common.KEY_IS_INVITED, -1);
    }

    public static int getMsgNum(Context context) {
        return (int) SPFUtils.get(context, Common.KEY_MSG_NUM, 0);
    }


    public static void clearCache(Context context) {
        SPFUtils.remove(context, Common.KEY_USER_ID);
        SPFUtils.remove(context, Common.KEY_USERNAME);
        SPFUtils.remove(context, Common.KEY_NICKNAME);
        SPFUtils.remove(context, Common.KEY_MOBILE);
        SPFUtils.remove(context, Common.KEY_HTTP_AVATAR);
        SPFUtils.remove(context, Common.KEY_GENDER);
        SPFUtils.remove(context, Common.KEY_DISTRIBUTION);
        SPFUtils.remove(context, Common.KEY_IS_NEW_MAN);
        SPFUtils.remove(context, Common.KEY_IS_VIP);
        SPFUtils.remove(context, Common.KEY_VIP_ENDTIME);
        SPFUtils.remove(context, Common.KEY_IS_VISITOR);
        SPFUtils.remove(context, Common.KEY_IS_INVITED);
        SPFUtils.remove(context, Common.KEY_MSG_NUM);
    }
}