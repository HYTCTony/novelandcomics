package com.hytc.ads.helper.flow

import android.app.Activity
import android.util.DisplayMetrics
import androidx.annotation.NonNull
import com.baidu.mobad.feeds.BaiduNative
import com.baidu.mobad.feeds.NativeErrorCode
import com.baidu.mobad.feeds.NativeResponse
import com.baidu.mobad.feeds.RequestParameters
import com.bytedance.sdk.openadsdk.AdSlot
import com.bytedance.sdk.openadsdk.TTAdNative
import com.bytedance.sdk.openadsdk.TTAdSdk
import com.bytedance.sdk.openadsdk.TTNativeExpressAd
import com.hytc.ads.R
import com.hytc.ads.TogetherAd
import com.hytc.ads.helper.AdBase
import com.hytc.ads.other.AdNameType
import com.hytc.ads.other.AdRandomUtil
import com.hytc.ads.other.logd
import com.hytc.ads.other.loge
import com.qq.e.ads.cfg.VideoOption
import com.qq.e.ads.nativ.ADSize
import com.qq.e.ads.nativ.ADSize.AUTO_HEIGHT
import com.qq.e.ads.nativ.NativeExpressAD
import com.qq.e.ads.nativ.NativeExpressADView
import com.qq.e.comm.util.AdError
import java.lang.ref.WeakReference
import java.util.*
import com.baidu.mobad.feeds.BaiduNativeManager
import com.baidu.mobad.feeds.ArticleInfo

/**
 * 备注：信息流广告（平台模板）
 */
object TogetherAdExpressFlow : AdBase() {

    private var timer: Timer? = null
    private var overTimerTask: OverTimerTask? = null
    @Volatile
    private var stop = false

    @JvmStatic
    fun getAdList(@NonNull activity: Activity, listConfigStr: String?, @NonNull adConstStr: String, @NonNull count: Int, @NonNull adListener: AdListenerList) {
        stop = false
        startTimerTask(activity, adListener)

        when (AdRandomUtil.getRandomAdName(listConfigStr)) {
            AdNameType.BAIDU -> {
                getAdListBaiduMob(activity, listConfigStr, adConstStr, count, adListener)
            }
            AdNameType.GDT -> {
                getAdListTecentGDT(activity, listConfigStr, adConstStr, count, adListener)
            }
            AdNameType.CSJ -> {
                getAdListCsj(activity, listConfigStr, adConstStr, count, adListener)
            }
            else -> {
                if (stop) {
                    return
                }
                cancelTimerTask()

                activity.runOnUiThread {
                    adListener.onAdFailed(activity.getString(R.string.all_ad_error))
                }
                loge(activity.getString(R.string.all_ad_error))
            }
        }
    }

    private fun getAdListBaiduMob(@NonNull activity: Activity, listConfigStr: String?, @NonNull adConstStr: String, @NonNull count: Int, @NonNull adListener: AdListenerList) {
        adListener.onStartRequest(AdNameType.BAIDU.type)

        val requestParameters = RequestParameters.Builder()
                .downloadAppConfirmPolicy(RequestParameters.DOWNLOAD_APP_CONFIRM_ONLY_MOBILE)
                .build()
        val mBaiduNativeManager = BaiduNativeManager(activity, TogetherAd.idMapBaidu[adConstStr])
        mBaiduNativeManager.loadFeedAd(requestParameters, object :BaiduNativeManager.FeedAdListener{
            override fun onNativeLoad(adList: MutableList<NativeResponse>?) {
                if (stop) {
                    return
                }
                if (adList.isNullOrEmpty()) {
                    loge("${AdNameType.BAIDU.type}: 返回的广告是空的")
                    val newListConfig = listConfigStr?.replace(AdNameType.BAIDU.type, AdNameType.NO.type)
                    getAdList(activity, newListConfig, adConstStr, count, adListener)
                    return
                }
                cancelTimerTask()

                adListener.onAdLoaded(AdNameType.BAIDU.type, adList)
                logd("${AdNameType.BAIDU.type}: list.size: " + adList.size)
            }

            override fun onNativeFail(nativeErrorCode: NativeErrorCode?) {
                if (stop) {
                    return
                }
                cancelTimerTask()

                val newListConfig = listConfigStr?.replace(AdNameType.BAIDU.type, AdNameType.NO.type)
                getAdList(activity, newListConfig, adConstStr, count, adListener)
                loge("${AdNameType.BAIDU.type}: nativeErrorCode: $nativeErrorCode")
            }

            override fun onLpClosed() {
            }

            override fun onVideoDownloadSuccess() {
            }

            override fun onVideoDownloadFailed() {
            }
        })
    }

    private fun getAdListCsj(@NonNull activity: Activity, listConfigStr: String?, @NonNull adConstStr: String, @NonNull count: Int, @NonNull adListener: AdListenerList) {
        adListener.onStartRequest(AdNameType.CSJ.type)

        val dm = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(dm)
        val adSlot = AdSlot.Builder()
                .setCodeId(TogetherAd.idMapCsj[adConstStr])
                .setSupportDeepLink(true)
                .setImageAcceptedSize(dm.widthPixels, (dm.widthPixels * 9 / 16))
                .setAdCount(3)
                .build()
        TTAdSdk.getAdManager().createAdNative(activity).loadNativeExpressAd(adSlot, object : TTAdNative.NativeExpressAdListener {
            override fun onNativeExpressAdLoad(adList: MutableList<TTNativeExpressAd>?) {
                if (stop) {
                    return
                }
                if (adList.isNullOrEmpty()) {
                    loge("${AdNameType.CSJ.type}: 返回的广告是空的")
                    val newListConfig = listConfigStr?.replace(AdNameType.CSJ.type, AdNameType.NO.type)
                    getAdList(activity, newListConfig, adConstStr, count, adListener)
                    return
                }

                cancelTimerTask()

                adListener.onAdLoaded(AdNameType.CSJ.type, adList)
                logd("${AdNameType.CSJ.type}: list.size: " + adList.size)
            }

            override fun onError(errorCode: Int, errorMsg: String?) {
                if (stop) {
                    return
                }
                cancelTimerTask()

                loge("${AdNameType.CSJ.type}: errorCode: $errorCode, errorMsg: $errorMsg")
                val newListConfig = listConfigStr?.replace(AdNameType.CSJ.type, AdNameType.NO.type)
                getAdList(activity, newListConfig, adConstStr, count, adListener)
            }
        })
    }

    private fun getAdListTecentGDT(@NonNull activity: Activity, listConfigStr: String?, @NonNull adConstStr: String, @NonNull count: Int, @NonNull adListener: AdListenerList) {
        adListener.onStartRequest(AdNameType.GDT.type)
        val listener = object : NativeExpressAD.NativeExpressADListener {
            override fun onADCloseOverlay(p0: NativeExpressADView?) {
                //广告关闭遮盖时调用
            }

            /**
             * 4.使用完了每一个 NativeExpressADView 之后都要释放掉资源
             */
            override fun onADLoaded(adViewList: MutableList<NativeExpressADView>?) {
                if (stop) {
                    return
                }
                cancelTimerTask()

                //list是空的，按照错误来处理
                if (adViewList?.isEmpty() != false) {
                    loge("${AdNameType.GDT.type}: 请求成功，但是返回的list为空")
                    val newListConfig = listConfigStr?.replace(AdNameType.GDT.type, AdNameType.NO.type)
                    activity.runOnUiThread {
                        getAdList(activity, newListConfig, adConstStr, count, adListener)
                    }
                    return
                }

                logd("${AdNameType.GDT.type}: list.size: " + adViewList.size)
                activity.runOnUiThread {
                    adListener.onAdLoaded(AdNameType.GDT.type, adViewList)
                }
            }

            override fun onADOpenOverlay(p0: NativeExpressADView?) {
                //广告展开遮盖时调用
            }

            override fun onRenderFail(p0: NativeExpressADView?) {
                //NativeExpressADView 渲染广告失败
                if (stop) {
                    return
                }
                cancelTimerTask()

                val newListConfig = listConfigStr?.replace(AdNameType.GDT.type, AdNameType.NO.type)
                getAdList(activity, newListConfig, adConstStr, count, adListener)
            }

            override fun onADExposure(p0: NativeExpressADView?) {
                //广告曝光
            }

            override fun onADClosed(p0: NativeExpressADView?) {
                //广告被关闭，将不再显示广告，此时广告对象已经释放资源，不可以再次用来展示了
            }

            override fun onADLeftApplication(p0: NativeExpressADView?) {
                //因为广告点击等原因离开当前 app 时调用
            }

            override fun onNoAD(adError: AdError?) {
                if (stop) {
                    return
                }
                cancelTimerTask()

                loge("${AdNameType.GDT.type}: ${adError?.errorCode}, ${adError?.errorMsg}")
                val newListConfig = listConfigStr?.replace(AdNameType.GDT.type, AdNameType.NO.type)
                getAdList(activity, newListConfig, adConstStr, count, adListener)
            }

            override fun onADClicked(p0: NativeExpressADView?) {
                //广告点击
            }

            override fun onRenderSuccess(p0: NativeExpressADView?) {
                //NativeExpressADView 渲染广告成功
            }
        }

        val dm = DisplayMetrics()
        val mAdManager = NativeExpressAD(activity, ADSize(dm.widthPixels, AUTO_HEIGHT), TogetherAd.idMapGDT[adConstStr], listener)
        //有效值就是 5-60
        mAdManager.setMaxVideoDuration(60)
        mAdManager.setMinVideoDuration(5)
        mAdManager.setVideoPlayPolicy(VideoOption.VideoPlayPolicy.AUTO) // 本次拉回的视频广告，在用户看来是否为自动播放的
        mAdManager.loadAD(count)
    }

    interface AdListenerList {

        fun onAdFailed(failedMsg: String?)

        fun onAdLoaded(channel: String, adList: List<*>)

        fun onStartRequest(channel: String)
    }

    /**
     * 取消超时任务
     */
    private fun cancelTimerTask() {
        stop = false
        timer?.cancel()
        overTimerTask?.cancel()
    }

    /**
     * 开始超时任务
     */
    private fun startTimerTask(activity: Activity, listener: AdListenerList) {
        cancelTimerTask()
        timer = Timer()
        overTimerTask = OverTimerTask(activity, listener)
        timer?.schedule(overTimerTask, TogetherAd.timeOutMillis)
    }

    /**
     * 超时任务
     */
    private class OverTimerTask(activity: Activity, listener: AdListenerList) : TimerTask() {

        private val weakReference: WeakReference<AdListenerList>?
        private val weakRefContext: WeakReference<Activity>?

        init {
            weakReference = WeakReference(listener)
            weakRefContext = WeakReference(activity)
        }

        override fun run() {
            stop = true
            weakRefContext?.get()?.runOnUiThread {
                weakReference?.get()?.onAdFailed(weakRefContext.get()?.getString(R.string.timeout))
                loge(weakRefContext.get()?.getString(R.string.timeout))
                timer = null
                overTimerTask = null
            }
        }
    }
}