package com.huli.foxread.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.huli.foxread.contact.Common;
import com.huli.foxread.ui.activities.CommonWebActivity;
import com.huli.foxread.ui.activities.MainActivity;

public class ClickJumpUtil {

    public static void handleJump(Activity mActivity, String link, int isJump, int needLogin) {
        if (link.startsWith("http")) {
            if (isJump == 1) {        //应用内
                Intent intent = new Intent(mActivity, CommonWebActivity.class);
                intent.putExtra(Common.KEY_URL, link);
                mActivity.startActivity(intent);
            } else {
                try {
                    Uri uri = Uri.parse(link);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    mActivity.startActivity(intent);
                } catch (Exception e) {

                }
            }
        } else if (link.startsWith("com.huli")) {
            // 隐示意图打开Activity
            try {
                Intent intent = new Intent();
                String[] split = link.split("&");
                if (split.length >= 2) {
                    intent.setAction(split[0]);
                    intent.putExtra(Common.KEY_BOOK_ID, split[1]);
                } else {
                    intent.setAction(link);
                }
                intent.addCategory("android.intent.category.DEFAULT");
                mActivity.startActivity(intent);
            } catch (Exception e) {
//                Tos.showShort(mActivity, entity.getTitle());
            }
        } else {            //MainActivity切换
            if (mActivity instanceof MainActivity) {
                if (link.equals(Common.SWITCH2_BOOKSTORE)) {
                    ((MainActivity) mActivity).switch2Bookstore();
                } else if (link.equals(Common.SWITCH2_WELFARE) || link.equals(Common.SIGNIN_NEWBIE)) {
                    ((MainActivity) mActivity).switch2Welfare();
                }
            }
        }
    }
}

