package com.nnmedia.read.utils;

import com.alibaba.fastjson.JSONObject;
import com.nnmedia.read.contact.Consts;

import java.util.HashMap;
import java.util.Map;

/**
 * 网络请求参数构造工具类
 */

public class ParamUtil {

    /**
     * 构造生成网络请求所需参数
     *
     * @param params
     * @return
     */
    public static String dealParams(Map<String, Object> params) {
        String jsonString = JSONObject.toJSONString(params);
        return encryptStr(jsonString);
    }

    /**
     * 构造生成网络请求所需参数(仅仅需要token的)
     *
     * @param token
     * @return
     */
    public static String dealTokenParam(String token) {
        Map<String, Object> map = new HashMap<>();
        map.put(Consts.TOKEN, token);
        String jsonString = JSONObject.toJSONString(map);
        return encryptStr(jsonString);
    }


   /* public static String encryptStr(String paramStr) {
        String randomStr = RSAUtil.getRandomString(8);        //8位随机字符串
        return RSAUtil.encode(randomStr) + DESUtil.encrypt(paramStr, randomStr);
    }*/

    public static String encryptStr(String paramStr) {
        String pwd = RSAUtil.getRandomString(16);
        String offset = RSAUtil.getRandomString(16);
        return RSAUtil.encode(pwd + offset) + AESCBCUtil.encrypt(paramStr, pwd, offset);
    }

}
