package com.huli.foxread.cache;

import android.content.Context;
import android.text.TextUtils;

import com.huli.foxread.contact.Common;
import com.huli.foxread.entity.FUser;
import com.huli.foxread.utils.SPFUtils;

public class UserInfoCache {
    private static String token;

    public static void saveCacheAll(Context context, FUser info) {
        SPFUtils.put(context, Common.KEY_USER_ID, info.getId());
        SPFUtils.put(context, Common.KEY_USERNAME, info.getUsername());
        SPFUtils.put(context, Common.KEY_NICKNAME, info.getNickname());
        SPFUtils.put(context, Common.KEY_MOBILE, info.getMobile());
        SPFUtils.put(context, Common.KEY_HTTP_AVATAR, info.getHttp_avatar());
        SPFUtils.put(context, Common.KEY_GENDER, info.getGender());
        SPFUtils.put(context, Common.KEY_MONEY, info.getMoney());
        SPFUtils.put(context, Common.KEY_SCORE, info.getScore());
        SPFUtils.put(context, Common.KEY_TODAY_SCORE, info.getToday_score());
        SPFUtils.put(context, Common.KEY_DISTRIBUTION, info.getDistribution());
        SPFUtils.put(context, Common.KEY_IS_NEW_MAN, info.getIs_new_man() == 1);
        SPFUtils.put(context, Common.KEY_IS_VIP, info.getIs_vip() == 1);
        SPFUtils.put(context, Common.KEY_VIP_ENDTIME, info.getVip_end());
        SPFUtils.put(context, Common.KEY_IS_VISITOR, info.getIs_visitor() == 1);
        SPFUtils.put(context, Common.KEY_IS_INVITED, info.getIs_invited());
        SPFUtils.put(context, Common.KEY_MSG_NUM, info.getMessage_sum());
        token = info.getToken();
        SPFUtils.put(context, Common.KEY_TOKEN, token);
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

    public static void saveMoney(Context context, double money) {
        SPFUtils.put(context, Common.KEY_MONEY, money);
    }

    public static void saveScore(Context context, int score) {
        SPFUtils.put(context, Common.KEY_SCORE, score);
    }

    public static void saveTodayScore(Context context, int todayScore) {
        SPFUtils.put(context, Common.KEY_TODAY_SCORE, todayScore);
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

    public static void saveToken(Context context, String mToken) {
        token = mToken;
        SPFUtils.put(context, Common.KEY_TOKEN, mToken);
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

    public static Double getMoney(Context context) {
        return (Double) SPFUtils.get(context, Common.KEY_MONEY, 0d);
    }

    public static int getScore(Context context) {
        return (int) SPFUtils.get(context, Common.KEY_SCORE, 0);
    }

    public static int getTodayScore(Context context) {
        return (int) SPFUtils.get(context, Common.KEY_TODAY_SCORE, 0);
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

    public static String getToken(Context context) {
        if (!TextUtils.isEmpty(token)) {
            return token;
        }
        return (String) SPFUtils.get(context, Common.KEY_TOKEN, "");
    }


    public static void clearCache(Context context) {
        SPFUtils.remove(context, Common.KEY_USER_ID);
        SPFUtils.remove(context, Common.KEY_USERNAME);
        SPFUtils.remove(context, Common.KEY_NICKNAME);
        SPFUtils.remove(context, Common.KEY_MOBILE);
        SPFUtils.remove(context, Common.KEY_HTTP_AVATAR);
        SPFUtils.remove(context, Common.KEY_GENDER);
        SPFUtils.remove(context, Common.KEY_MONEY);
        SPFUtils.remove(context, Common.KEY_SCORE);
        SPFUtils.remove(context, Common.KEY_TODAY_SCORE);
        SPFUtils.remove(context, Common.KEY_DISTRIBUTION);
        SPFUtils.remove(context, Common.KEY_IS_NEW_MAN);
        SPFUtils.remove(context, Common.KEY_IS_VIP);
        SPFUtils.remove(context, Common.KEY_VIP_ENDTIME);
        SPFUtils.remove(context, Common.KEY_IS_VISITOR);
        SPFUtils.remove(context, Common.KEY_IS_INVITED);
        SPFUtils.remove(context, Common.KEY_MSG_NUM);
        SPFUtils.remove(context, Common.KEY_TOKEN);
    }
}