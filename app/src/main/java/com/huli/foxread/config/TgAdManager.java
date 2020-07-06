package com.huli.foxread.config;

import android.app.Application;
import android.content.Context;

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
        Map<String, String> csjIdMap = new HashMap<>();
        csjIdMap.put(TogetherAdConst.AD_SPLASH, "887319954");
        csjIdMap.put(TogetherAdConst.AD_FLOW_BOOKRACK, "945165433");
        csjIdMap.put(TogetherAdConst.AD_WELFARE_STIMULATE_COIN, "945166035");
        csjIdMap.put(TogetherAdConst.AD_WELFARE_STIMULATE_2, "945192284");
        csjIdMap.put(TogetherAdConst.AD_BTM_BANNER_DARK_BLUE, "945160023");
        csjIdMap.put(TogetherAdConst.AD_BTM_BANNER_POOL_BLUE, "945160023");
        csjIdMap.put(TogetherAdConst.AD_BTM_BANNER_GREEN, "945160023");
        csjIdMap.put(TogetherAdConst.AD_BTM_BANNER_ASHEN, "945160023");
        csjIdMap.put(TogetherAdConst.AD_BTM_BANNER_PINK, "945160023");
        csjIdMap.put(TogetherAdConst.AD_BTM_BANNER_NIGHT, "945160023");
        csjIdMap.put(TogetherAdConst.AD_BTM_BANNER_YELLOW_PAPER, "945160023");
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
        gdtIdMap.put(TogetherAdConst.AD_CENTER_DARK_BLUE, "9001217858920807");
        gdtIdMap.put(TogetherAdConst.AD_CENTER_POOL_BLUE, "9071118858437887");
        gdtIdMap.put(TogetherAdConst.AD_CENTER_GREEN, "2041811858431090");
        gdtIdMap.put(TogetherAdConst.AD_CENTER_ASHEN, "2031817858638698");
        gdtIdMap.put(TogetherAdConst.AD_CENTER_PINK, "1061415838541032");
        gdtIdMap.put(TogetherAdConst.AD_CENTER_NIGHT, "6001219898794913");
        gdtIdMap.put(TogetherAdConst.AD_CENTER_YELLOW_PAPER, "4011019898634903");
        TogetherAd.initGDTAd(context, "207010113294", gdtIdMap);
    }
}
