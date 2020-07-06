package com.hytc.ads.other

import android.util.Log

/**
 * 备注：日志打印
 */
internal fun Any.logd(msg: String?) {
    Log.d("TogetherAd", "${this.javaClass.simpleName}: $msg")
}

internal fun Any.loge(msg: String?) {
    Log.e("TogetherAd", "${this.javaClass.simpleName}: $msg")
}