package com.huli.foxread.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.huli.foxread.cache.UserInfoCache;
import com.huli.foxread.contact.Common;
import com.huli.foxread.entity.BannerADEntity;
import com.huli.foxread.ui.activities.CommonWebActivity;
import com.huli.foxread.ui.activities.LoginActivity;

public class BannerJumpUtil {

    public static void handleBannerJump(Context context, BannerADEntity entity) {
        String link = entity.getLink();
        if (entity.getJump() == 1) {        //应用内
            if (link.startsWith("http")) {
                Intent intent = new Intent(context, CommonWebActivity.class);
                intent.putExtra(Common.KEY_URL, link);
                context.startActivity(intent);
            } else {
                // 隐示意图打开Activity
                try {
                    Intent intent = new Intent();
                    if (link.contains(Common.INTENT_ACTION_BOOK_DT)) {
                        String[] split = link.split("&");
                        intent.setAction(split[0]);
                        intent.putExtra(Common.KEY_BOOK_ID, split[1]);
                    } else if (link.contains(Common.INTENT_ACTION_PRIVILEGE)) {
                        if (UserInfoCache.getIsVisitor(context)) {
                            context.startActivity(new Intent(context, LoginActivity.class));
                            return;
                        }
                        intent.setAction(link);
                    }
                    intent.addCategory("android.intent.category.DEFAULT");
                    context.startActivity(intent);
                } catch (Exception e) {
                    Tos.showShort(context, entity.getTitle());
                }
            }

        } else {
            try {
                Uri uri = Uri.parse(link);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            } catch (Exception e) {

            }
        }
    }
}
