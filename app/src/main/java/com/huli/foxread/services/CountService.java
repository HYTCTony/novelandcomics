package com.huli.foxread.services;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import com.huli.foxread.ebsevent.WelfareChangeEvent;

import org.greenrobot.eventbus.EventBus;

import androidx.annotation.Nullable;

/**
 * 激励视频任务冷却倒计时
 */
public class CountService extends Service {
    public static final String EXTRA_COUNT_MIN = "key_count_down";

    private CountDownTimer timer;

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int totalMin = intent.getIntExtra(EXTRA_COUNT_MIN, 0);      //秒
        if (timer != null) {
            timer.cancel();
        }

        if (totalMin > 0) {
            /**
             * CountDownTimer timer = new CountDownTimer(3000, 1000)中，
             * 第一个参数表示总时间，第二个参数表示间隔时间。
             * 意思就是每隔一秒会回调一次方法onTick，然后1秒之后会回调onFinish方法。
             */
            timer = new CountDownTimer(totalMin * 1000 + 500, 30 * 1000) {
                public void onTick(long millisUntilFinished) {
                    Log.e("sssssss", "倒计时===" + millisUntilFinished);
                }

                public void onFinish() {
                    Log.e("sssssss", "倒计时结束");
                    //通知福利列表刷新
                    EventBus.getDefault().post(new WelfareChangeEvent(true));

                    //结束服务
                    stopSelf();
                }
            };
            //调用 CountDownTimer 对象的 start() 方法开始倒计时，也不涉及到线程处理
            timer.start();
        } else {

            stopSelf();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }
}
