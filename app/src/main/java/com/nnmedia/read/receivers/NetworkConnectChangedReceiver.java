package com.nnmedia.read.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.nnmedia.read.ebsevent.NetworkChangeEvent;
import com.nnmedia.read.utils.NetworkUtil;

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