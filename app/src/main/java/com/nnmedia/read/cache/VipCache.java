package com.nnmedia.read.cache;

import android.content.Context;
import android.text.TextUtils;

import com.nnmedia.read.contact.Common;
import com.nnmedia.read.utils.SPFUtils;

public class VipCache {
    private static String cashComboId;
    private static String hPointComboId;
    private static String GoldComboId;
    private static String pointComboId;

    public static void saveCombo(Context context, String mComboId, String alipay, String weixin, int cdkey, int type) {
        switch (type) {
            case 0:
                cashComboId = mComboId;
                SPFUtils.put(context, Common.KEY_CASH_COMBOID, cashComboId);
                SPFUtils.put(context, Common.KEY_CDKEY, cdkey);
                SPFUtils.put(context, Common.KEY_CASH_ALIPAY, alipay);
                SPFUtils.put(context, Common.KEY_CASH_WEIXIN, weixin);
                break;
//            case 1:
//                hPointComboId = mComboId;
//                SPFUtils.put(context, Common.KEY_H_COMBOID, hPointComboId);
//                SPFUtils.put(context, Common.KEY_H_CDKEY, cdkey);
//                SPFUtils.put(context, Common.KEY_H_ALIPAY, alipay);
//                SPFUtils.put(context, Common.KEY_H_WEIXIN, weixin);
//                break;
            case 1:
                GoldComboId = mComboId;
                SPFUtils.put(context, Common.KEY_GOLD_COMBOID, GoldComboId);
                break;
            case 2:
                pointComboId = mComboId;
                SPFUtils.put(context, Common.KEY_POINT_COMBOID, pointComboId);
                break;
        }
    }

    public static String getComboId(Context context, int type) {
        switch (type) {
            case 0:
                if (!TextUtils.isEmpty(cashComboId)) {
                    return cashComboId;
                }
                return (String) SPFUtils.get(context, Common.KEY_CASH_COMBOID, "");
//            case 1:
//                if (!TextUtils.isEmpty(hPointComboId)) {
//                    return hPointComboId;
//                }
//                return (String) SPFUtils.get(context, Common.KEY_H_COMBOID, "");
            case 1:
                if (!TextUtils.isEmpty(GoldComboId)) {
                    return GoldComboId;
                }
                return (String) SPFUtils.get(context, Common.KEY_GOLD_COMBOID, "");
            case 2:
                if (!TextUtils.isEmpty(pointComboId)) {
                    return pointComboId;
                }
                return (String) SPFUtils.get(context, Common.KEY_POINT_COMBOID, "");
            default:
                if (!TextUtils.isEmpty(cashComboId)) {
                    return cashComboId;
                }
                return (String) SPFUtils.get(context, Common.KEY_CASH_COMBOID, "");
        }
    }

    public static int getCDKEY(Context context, int type) {
        switch (type) {
            case 0:
                return (int) SPFUtils.get(context, Common.KEY_CDKEY, 0);
            case 1:
                return (int) SPFUtils.get(context, Common.KEY_H_CDKEY, 0);
            default:
                return (int) SPFUtils.get(context, Common.KEY_CDKEY, 0);
        }
    }

    public static String getAlipay(Context context, int type) {
        switch (type) {
            case 0:
                return (String) SPFUtils.get(context, Common.KEY_CASH_ALIPAY, "");
            case 1:
                return (String) SPFUtils.get(context, Common.KEY_H_ALIPAY, "");
            default:
                return (String) SPFUtils.get(context, Common.KEY_CASH_ALIPAY, "");
        }
    }

    public static String getWeixin(Context context, int type) {
        switch (type) {
            case 0:
                return (String) SPFUtils.get(context, Common.KEY_CASH_WEIXIN, "");
            case 1:
                return (String) SPFUtils.get(context, Common.KEY_H_WEIXIN, "");
            default:
                return (String) SPFUtils.get(context, Common.KEY_CASH_WEIXIN, "");
        }
    }

    public static void clear(Context context) {
        SPFUtils.remove(context, Common.KEY_CASH_ALIPAY);
        SPFUtils.remove(context, Common.KEY_CASH_WEIXIN);
        SPFUtils.remove(context, Common.KEY_H_ALIPAY);
        SPFUtils.remove(context, Common.KEY_H_WEIXIN);
        SPFUtils.remove(context, Common.KEY_CASH_COMBOID);
        SPFUtils.remove(context, Common.KEY_H_COMBOID);
        SPFUtils.remove(context, Common.KEY_GOLD_COMBOID);
        SPFUtils.remove(context, Common.KEY_POINT_COMBOID);
    }
}