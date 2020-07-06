package com.hytc.ads.helper.stimulatevideo

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.util.Log
import android.view.WindowManager
import androidx.annotation.NonNull
import com.baidu.mobads.rewardvideo.RewardVideoAd
import com.bytedance.sdk.openadsdk.*
import com.hytc.ads.R
import com.hytc.ads.TogetherAd
import com.hytc.ads.helper.AdBase
import com.hytc.ads.helper.flow.TogetherAdFlow
import com.hytc.ads.other.AdNameType
import com.hytc.ads.other.AdRandomUtil
import com.hytc.ads.other.logd
import com.hytc.ads.other.loge
import com.qq.e.ads.rewardvideo.RewardVideoAD
import com.qq.e.ads.rewardvideo.RewardVideoADListener
import com.qq.e.comm.util.AdError
import java.lang.ref.WeakReference
import java.util.*

/**
 * 备注：激励视频
 */
object TogetherAdStimulate : AdBase() {

    private var timer: Timer? = null
    private var overTimerTask: OverTimerTask? = null
    @Volatile
    private var stop = false

    /*百度*/
    var mRewardVideoAd: RewardVideoAd? = null
    /*穿山甲*/
    var mttRewardVideoAd: TTRewardVideoAd? = null
    /*广点通*/
    var gdtRewardVideoAD: RewardVideoAD? = null
    var adLoaded: Boolean = false       //广点通广告是否加载

    /**
     * 显示激励视频
     *
     * @param splashConfigStr "baidu:2,gdt:8"
     * @param adListener      监听
     */
    @JvmStatic
    fun showAdFull(@NonNull activity: Activity, @NonNull token: String, splashConfigStr: String?, @NonNull adConstStr: String, @NonNull adListener: AdListenerSplashFull) {
        when (AdRandomUtil.getRandomAdName(splashConfigStr)) {
            AdNameType.BAIDU -> {
                showAdFullBaiduMob(activity, token, splashConfigStr, adConstStr, adListener)
            }
            AdNameType.GDT -> {
                showAdFullGDT(activity, token, splashConfigStr, adConstStr, adListener)
            }
            AdNameType.CSJ -> {
                showAdFullCsj(activity, token, splashConfigStr, adConstStr, adListener)
            }
            else -> {
                if (stop) {
                    return
                }
                cancelTimerTask()

                adListener.onAdFailed(activity.getString(R.string.all_ad_error))
                loge(activity.getString(R.string.all_ad_error))
            }
        }
    }

    /**
     * 腾讯广点通
     */
    private fun showAdFullGDT(@NonNull activity: Activity, @NonNull token: String, splashConfigStr: String?, @NonNull adConstStr: String, @NonNull adListener: AdListenerSplashFull) {
        adListener.onStartRequest(AdNameType.GDT.type)
        gdtRewardVideoAD = RewardVideoAD(activity, TogetherAd.idMapGDT[adConstStr], object : RewardVideoADListener {
            /**
             * 激励视频广告曝光
             */
            override fun onADExpose() {
            }

            /**
             * 激励视频广告被点击
             */
            override fun onADClick() {
                adListener.onADClick(AdNameType.GDT.type)
            }

            /**
             * 视频素材缓存成功，可在此回调后进行广告展示
             */
            override fun onVideoCached() {
                adListener.onAdPrepared(AdNameType.GDT.type)
                if (adLoaded) {//广告展示检查1：广告成功加载，此处也可以使用videoCached来实现视频预加载完成后再展示激励视频广告的逻辑
                    gdtRewardVideoAD?.showAD()
                }
            }

            /**
             * 激励视频触发激励（观看视频大于一定时长或者视频播放完毕）
             */
            override fun onReward() {
                adListener.onAdRewardVerify(true)
            }

            /**
             * 激励视频界面关闭
             */
            override fun onADClose() {
                gdtRewardVideoAD = null
                adListener.onAdDismissed()
            }

            /**
             * 广告加载成功，可在此回调后进行广告展示
             **/
            override fun onADLoad() {
                if (stop) {
                    return
                }
                cancelTimerTask()
                adLoaded = true
            }

            /**
             * 激励视频播放完毕
             */
            override fun onVideoComplete() {
            }

            /**
             * 广告流程出错
             */
            override fun onError(adError: AdError) {
                if (stop) {
                    return
                }
                cancelTimerTask()

                val msg = String.format(Locale.getDefault(), "onError, error code: %d, error msg: %s",
                        adError.errorCode, adError.errorMsg)
                loge("${AdNameType.GDT.type}: $msg")
                val newSplashConfigStr = splashConfigStr?.replace(AdNameType.GDT.type, AdNameType.NO.type)
                showAdFull(activity, token, newSplashConfigStr, adConstStr, adListener)
            }

            /**
             * 激励视频广告页面展示
             */
            override fun onADShow() {
            }
        })// 有声播放
        gdtRewardVideoAD?.loadAD()  //开始加载广告
    }

    /**
     * 百度Mob
     */
    private fun showAdFullBaiduMob(@NonNull activity: Activity, @NonNull token: String, splashConfigStr: String?, @NonNull adConstStr: String, @NonNull adListener: AdListenerSplashFull) {
        adListener.onStartRequest(AdNameType.BAIDU.type)

        mRewardVideoAd = RewardVideoAd(activity, TogetherAd.idMapBaidu[adConstStr], object : RewardVideoAd.RewardVideoAdListener {
            override fun onAdFailed(s: String?) {
                if (stop) {
                    return
                }
                cancelTimerTask()

                loge("${AdNameType.BAIDU.type}: $s")
                val newConfig = splashConfigStr?.replace(AdNameType.BAIDU.type, AdNameType.NO.type)
                showAdFull(activity, token, newConfig, adConstStr, adListener)
            }

            override fun playCompletion() {
                adListener.onAdRewardVerify(true)
            }

            override fun onAdShow() {
            }

            override fun onAdClick() {
                adListener.onADClick(AdNameType.BAIDU.type)
            }

            override fun onAdClose(p0: Float) {
                logd("${AdNameType.BAIDU.type}: ${activity.getString(R.string.dismiss)}")
                adListener.onAdDismissed()
            }

            override fun onVideoDownloadSuccess() {
                if (stop) {
                    return
                }
                cancelTimerTask()

                adListener.onAdPrepared(AdNameType.BAIDU.type)
                logd("${AdNameType.BAIDU.type}: ${activity.getString(R.string.prepared)}")

                val isReady = mRewardVideoAd != null && mRewardVideoAd!!.isReady
                if (isReady) {
                    mRewardVideoAd?.show()
                } else {
                    val newConfig = splashConfigStr?.replace(AdNameType.BAIDU.type, AdNameType.NO.type)
                    showAdFull(activity, token, newConfig, adConstStr, adListener)
                }
            }

            override fun onVideoDownloadFailed() {
            }
        }, true)
        mRewardVideoAd?.load()
    }


    /**
     * 穿山甲
     */
    private fun showAdFullCsj(@NonNull activity: Activity, @NonNull token: String, splashConfigStr: String?, @NonNull adConstStr: String, @NonNull adListener: AdListenerSplashFull) {
        adListener.onStartRequest(AdNameType.CSJ.type)
        val wm = activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val point = Point()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.defaultDisplay.getRealSize(point)
        } else {
            wm.defaultDisplay.getSize(point)
        }
        val adSlot = AdSlot.Builder()
                .setCodeId(TogetherAd.idMapCsj[adConstStr])
                .setSupportDeepLink(true)
                //.setRewardName("金币") //奖励的名称
                //.setRewardAmount(3)  //奖励的数量
                .setUserID(token)//用户id,必传参数
                .setMediaExtra("media_extra") //附加参数，可选
                .setOrientation(TTAdConstant.VERTICAL) //必填参数，期望视频的播放方向：TTAdConstant.HORIZONTAL 或 TTAdConstant.VERTICAL
                .build()
        TTAdSdk.getAdManager().createAdNative(activity).loadRewardVideoAd(adSlot, object : TTAdNative.RewardVideoAdListener {
            override fun onRewardVideoAdLoad(ad: TTRewardVideoAd?) {
                if (stop) {
                    return
                }
                cancelTimerTask()

                adListener.onAdPrepared(AdNameType.CSJ.type)
                mttRewardVideoAd = ad
                mttRewardVideoAd?.setRewardAdInteractionListener(object : TTRewardVideoAd.RewardAdInteractionListener {
                    override fun onAdShow() {}
                    override fun onAdVideoBarClick() {
                        adListener.onADClick(AdNameType.CSJ.type)
                    }

                    override fun onAdClose() {
                        adListener.onAdDismissed()
                    }

                    //视频播放完成回调
                    override fun onVideoComplete() {}

                    override fun onVideoError() {}
                    //视频播放完成后，奖励验证回调，rewardVerify：是否有效，rewardAmount：奖励梳理，rewardName：奖励名称
                    override fun onRewardVerify(rewardVerify: Boolean, rewardAmount: Int, rewardName: String) {
                        adListener.onAdRewardVerify(rewardVerify)
                    }

                    override fun onSkippedVideo() {}
                })
            }

            override fun onRewardVideoCached() {
                adListener.onAdPrepared(AdNameType.CSJ.type)
                if (mttRewardVideoAd != null) {
                    //step6:在获取到广告后展示,强烈建议在onRewardVideoCached回调后，展示广告，提升播放体验
                    //该方法直接展示广告
                    //mttRewardVideoAd.showRewardVideoAd(RewardVideoActivity.this);

                    //展示广告，并传入广告展示的场景
                    mttRewardVideoAd?.showRewardVideoAd(activity, TTAdConstant.RitScenes.CUSTOMIZE_SCENES, adConstStr)
                    mttRewardVideoAd = null
                } else {
                    //TToast.show(RewardVideoActivity.this, "请先加载广告");
                }
            }

            override fun onError(errorCode: Int, errorMsg: String?) {
                if (stop) {
                    return
                }
                cancelTimerTask()

                loge("${AdNameType.CSJ.type}: $errorCode : $errorMsg")
                val newSplashConfigStr = splashConfigStr?.replace(AdNameType.CSJ.type, AdNameType.NO.type)
                showAdFull(activity, token, newSplashConfigStr, adConstStr, adListener)
            }
        })
    }

    /**
     * 监听器
     */
    interface AdListenerSplashFull {
        fun onStartRequest(channel: String)

        fun onADClick(channel: String)

        fun onAdFailed(failedMsg: String?)

        fun onAdDismissed()

        fun onAdPrepared(channel: String)

        fun onAdRewardVerify(rewardVerify: Boolean)
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
    private fun startTimerTask(activity: Activity, listener: TogetherAdFlow.AdListenerList) {
        cancelTimerTask()
        timer = Timer()
        overTimerTask = OverTimerTask(activity, listener)
        timer?.schedule(overTimerTask, TogetherAd.timeOutMillis)
    }

    /**
     * 超时任务
     */
    private class OverTimerTask(activity: Activity, listener: TogetherAdFlow.AdListenerList) : TimerTask() {

        private val weakReference: WeakReference<TogetherAdFlow.AdListenerList>?
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