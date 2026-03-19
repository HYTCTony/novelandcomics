//package com.huli.foxread.services;
//
//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.BitmapFactory;
//import android.os.Build;
//import android.text.TextUtils;
//import android.util.Log;
//
//import com.huli.foxread.R;
//import com.umeng.message.UmengMessageService;
//import com.umeng.message.entity.UMessage;
//
//import org.json.JSONObject;
//
//public class YouMengPushIntentService extends UmengMessageService {
//    private static final String TAG = YouMengPushIntentService.class.getSimpleName();
//
//    public void getNotification(Context context, String title, String msg) {
//        Log.e("rrrrrrrrrrrrrrrr", "getNotification===" + msg);
//
//        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        int id = (int) (System.currentTimeMillis() / 1000);
////        Intent intentClick = new Intent(this, NotificationClickReceiver.class);
////        intentClick.putExtra("title", title);
////        intentClick.putExtra("msg", msg);
////        intentClick.setAction("notification_clicked");
////        intentClick.putExtra(NotificationClickReceiver.TYPE, 0); //0代表点击
////        PendingIntent pendingIntentClick = PendingIntent.getBroadcast(this, id, intentClick, PendingIntent.FLAG_ONE_SHOT);
////
////        Intent intentCancel = new Intent(this, NotificationClickReceiver.class);
////        intentCancel.setAction("notification_cancelled");
////        intentCancel.putExtra(NotificationClickReceiver.TYPE, 1); //1代表清除的监听
////        PendingIntent pendingIntentCancel = PendingIntent.getBroadcast(this, id, intentCancel, PendingIntent.FLAG_ONE_SHOT);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {  //判断8.0，若为8.0型号的手机进行创下一下的通知栏
//            NotificationChannel channel = new NotificationChannel("channel_id", "channel_name", NotificationManager.IMPORTANCE_HIGH);
//            channel.enableLights(true);
//            if (manager != null) {
//                manager.createNotificationChannel(channel);
//            }
//            Notification.Builder builder = new Notification.Builder(context, "channel_id");
//            builder.setSmallIcon(R.mipmap.ic_launcher)
//                    .setWhen(System.currentTimeMillis())
//                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
//                    .setContentTitle(title)
//                    .setContentText(msg)
//                    .setAutoCancel(true);
////                    .setContentIntent(pendingIntentClick)
////                    .setDeleteIntent(pendingIntentCancel);
//            if (manager != null) {
//                manager.notify(id, builder.build());
//            }
//        } else {
//            Notification.Builder builder = new Notification.Builder(context);
//            builder.setSmallIcon(R.mipmap.ic_launcher)
//                    .setWhen(System.currentTimeMillis())
//                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
//                    .setContentTitle(title)
//                    .setContentText(msg)
//                    .setVisibility(Notification.VISIBILITY_PUBLIC).setPriority(Notification.PRIORITY_HIGH)
//                    .setAutoCancel(true);
////                    .setContentIntent(pendingIntentClick)
////                    .setDeleteIntent(pendingIntentCancel);
//            if (manager != null) {
//                manager.notify(id, builder.build());
//            }
//        }
//    }
//
//
//    @Override
//    public void onMessage(Context context, Intent intent) {
//        Log.e("rrrrrrrrrrrrrrrr", "onMessage===intent");
//        try {
//            Intent data = new Intent(intent);
////            data.setClass(context, DialogActivity.class);
////            data.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//需为Intent添加Flag：Intent.FLAG_ACTIVITY_NEW_TASK，否则无法启动Activity。
////            context.startActivity(data);
//            //可以通过MESSAGE_BODY取得消息体
//
//            final String message = intent.getStringExtra("body");
//            Log.e("rrrrrrrrrrrrrrrr", "onMessage===" + message);
//            if (TextUtils.isEmpty(message)) {
//                return;
//            }
//            final UMessage msg = new UMessage(new JSONObject(message));
////            getNotification(context, msg.title, msg.text);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}