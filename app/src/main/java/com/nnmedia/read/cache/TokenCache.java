package com.nnmedia.read.cache;

import android.content.Context;
import android.text.TextUtils;

import com.nnmedia.read.contact.Common;
import com.nnmedia.read.utils.SPFUtils;

public class TokenCache {
    private static String token;

    public static void saveToken(Context context, String mToken) {
        token = mToken;
        SPFUtils.put(context, Common.KEY_TOKEN, mToken);
    }

    public static String getToken(Context context) {
        if (!TextUtils.isEmpty(token)) {
            return token;
        }
        return (String) SPFUtils.get(context, Common.KEY_TOKEN, "");
    }

    public static void clearToken(Context context) {
        SPFUtils.remove(context, Common.KEY_TOKEN);
    }
}
