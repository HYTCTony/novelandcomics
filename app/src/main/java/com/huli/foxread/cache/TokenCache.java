package com.huli.foxread.cache;

import android.content.Context;
import android.text.TextUtils;

import com.huli.foxread.contact.Common;
import com.huli.foxread.utils.SPFUtils;

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

    public static void clearToken(Context context){
        SPFUtils.remove(context, Common.KEY_TOKEN);
    }
}
