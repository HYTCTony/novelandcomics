package com.huli.foxread.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.huli.foxread.ebsevent.NetworkChangeEvent;
import com.huli.foxread.utils.NetworkUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * 接收网络变化通知
 */
public class NetworkConnectChangedReceiver extends BroadcastReceiver {
    private static final String TAG = "NetworkConnectChanged";

    @Override
    public void onReceive(Context context, Intent intent) {
        //**判断当前的网络连接状态是否可用*/
        boolean isConnected = NetworkUtil.isNetworkAvailable(context);
//        Log.e(TAG, "onReceive: 当前网络 " + isConnected);
        EventBus.getDefault().post(new NetworkChangeEvent(isConnected));
    }

}