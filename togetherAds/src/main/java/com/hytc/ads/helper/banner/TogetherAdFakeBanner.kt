package com.hytc.ads.helper.banner

import android.app.Activity
import android.util.DisplayMetrics
import android.view.View
import androidx.annotation.NonNull
import com.baidu.mobad.feeds.BaiduNative
import com.baidu.mobad.feeds.NativeErrorCode
import com.baidu.mobad.feeds.NativeResponse
import com.baidu.mobad.feeds.RequestParameters
import com.bytedance.sdk.openadsdk.*
import com.hytc.ads.R
import com.hytc.ads.TogetherAd
import com.hytc.ads.helper.AdBase
import com.hytc.ads.other.AdNameType
import com.hytc.ads.other.AdRandomUtil
import com.hytc.ads.other.logd
import com.hytc.ads.other.loge
import com.qq.e.ads.cfg.VideoOption
import com.qq.e.ads.nativ.NativeADUnifiedListener
import com.qq.e.ads.nativ.NativeUnifiedAD
import com.qq.e.ads.nativ.NativeUnifiedADData
import com.qq.e.comm.util.AdError
import java.lang.ref.WeakReference
import java.util.*

/**
 * 备注：伪banner（csj用信息流模板，gdt和baidu用自渲染模板）
 */
object TogetherAdFakeBanner : AdBase() {

    private var timer: Timer? = null
    private var overTimerTask: OverTimerTask? = null
    @Volatile
    private var stop = false

    @JvmStatic
    fun getMixAd(@NonNull activity: Activity, listConfigStr: String?, @NonNull adConstStr: String, @NonNull count: Int, @NonNull height: Float, @NonNull adListener: AdListenerList) {
        stop = false
        startTimerTask(activity, adListener)

        when (AdRandomUtil.getRandomAdName(listConfigStr)) {
            AdNameType.BAIDU -> {
                getAdListBaiduMob(activity, listConfigStr, adConstStr, count, height, adListener)
            }
            AdNameType.GDT -> {
                getAdListTecentGDT(activity, listConfigStr, adConstStr, count, height, adListener)
            }
            AdNameType.CSJ -> {
                getAdListCsj(activity, listConfigStr, adConstStr, count, height, adListener)
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

    /**
     * baidu自渲染信息流
     */
    private fun getAdListBaiduMob(@NonNull activity: Activity, listConfigStr: String?, @NonNull adConstStr: String, @NonNull count: Int, @NonNull height: Float, @NonNull adListener: AdListenerList) {
        adListener.onStartRequest(AdNameType.BAIDU.type)
        val baidu = BaiduNative(activity, TogetherAd.idMapBaidu[adConstStr], object : BaiduNative.BaiduNativeNetworkListener {

            override fun onNativeLoad(list: List<NativeResponse>) {
                if (stop) {
                    return
                }
                cancelTimerTask()

                activity.runOnUiThread {
                    adListener.onAdLoaded(AdNameType.BAIDU.type, list)
                }
                logd("${AdNameType.BAIDU.type}: list.size: " + list.size)
            }

            override fun onNativeFail(nativeErrorCode: NativeErrorCode) {
                if (stop) {
                    return
                }
                cancelTimerTask()

                val newListConfig = listConfigStr?.replace(AdNameType.BAIDU.type, AdNameType.NO.type)
                getMixAd(activity, newListConfig, adConstStr, count, height, adListener)
                loge("${AdNameType.BAIDU.type}: nativeErrorCode: $nativeErrorCode")
            }
        })
        /*
         * Step 2. 创建requestParameters对象，并将其传给baidu.makeRequest来请求广告
         */
        // 用户点击下载类广告时，是否弹出提示框让用户选择下载与否
        val requestParameters = RequestParameters.Builder().build()

        baidu.makeRequest(requestParameters)
    }

    /**
     * csj模板
     */
    private fun getAdListCsj(@NonNull activity: Activity, listConfigStr: String?, @NonNull adConstStr: String, @NonNull count: Int, @NonNull height: Float, @NonNull adListener: AdListenerList) {
        adListener.onStartRequest(AdNameType.CSJ.type)

        val dm = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(dm)
        //图片以16：9的宽高比展示
        //无论是横屏还是竖屏都是取小的那个长度的80%
        val n = ((if (dm.widthPixels > dm.heightPixels) dm.heightPixels else dm.widthPixels) * 0.8).toInt()

        val adSlot = AdSlot.Builder()
                .setCodeId(TogetherAd.idMapCsj[adConstStr])
                .setSupportDeepLink(true)
                .setAdCount(count) //请求广告数量为1到3条
                .setExpressViewAcceptedSize(n.toFloat(), height)
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
                    val newConfigStr = listConfigStr?.replace(AdNameType.CSJ.type, AdNameType.NO.type)
                    getMixAd(activity, newConfigStr, adConstStr, count, height, adListener)
                    return
                }

                cancelTimerTask()

                logd("${AdNameType.CSJ.type}: ${activity.getString(R.string.prepared)}")

                val expressAd = adList[0]
                expressAd.setExpressInteractionListener(object : TTNativeExpressAd.ExpressAdInteractionListener {
                    override fun onAdClicked(view: View, type: Int) {
                    }

                    override fun onAdShow(view: View, type: Int) {
                    }

                    override fun onRenderFail(view: View, msg: String, code: Int) {
                        if (stop) {
                            return
                        }
                        cancelTimerTask()

                        val newConfigStr = listConfigStr?.replace(AdNameType.CSJ.type, AdNameType.NO.type)
                        getMixAd(activity, newConfigStr, adConstStr, count, height, adListener)
                    }

                    override fun onRenderSuccess(view: View, width: Float, height: Float) {
                        adListener.onCsjRenderSuccess(view, width, height)

                        //使用默认模板中默认dislike弹出样式
                        expressAd.setDislikeCallback(activity, object : TTAdDislike.DislikeInteractionCallback {
                            override fun onSelected(position: Int, value: String) {
                                //用户选择不喜欢原因后，移除广告展示
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

                val newConfigStr = listConfigStr?.replace(AdNameType.CSJ.type, AdNameType.NO.type)
                getMixAd(activity, newConfigStr, adConstStr, count, height, adListener)
            }
        })
    }

    /**
     * gdt自渲染信息流
     */
    private fun getAdListTecentGDT(@NonNull activity: Activity, listConfigStr: String?, @NonNull adConstStr: String, @NonNull count: Int, @NonNull height: Float, @NonNull adListener: AdListenerList) {
        adListener.onStartRequest(AdNameType.GDT.type)

        val listener = object : NativeADUnifiedListener {
            override fun onADLoaded(adList: List<NativeUnifiedADData>?) {
                if (stop) {
                    return
                }
                cancelTimerTask()

                //list是空的，按照错误来处理
                if (adList?.isEmpty() != false) {
                    loge("${AdNameType.GDT.type}: 请求成功，但是返回的list为空")
                    val newListConfig = listConfigStr?.replace(AdNameType.GDT.type, AdNameType.NO.type)
                    activity.runOnUiThread {
                        getMixAd(activity, newListConfig, adConstStr, count, height, adListener)
                    }
                    return
                }

                logd("${AdNameType.GDT.type}: list.size: " + adList.size)
                activity.runOnUiThread {
                    adListener.onAdLoaded(AdNameType.GDT.type, adList)
                }
            }

            override fun onNoAD(adError: AdError?) {
                if (stop) {
                    return
                }
                cancelTimerTask()

                loge("${AdNameType.GDT.type}: ${adError?.errorCode}, ${adError?.errorMsg}")
                val newListConfig = listConfigStr?.replace(AdNameType.GDT.type, AdNameType.NO.type)
                getMixAd(activity, newListConfig, adConstStr, count, height, adListener)
            }
        }

        val mAdManager = NativeUnifiedAD(activity, TogetherAd.idMapGDT[adConstStr], listener)
        //有效值就是 5-60
        mAdManager.setMaxVideoDuration(60)
        mAdManager.setMinVideoDuration(5)
        mAdManager.setVideoPlayPolicy(VideoOption.VideoPlayPolicy.AUTO) // 本次拉回的视频广告，在用户看来是否为自动播放的
        mAdManager.setVideoADContainerRender(VideoOption.VideoADContainerRender.SDK) // 视频播放前，用户看到的广告容器是由SDK渲染的
        mAdManager.loadData(count)
    }

    interface AdListenerList {

        fun onAdFailed(failedMsg: String?)

        fun onAdLoaded(channel: String, adList: List<*>)

        fun onStartRequest(channel: String)

        fun onCsjRenderSuccess(view: View, width: Float, height: Float)
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