package com.nnmedia.read.ui.wakeup;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;

/**
 * 模拟的消息push消息接收器。假的     //TODO 应该在接到推送消息--> Notification点击：1.JPush可以在它控制台定好点击Notification启动AwakeTempActivity页面；2.也可以不用它的Notification，自己创建并设置
 */
public class MessageReceive {

    private void getPushMessaage(Context context, String message) {
        try {

            JSONObject jsonObj = JSONObject.parseObject(message);

           /* if (!Util.checkStringIsEmpty(jsonObj.optString("app")) && !Util.isAllowMessageShow()) {
                //所需要的数据不为空，并且，是运行接收的Activity

                JSONObject value_jo = new JSONObject(jsonObj.optString("app"));

                //类型
                String type = value_jo.optString("type");
                AwakePageInfoBean bean = new AwakePageInfoBean();
                bean.setType(type);

                Intent intent = new Intent(context, SysChannelAwakeTempActivity.class);
                intent.putExtra("AwakePageInfoBean", bean);

                //把intent放到PendingIntent中，然后和Notification进行后续处理。。。。。。

            }*/

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}