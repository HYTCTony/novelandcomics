//package com.huli.foxread.ui.wakeup;
//
///**
// * 创建时间：2019/5/16  16:54
// */
//
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.os.Bundle;
//
//import com.alibaba.fastjson.JSONObject;
//import com.huli.foxread.R;
//import com.huli.foxread.ui.activities.MainActivity;
//import com.huli.foxread.utils.PackageUtils;
//import com.umeng.message.UmengNotifyClickActivity;
//
//import org.android.agoo.common.AgooConstants;
//
//import java.util.Map;
//
//import androidx.annotation.Nullable;
//
///**
// * 系统通道
// * 唤醒APP的临时界面。当用户把APP杀死或者双击退出后，点击notification，先到这个界面，唤醒APP，然后再去目标界面。
// * 这样做的原因：
// * 1、假如用户双击退出APP，推送是Activity_1。这个时候，手机系统并没有杀死APP，用户点击通知栏，页面被唤醒，进行后续操作，当用户在Activity_1点击返回键，界面被销毁，直接退出程序。
// * 2、假如用户手动或者某些情况下手机系统自己杀死了APP，这个时候做推送，用户点击后，有时候仅仅是打开APP首页，不会去到目标界面。有时候不会有任何反应
// * 为了避免上述2个情况出现，在这里先判断手机状态，双击或被杀死的话，就先唤醒APP，然后再去目标界面，这样，用户点击返回键，也不会直接退出。增加用户在APP中的停留时间。
// */
//
//public class SysChannelAwakeTempActivity extends UmengNotifyClickActivity {
//    private static String TAG = SysChannelAwakeTempActivity.class.getName();
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_transparency);
//    }
//
//
//    @Override
//    public void onMessage(Intent intent) {
//        super.onMessage(intent);  //此方法必须调用，否则无法统计打开数
//        if (intent != null) {
//            String msgBody = intent.getStringExtra(AgooConstants.MESSAGE_BODY);
//            JSONObject jsonObject = JSONObject.parseObject(msgBody);
//            String displayType = jsonObject.getString("display_type");
//                /* if(displayType.equals("notification")){
//
//                }*/
//            try {
//                JSONObject body = jsonObject.getJSONObject("body");
//                String afterOpen = body.getString("after_open");
//                if ("go_activity".equals(afterOpen)) {
//
//                    //参数
//                    JSONObject extraObj = jsonObject.getJSONObject("extra");
//                    String targetActivity = body.getString("activity");
//                    Intent it = new Intent();
//                    it.setClassName(this, targetActivity);
//                    for (Object eSet : extraObj.entrySet()) {
//                        String key = String.valueOf(((Map.Entry) eSet).getKey());       //key
//                        String value = String.valueOf(((Map.Entry) eSet).getValue());   //value
//                        it.putExtra(key, value);        //参数填充
//                    }
//
//                    Intent[] its = new Intent[]{new Intent(this, MainActivity.class), it};      //退出目标Activity时，启动MainActivity
//                    startActivities(its);
//                } else if ("go_custom".equals(afterOpen)) {
//                    String packageName = PackageUtils.getPackageName(SysChannelAwakeTempActivity.this);
//                    PackageManager pm = getPackageManager();
//                    if (packageName != null) {
//                        Intent launchIntent = pm.getLaunchIntentForPackage(packageName);
//                        startActivity(launchIntent);
//                    }
//                }
//            } catch (Exception e) {
//
//            } finally {
//                finish();
//            }
////            Log.e(TAG, "App死了---onMessage===" + body);
//        } else {
////            Log.e(TAG, "App死了---onMessage===空---走这里");
//            String packageName = PackageUtils.getPackageName(SysChannelAwakeTempActivity.this);
//            PackageManager pm = getPackageManager();
//            if (packageName != null) {
//                Intent launchIntent = pm.getLaunchIntentForPackage(packageName);
//                startActivity(launchIntent);
//                finish();
//            }
//        }
//    }
//}