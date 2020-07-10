package com.huli.foxread.config;

import android.app.Application;

import com.huli.foxread.R;
import com.hytc.ads.TogetherAd;

import java.util.HashMap;
import java.util.Map;

/**
 * 项目名称：FoxRead
 * 创建人：Bill
 * 创建时间：2020/7/6  10:14
 * 备注：聚合广告
 */
public class TgAdManager {
    public static void init(Application context) {
        //穿山甲广告初始化
        Map<String, String> csjIdMap = new HashMap<>();
        csjIdMap.put(TogetherAdConst.AD_SPLASH, "887319954");
        csjIdMap.put(TogetherAdConst.AD_FLOW_BOOKRACK, "945165433");
        csjIdMap.put(TogetherAdConst.AD_WELFARE_STIMULATE_COIN, "945166035");
        csjIdMap.put(TogetherAdConst.AD_WELFARE_STIMULATE_2, "945192284");
        csjIdMap.put(TogetherAdConst.AD_BTM_BANNER_DARK_BLUE, "945191946");
        csjIdMap.put(TogetherAdConst.AD_BTM_BANNER_POOL_BLUE, "945191946");
        csjIdMap.put(TogetherAdConst.AD_BTM_BANNER_GREEN, "945191946");
        csjIdMap.put(TogetherAdConst.AD_BTM_BANNER_ASHEN, "945191946");
        csjIdMap.put(TogetherAdConst.AD_BTM_BANNER_PINK, "945191946");
        csjIdMap.put(TogetherAdConst.AD_BTM_BANNER_NIGHT, "945191946");
        csjIdMap.put(TogetherAdConst.AD_BTM_BANNER_YELLOW_PAPER, "945191946");
        csjIdMap.put(TogetherAdConst.AD_CENTER_DARK_BLUE, "945245837");
        csjIdMap.put(TogetherAdConst.AD_CENTER_POOL_BLUE, "945245836");
        csjIdMap.put(TogetherAdConst.AD_CENTER_GREEN, "945245831");
        csjIdMap.put(TogetherAdConst.AD_CENTER_ASHEN, "945245835");
        csjIdMap.put(TogetherAdConst.AD_CENTER_PINK, "945191678");
        csjIdMap.put(TogetherAdConst.AD_CENTER_NIGHT, "945245839");
        csjIdMap.put(TogetherAdConst.AD_CENTER_YELLOW_PAPER, "945191706");
        TogetherAd.initCsjAd(context, "5063649", context.getString(R.string.app_name), csjIdMap, true);

        //腾讯广告初始化
        Map<String, String> gdtIdMap = new HashMap<>();
        gdtIdMap.put(TogetherAdConst.AD_SPLASH, "7061113827587147");
        gdtIdMap.put(TogetherAdConst.AD_FLOW_BOOKRACK, "6061817848543342");
        gdtIdMap.put(TogetherAdConst.AD_WELFARE_STIMULATE_COIN, "4071315827299728");
        gdtIdMap.put(TogetherAdConst.AD_WELFARE_STIMULATE_2, "8071116807691652");
        gdtIdMap.put(TogetherAdConst.AD_BTM_BANNER_DARK_BLUE, "6031314807992506");
        gdtIdMap.put(TogetherAdConst.AD_BTM_BANNER_POOL_BLUE, "1051616857493878");
        gdtIdMap.put(TogetherAdConst.AD_BTM_BANNER_GREEN, "9091012838409041");
        gdtIdMap.put(TogetherAdConst.AD_BTM_BANNER_ASHEN, "4021613888626047");
        gdtIdMap.put(TogetherAdConst.AD_BTM_BANNER_PINK, "1051910888820190");
        gdtIdMap.put(TogetherAdConst.AD_BTM_BANNER_NIGHT, "7031518858722295");
        gdtIdMap.put(TogetherAdConst.AD_BTM_BANNER_YELLOW_PAPER, "9071118858437887");
        gdtIdMap.put(TogetherAdConst.AD_CENTER_DARK_BLUE, "9021617988195254");
        gdtIdMap.put(TogetherAdConst.AD_CENTER_POOL_BLUE, "8001913978896158");
        gdtIdMap.put(TogetherAdConst.AD_CENTER_GREEN, "3031810938994200");
        gdtIdMap.put(TogetherAdConst.AD_CENTER_ASHEN, "4041615958797017");
        gdtIdMap.put(TogetherAdConst.AD_CENTER_PINK, "3041115998392120");
        gdtIdMap.put(TogetherAdConst.AD_CENTER_NIGHT, "8021218988395143");
        gdtIdMap.put(TogetherAdConst.AD_CENTER_YELLOW_PAPER, "8011911958491134");
        TogetherAd.initGDTAd(context, "207010113294", gdtIdMap);

        //百度广告初始化
        Map<String, String> baiduIdMap = new HashMap<>();
        baiduIdMap.put(TogetherAdConst.AD_SPLASH, "7154344");
        baiduIdMap.put(TogetherAdConst.AD_FLOW_BOOKRACK, "0000000");
        baiduIdMap.put(TogetherAdConst.AD_WELFARE_STIMULATE_COIN, "7154347");
        baiduIdMap.put(TogetherAdConst.AD_WELFARE_STIMULATE_2, "7154348");
        baiduIdMap.put(TogetherAdConst.AD_BTM_BANNER_DARK_BLUE, "0000000");
        baiduIdMap.put(TogetherAdConst.AD_BTM_BANNER_POOL_BLUE, "0000000");
        baiduIdMap.put(TogetherAdConst.AD_BTM_BANNER_GREEN, "0000000");
        baiduIdMap.put(TogetherAdConst.AD_BTM_BANNER_ASHEN, "0000000");
        baiduIdMap.put(TogetherAdConst.AD_BTM_BANNER_PINK, "0000000");
        baiduIdMap.put(TogetherAdConst.AD_BTM_BANNER_NIGHT, "0000000");
        baiduIdMap.put(TogetherAdConst.AD_BTM_BANNER_YELLOW_PAPER, "0000000");
        baiduIdMap.put(TogetherAdConst.AD_CENTER_DARK_BLUE, "0000000");
        baiduIdMap.put(TogetherAdConst.AD_CENTER_POOL_BLUE, "0000000");
        baiduIdMap.put(TogetherAdConst.AD_CENTER_GREEN, "0000000");
        baiduIdMap.put(TogetherAdConst.AD_CENTER_ASHEN, "0000000");
        baiduIdMap.put(TogetherAdConst.AD_CENTER_PINK, "0000000");
        baiduIdMap.put(TogetherAdConst.AD_CENTER_NIGHT, "0000000");
        baiduIdMap.put(TogetherAdConst.AD_CENTER_YELLOW_PAPER, "0000000");
        TogetherAd.initBaiduAd(context, "c53adfbd", baiduIdMap);
    }
}
