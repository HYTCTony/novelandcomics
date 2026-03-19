package com.nnmedia.read.cache;

import android.content.Context;
import android.text.TextUtils;

import com.nnmedia.read.contact.Common;
import com.nnmedia.read.contact.Consts;
import com.nnmedia.read.utils.SPFUtils;

public class AccountCache {
    private static String account;

    public static void saveAccout(Context context, String mAccount) {
        account = Consts.TIP + mAccount;
        SPFUtils.put(context, Common.KEY_ACCOUNT, account);
    }

    public static String getAccout(Context context) {
        if (!TextUtils.isEmpty(account)) {
            return account;
        }
        return (String) SPFUtils.get(context, Common.KEY_ACCOUNT, "");
    }

    public static void clearAccout(Context context) {
        SPFUtils.remove(context, Common.KEY_ACCOUNT);
    }
}
