//package com.nnmedia.read.config;
//
//import android.app.Application;
//
//import com.nnmedia.novel.R;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class TgAdManager {
//    public static void init(Application context) {
//        //穿山甲广告初始化
//        Map<String, String> csjIdMap = new HashMap<>();
//        csjIdMap.put(TogetherAdConst.AD_SPLASH, "887319954");
//        csjIdMap.put(TogetherAdConst.AD_FLOW_BOOKRACK, "945165433");
//        csjIdMap.put(TogetherAdConst.AD_WELFARE_STIMULATE_COIN, "945166035");
//        csjIdMap.put(TogetherAdConst.AD_WELFARE_STIMULATE_2, "945192284");
//        csjIdMap.put(TogetherAdConst.AD_BTM_BANNER, "945191946");
//        csjIdMap.put(TogetherAdConst.AD_CENTER_DARK_BLUE, "945245837");
//        csjIdMap.put(TogetherAdConst.AD_CENTER_POOL_BLUE, "945245836");
//        csjIdMap.put(TogetherAdConst.AD_CENTER_GREEN, "945245831");
//        csjIdMap.put(TogetherAdConst.AD_CENTER_ASHEN, "945245835");
//        csjIdMap.put(TogetherAdConst.AD_CENTER_PINK, "945191678");
//        csjIdMap.put(TogetherAdConst.AD_CENTER_NIGHT, "945245839");
//        csjIdMap.put(TogetherAdConst.AD_CENTER_YELLOW_PAPER, "945191706");
//        TogetherAd.initCsjAd(context, "5063649", context.getString(R.string.app_name), csjIdMap, true);
//
//        //腾讯广告初始化
//        Map<String, String> gdtIdMap = new HashMap<>();
//        gdtIdMap.put(TogetherAdConst.AD_SPLASH, "7061113827587147");
//        gdtIdMap.put(TogetherAdConst.AD_FLOW_BOOKRACK, "6061817848543342");
//        gdtIdMap.put(TogetherAdConst.AD_WELFARE_STIMULATE_COIN, "4071315827299728");
//        gdtIdMap.put(TogetherAdConst.AD_WELFARE_STIMULATE_2, "8071116807691652");
//        gdtIdMap.put(TogetherAdConst.AD_BTM_BANNER, "8061221063536701");
//        gdtIdMap.put(TogetherAdConst.AD_CENTER_DARK_BLUE, "6061817848543342");
//        gdtIdMap.put(TogetherAdConst.AD_CENTER_POOL_BLUE, "6061817848543342");
//        gdtIdMap.put(TogetherAdConst.AD_CENTER_GREEN, "6061817848543342");
//        gdtIdMap.put(TogetherAdConst.AD_CENTER_ASHEN, "6061817848543342");
//        gdtIdMap.put(TogetherAdConst.AD_CENTER_PINK, "6061817848543342");
//        gdtIdMap.put(TogetherAdConst.AD_CENTER_NIGHT, "6061817848543342");
//        gdtIdMap.put(TogetherAdConst.AD_CENTER_YELLOW_PAPER, "6061817848543342");
//        TogetherAd.initGDTAd(context, "207010113294", gdtIdMap);
//
//        //百度广告初始化
//        Map<String, String> baiduIdMap = new HashMap<>();
//        baiduIdMap.put(TogetherAdConst.AD_SPLASH, "7154344");
//        baiduIdMap.put(TogetherAdConst.AD_FLOW_BOOKRACK, "0000000");
//        baiduIdMap.put(TogetherAdConst.AD_WELFARE_STIMULATE_COIN, "7154347");
//        baiduIdMap.put(TogetherAdConst.AD_WELFARE_STIMULATE_2, "7154348");
//        baiduIdMap.put(TogetherAdConst.AD_BTM_BANNER, "0000000");
//        baiduIdMap.put(TogetherAdConst.AD_CENTER_DARK_BLUE, "0000000");
//        baiduIdMap.put(TogetherAdConst.AD_CENTER_POOL_BLUE, "0000000");
//        baiduIdMap.put(TogetherAdConst.AD_CENTER_GREEN, "0000000");
//        baiduIdMap.put(TogetherAdConst.AD_CENTER_ASHEN, "0000000");
//        baiduIdMap.put(TogetherAdConst.AD_CENTER_PINK, "0000000");
//        baiduIdMap.put(TogetherAdConst.AD_CENTER_NIGHT, "0000000");
//        baiduIdMap.put(TogetherAdConst.AD_CENTER_YELLOW_PAPER, "0000000");
//        TogetherAd.initBaiduAd(context, "c53adfbd", baiduIdMap);
//    }
//}
