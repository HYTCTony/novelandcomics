package com.huli.foxread.cache;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.huli.foxread.contact.Common;
import com.huli.foxread.entity.FUser;
import com.huli.foxread.utils.SPFUtils;

public class UserInfoCache {

    private static FUser info;

    public static void saveUserInfo(Context context, FUser fUser) {
        info = fUser;
        String userStr = JSONObject.toJSONString(fUser);
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
        if (info == null) {
            info = getUserInfo(context);
        }
        info.setId(userId);
        saveUserInfo(context, info);
        return info;
    }

    public static FUser saveUserName(Context context, String userName) {
        if (info == null) {
            info = getUserInfo(context);
        }
        info.setUsername(userName);
        saveUserInfo(context, info);
        return info;
    }

    public static FUser saveMobile(Context context, String mobile) {
        if (info == null) {
            info = getUserInfo(context);
        }
        info.setMobile(mobile);
        saveUserInfo(context, info);
        return info;
    }

    public static FUser saveHeadPic(Context context, String headPic) {
        if (info == null) {
            info = getUserInfo(context);
        }
        info.setHttp_avatar(headPic);
        saveUserInfo(context, info);
        return info;
    }

    public static FUser saveGender(Context context, int gender) {
        if (info == null) {
            info = getUserInfo(context);
        }
        info.setGender(gender);
        saveUserInfo(context, info);
        return info;
    }

    public static FUser saveDistribution(Context context, String distribution) {
        if (info == null) {
            info = getUserInfo(context);
        }
        info.setDistribution(distribution);
        saveUserInfo(context, info);
        return info;
    }

    public static FUser saveIsNewMan(Context context, boolean flagNewMan) {
        if (info == null) {
            info = getUserInfo(context);
        }
        info.setIs_new(flagNewMan);
        saveUserInfo(context, info);
        return info;
    }

    public static FUser saveIsVip(Context context, boolean flagIsVip) {
        if (info == null) {
            info = getUserInfo(context);
        }
        info.setIs_vip(flagIsVip);
        saveUserInfo(context, info);
        return info;
    }

    public static FUser saveVipEndtime(Context context, long vipEndtime) {
        if (info == null) {
            info = getUserInfo(context);
        }
        info.setVip_end(vipEndtime);
        saveUserInfo(context, info);
        return info;
    }

    public static FUser saveIsTourist(Context context, boolean flagIsVisitor) {
        if (info == null) {
            info = getUserInfo(context);
        }
        info.setIs_tourist(flagIsVisitor);
        saveUserInfo(context, info);
        return info;
    }

    public static FUser saveIsInvited(Context context, boolean flagIsInvited) {
        if (info == null) {
            info = getUserInfo(context);
        }
        info.setIs_invited(flagIsInvited);
        saveUserInfo(context, info);
        return info;
    }

    public static FUser saveIsBindWechat(Context context, boolean flagBindWx) {
        if (info == null) {
            info = getUserInfo(context);
        }
        info.setIs_wx(flagBindWx);
        saveUserInfo(context, info);
        return info;
    }


    public static String getUserId(Context context) {
        if (info == null) {
            info = getUserInfo(context);
        }
        return info.getId();
    }

    public static String getUserName(Context context) {
        if (info == null) {
            info = getUserInfo(context);
        }
        return info.getUsername();
    }

    public static String getMobile(Context context) {
        if (info == null) {
            info = getUserInfo(context);
        }
        return info.getMobile();
    }

    public static String getHeadPic(Context context) {
        if (info == null) {
            info = getUserInfo(context);
        }
        return info.getHttp_avatar();
    }

    public static int getGender(Context context) {
        if (info == null) {
            info = getUserInfo(context);
        }
        return info.getGender();
    }

    public static String getDistribution(Context context) {
        if (info == null) {
            info = getUserInfo(context);
        }
        return info.getDistribution();
    }

    public static boolean getIsNewMan(Context context) {
        if (info == null) {
            info = getUserInfo(context);
        }
        return info.isIs_new();
    }

    public static boolean getIsVip(Context context) {
        if (info == null) {
            info = getUserInfo(context);
        }
        return info.isIs_vip();
    }

    public static long getVipEndtime(Context context) {
        if (info == null) {
            info = getUserInfo(context);
        }
        return info.getVip_end();
    }

    public static boolean getIsTourist(Context context) {
        if (info == null) {
            info = getUserInfo(context);
        }
        return info.isIs_tourist();
    }

    public static boolean getIsInvited(Context context) {
        if (info == null) {
            info = getUserInfo(context);
        }
        return info.isIs_invited();
    }

    public static boolean getIsBindWechat(Context context) {
        if (info == null) {
            info = getUserInfo(context);
        }
        return info.isIs_wx();
    }


    public static void clearCache(Context context) {
        info = null;
        SPFUtils.remove(context, Common.USER_DTO);
    }
}