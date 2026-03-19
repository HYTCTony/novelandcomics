package com.nnmedia.read.config;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.nnmedia.read.entity.AdConfigBean;
import com.nnmedia.read.utils.SPFUtils;

public class AdConfig {
    private static AdConfigBean data;
    private static final String KEY_AD_CONFIG = "SP_AdConfig";

    public static void saveAdConfig(Context context, AdConfigBean bean) {
        data = bean;
        String userStr = JSONObject.toJSONString(bean);
        SPFUtils.put(context, KEY_AD_CONFIG, userStr);
    }

    private static AdConfigBean getAdConfig(Context context) {
        String userStr = (String) SPFUtils.get(context, KEY_AD_CONFIG, "");
        AdConfigBean bean = JSONObject.parseObject(userStr, AdConfigBean.class);
        if (bean == null) {
            bean = new AdConfigBean();
        }
        return bean;
    }

    /**
     * 开屏广告
     */
    public static String splashAdConfig(Context context) {
        if (data == null) {
            data = getAdConfig(context);
        }
        return data.getOpen_screen();
//        return "baidu:1,gdt:1,csj:0";
    }

    /**
     * 翻页广告
     */
    public static String turnPageAdConfig(Context context) {
        if (data == null) {
            data = getAdConfig(context);
        }
        return data.getTurn_page();
//        return "baidu:0,gdt:1,csj:0";
    }

    /**
     * 阅读页下方Banner
     */
    public static String bannerAdConfig(Context context) {
        if (data == null) {
            data = getAdConfig(context);
        }
        return data.getBanner();
//        return "baidu:0,gdt:1,csj:1";
    }

    /**
     * 书架广告
     */
    public static String listAdConfig(Context context) {
        if (data == null) {
            data = getAdConfig(context);
        }
        return data.getInformation_flow();
//        return "baidu:0,gdt:1,csj:0";
    }

    /**
     * 看视频的金币
     */
    public static String welfareBonusesConfig(Context context) {
        if (data == null) {
            data = getAdConfig(context);
        }
        String bonusesConfig = data.getBonuses();
        if (TextUtils.isEmpty(bonusesConfig)) {
            bonusesConfig = "baidu:1,gdt:2,csj:2";
        }
        return bonusesConfig;
//        return "baidu:5,gdt:1,csj:1";
    }

    /**
     * 看视频免广告
     */
    public static String welfareAdvertConfig(Context context) {
        if (data == null) {
            data = getAdConfig(context);
        }
        String advertConfig = data.getAdvert();
        if (TextUtils.isEmpty(advertConfig)) {
            advertConfig = "baidu:1,gdt:2,csj:2";
        }
        return advertConfig;
//        return "baidu:4,gdt:1,csj:1";
    }

    public static String webViewAdConfig(Context context) {
        if (data == null) {
            data = getAdConfig(context);
        }
//        return data.getEeeee();
        return "baidu:0,gdt:1,csj:0";
    }

    public static String midAdConfig(Context context) {
        if (data == null) {
            data = getAdConfig(context);
        }
//        return data.getFffff();
        return "baidu:0,gdt:1,csj:1";
    }


    public static String preMoiveAdConfig(Context context) {
        if (data == null) {
            data = getAdConfig(context);
        }
//        return data.getCcccc();
        return "baidu:0,gdt:1,csj:0";
    }

}
