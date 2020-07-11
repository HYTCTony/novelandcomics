package com.hytc.ads.helper.mid

import android.app.Activity
import android.util.DisplayMetrics
import android.view.View
import androidx.annotation.NonNull
import com.baidu.mobad.feeds.*
import com.baidu.mobads.component.FeedNativeView
import com.bytedance.sdk.openadsdk.*
import com.hytc.ads.R
import com.hytc.ads.TogetherAd
import com.hytc.ads.helper.AdBase
import com.hytc.ads.other.AdNameType
import com.hytc.ads.other.AdRandomUtil
import com.hytc.ads.other.logd
import com.hytc.ads.other.loge
import com.qq.e.ads.cfg.VideoOption
import com.qq.e.ads.nativ.ADSize
import com.qq.e.ads.nativ.NativeExpressAD
import com.qq.e.ads.nativ.NativeExpressADView
import com.qq.e.comm.util.AdError
import java.lang.ref.WeakReference
import java.util.*


/**
 * 备注：用于界面中间插一个广告(返回View)模板
 */
object TogetherAdMidExpress : AdBase() {

    private var timer: Timer? = null
    private var overTimerTask: OverTimerTask? = null
    @Volatile
    private var stop = false

    @JvmStatic
    fun showAdMid(@NonNull activity: Activity, midConfigStr: String?, @NonNull adConstStr: String, @NonNull adListener: AdListenerMid) {
        stop = false
        startTimerTask(activity, adListener)

        when (AdRandomUtil.getRandomAdName(midConfigStr)) {
            AdNameType.BAIDU -> {
                showAdMidBaiduMob(activity, midConfigStr, adConstStr, adListener)
            }
            AdNameType.GDT -> {
                showAdMidTecentGDT(activity, midConfigStr, adConstStr, adListener)
            }
            AdNameType.CSJ -> {
                showAdMidCsj(activity, midConfigStr, adConstStr, adListener)
            }
            else -> {
                if (stop) {
                    return
                }
                cancelTimerTask()

                loge(activity.getString(R.string.all_ad_error))
                adListener.onAdFailed(activity.getString(R.string.all_ad_error))
            }
        }
    }

    private var nativeExpressADView: NativeExpressADView? = null
    private fun showAdMidTecentGDT(@NonNull activity: Activity, midConfigStr: String?, @NonNull adConstStr: String, @NonNull adListener: AdListenerMid) {
        adListener.onStartRequest(AdNameType.GDT.type)
        val listener2 = object : NativeExpressAD.NativeExpressADListener {
            override fun onADCloseOverlay(ad: NativeExpressADView?) {
                //To change body of created functions use File | Settings | File Templates.
            }

            override fun onADLoaded(adViewList: MutableList<NativeExpressADView>?) {
                if (stop) {
                    return
                }
                cancelTimerTask()

                if (adViewList?.isEmpty() != false) {
                    loge("${AdNameType.GDT.type}: 广点通信息流伪装MID返回空的")
                    val newConfigStr = midConfigStr?.replace(AdNameType.GDT.type, AdNameType.NO.type)
                    showAdMid(activity, newConfigStr, adConstStr, adListener)
                    return
                }

                if (nativeExpressADView != null) {
                    nativeExpressADView!!.destroy()
                }
                nativeExpressADView = adViewList[0]
                /* // 3.返回数据后，SDK 会返回可以用于展示 NativeExpressADView 列表
                 if (nativeExpressADView!!.boundData.adPatternType == AdPatternType.NATIVE_VIDEO) {
                     nativeExpressADView!!.setMediaListener(mediaListener)
                 }*/
                nativeExpressADView!!.render()

                adListener.onAdPrepared(AdNameType.GDT.type)
            }

            override fun onADOpenOverlay(adView: NativeExpressADView?) {
            }

            override fun onRenderFail(ad: NativeExpressADView?) {
                if (stop) {
                    return
                }
                cancelTimerTask()

                val newConfigStr = midConfigStr?.replace(AdNameType.GDT.type, AdNameType.NO.type)
                showAdMid(activity, newConfigStr, adConstStr, adListener)
            }

            override fun onADExposure(adView: NativeExpressADView?) {
            }

            override fun onADClosed(adView: NativeExpressADView?) {
                adListener.onDisLike(AdNameType.GDT.type, 0, "")
            }

            override fun onADLeftApplication(adView: NativeExpressADView?) {
            }

            override fun onNoAD(error: AdError?) {
                if (stop) {
                    return
                }
                cancelTimerTask()

                val newConfigStr = midConfigStr?.replace(AdNameType.GDT.type, AdNameType.NO.type)
                showAdMid(activity, newConfigStr, adConstStr, adListener)
            }

            override fun onADClicked(adView: NativeExpressADView?) {
                adListener.onAdClick(AdNameType.GDT.type)
            }

            override fun onRenderSuccess(adView: NativeExpressADView?) {
                if (adView != null) {
                    adListener.onRenderSuccess(AdNameType.GDT.type, adView, adView.width.toFloat(), adView.height.toFloat())
                } else {
                    if (stop) {
                        return
                    }
                    cancelTimerTask()

                    val newConfigStr = midConfigStr?.replace(AdNameType.GDT.type, AdNameType.NO.type)
                    showAdMid(activity, newConfigStr, adConstStr, adListener)
                }
            }
        }

        val dm = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(dm)
        //图片以16：9的宽高比展示
        //无论是横屏还是竖屏都是取小的那个长度的80%
        val n = ((if (dm.widthPixels > dm.heightPixels) dm.heightPixels else dm.widthPixels) * 0.8).toInt()
        val nativeExpressAD = NativeExpressAD(activity, ADSize(n, ADSize.AUTO_HEIGHT), TogetherAd.idMapGDT[adConstStr], listener2)
        nativeExpressAD.setVideoPlayPolicy(VideoOption.VideoPlayPolicy.AUTO) // 本次拉回的视频广告，从用户的角度看是自动播放的
        nativeExpressAD.loadAD(1)
    }

    private fun showAdMidBaiduMob(@NonNull activity: Activity, midConfigStr: String?, @NonNull adConstStr: String, @NonNull adListener: AdListenerMid) {
        adListener.onStartRequest(AdNameType.BAIDU.type)
        val requestParameters = RequestParameters.Builder()
                .downloadAppConfirmPolicy(RequestParameters.DOWNLOAD_APP_CONFIRM_ONLY_MOBILE)
                .build()
        val mBaiduNativeManager = BaiduNativeManager(activity, TogetherAd.idMapBaidu[adConstStr])
        mBaiduNativeManager.loadFeedAd(requestParameters, object : BaiduNativeManager.FeedAdListener {
            override fun onNativeLoad(nativeResponses: MutableList<NativeResponse>?) {
                if (stop) {
                    return
                }
                if (nativeResponses.isNullOrEmpty()) {
                    loge("${AdNameType.BAIDU.type}: 返回的广告是空的")
                    val newListConfig = midConfigStr?.replace(AdNameType.BAIDU.type, AdNameType.NO.type)
                    showAdMid(activity, newListConfig, adConstStr, adListener)
                    return
                }
                cancelTimerTask()

                adListener.onAdPrepared(AdNameType.BAIDU.type)

                val rep = nativeResponses[0]
                val mFeedNativeView = FeedNativeView(activity)
                mFeedNativeView.setAdData(rep as XAdNativeResponse?)
                adListener.onRenderSuccess(AdNameType.BAIDU.type, mFeedNativeView, mFeedNativeView.width.toFloat(), mFeedNativeView.height.toFloat())
            }

            override fun onNativeFail(errorCode: NativeErrorCode?) {
                if (stop) {
                    return
                }
                cancelTimerTask()

                val newConfigStr = midConfigStr?.replace(AdNameType.BAIDU.type, AdNameType.NO.type)
                showAdMid(activity, newConfigStr, adConstStr, adListener)
            }

            override fun onLpClosed() {
                adListener.onDisLike(AdNameType.BAIDU.type, 0, "")
            }

            override fun onVideoDownloadSuccess() {
            }

            override fun onVideoDownloadFailed() {
            }
        })
    }

    private fun showAdMidCsj(@NonNull activity: Activity, midConfigStr: String?, @NonNull adConstStr: String, @NonNull adListener: AdListenerMid) {
        adListener.onStartRequest(AdNameType.CSJ.type)

        val dm = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(dm)
        //图片以16：9的宽高比展示
        //无论是横屏还是竖屏都是取小的那个长度的80%
        val n = ((if (dm.widthPixels > dm.heightPixels) dm.heightPixels else dm.widthPixels) * 0.8).toInt()

        val adSlot = AdSlot.Builder()
                .setCodeId(TogetherAd.idMapCsj[adConstStr])
                .setSupportDeepLink(true)
                .setAdCount(1) //请求广告数量为1到3条
                .setExpressViewAcceptedSize(n.toFloat(), 0F)
                .setImageAcceptedSize(n, n * 9 / 16)
                .setNativeAdType(AdSlot.TYPE_INTERACTION_AD)//请求原生广告时候，请务必调用该方法，设置参数为TYPE_BANNER或TYPE_INTERACTION_AD
                .build()
        TTAdSdk.getAdManager().createAdNative(activity).loadNativeExpressAd(adSlot, object : TTAdNative.NativeExpressAdListener {
            override fun onNativeExpressAdLoad(adList: MutableList<TTNativeExpressAd>?) {
                if (stop) {
                    return
                }
                if (adList.isNullOrEmpty()) {
                    loge("${AdNameType.CSJ.type}: 穿山甲返回的广告是 null")
                    val newConfigStr = midConfigStr?.replace(AdNameType.CSJ.type, AdNameType.NO.type)
                    showAdMid(activity, newConfigStr, adConstStr, adListener)
                    return
                }

                cancelTimerTask()

                logd("${AdNameType.CSJ.type}: ${activity.getString(R.string.prepared)}")
                adListener.onAdPrepared(AdNameType.CSJ.type)

                val expressAd = adList[0]

                expressAd.setExpressInteractionListener(object : TTNativeExpressAd.ExpressAdInteractionListener {
                    override fun onAdClicked(view: View, type: Int) {
                        adListener.onAdClick(AdNameType.CSJ.type)
                    }

                    override fun onAdShow(view: View, type: Int) {
                    }

                    override fun onRenderFail(view: View, msg: String, code: Int) {
                        if (stop) {
                            return
                        }
                        cancelTimerTask()

                        val newConfigStr = midConfigStr?.replace(AdNameType.CSJ.type, AdNameType.NO.type)
                        showAdMid(activity, newConfigStr, adConstStr, adListener)
                    }

                    override fun onRenderSuccess(view: View, width: Float, height: Float) {
                        adListener.onRenderSuccess(AdNameType.CSJ.type, view, width, height)

                        //使用默认模板中默认dislike弹出样式
                        expressAd.setDislikeCallback(activity, object : TTAdDislike.DislikeInteractionCallback {
                            override fun onSelected(position: Int, value: String) {
                                //用户选择不喜欢原因后，移除广告展示
                                adListener.onDisLike(AdNameType.CSJ.type, position, value)
                            }

                            override fun onCancel() {
                                //Log.e("ExpressView", "点击取消 ");
                            }
                        })
                    }
                })
                //渲染广告
                expressAd.render()
            }

            override fun onError(errorCode: Int, errorMsg: String?) {
                loge("${AdNameType.CSJ.type}: $errorCode : $errorMsg")
                if (stop) {
                    return
                }
                cancelTimerTask()

                val newConfigStr = midConfigStr?.replace(AdNameType.CSJ.type, AdNameType.NO.type)
                showAdMid(activity, newConfigStr, adConstStr, adListener)
            }
        })
    }

    fun destroy() {
        stop = true
    }

    interface AdListenerMid {

        fun onStartRequest(channel: String)

        fun onAdClick(channel: String)

        fun onAdFailed(failedMsg: String?)

        fun onAdPrepared(channel: String)

        fun onRenderSuccess(channel: String, view: View, width: Float, height: Float)

        fun onDisLike(channel: String, position: Int, value: String)
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
    private fun startTimerTask(activity: Activity, listener: AdListenerMid) {
        cancelTimerTask()
        timer = Timer()
        overTimerTask = OverTimerTask(activity, listener)
        timer?.schedule(overTimerTask, TogetherAd.timeOutMillis)
    }

    /**
     * 超时任务
     */
    private class OverTimerTask(activity: Activity, listener: AdListenerMid) : TimerTask() {

        private val weakReference: WeakReference<AdListenerMid>?
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