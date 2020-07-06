package com.hytc.ads

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import com.baidu.mobad.feeds.NativeResponse
import com.bumptech.glide.Glide
import com.hytc.ads.other.AdNameType

/**
 * 备注：这是一个展示广告 Logo 的 自定义View
 */
class AdLogoView(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {

    /**
     * 百度
     */
    private var mContainerLogoBaidu: ViewGroup? = null
    private var mIvLogoBaidu: ImageView
    private var mIvAdBaidu: ImageView

    /**
     * 穿山甲
     */
    private var mContainerLogoCsj: ViewGroup? = null
    private var mIvLogoCsj: ImageView? = null

  /**
     * 广点通
     */
    private var mContainerLogoGdt: ViewGroup? = null
    private var mIvLogoGdt: ImageView? = null


    init {
        val view = View.inflate(context, R.layout.view_ad_logo, this)
        mContainerLogoBaidu = view.findViewById(R.id.container_logo_baidu)
        mIvLogoBaidu = view.findViewById(R.id.img_logo_baidu)
        mIvAdBaidu = view.findViewById(R.id.img_ad)

        mContainerLogoCsj = view.findViewById(R.id.container_logo_csj)
        mIvLogoCsj = view.findViewById(R.id.img_logo_csj)

        mContainerLogoGdt = view.findViewById(R.id.container_logo_gdt)
        mIvLogoGdt = view.findViewById(R.id.img_logo_gdt)
    }


    /**
     * 设置广告的类型，就会展示相应的 Logo
     * adObject: 对应广告的对象，必须和类型一致，可传 null
     */
    fun setAdLogoType(adNameType: AdNameType, adObject: Any? = null) {

        setAdLogoVisibility(adNameType)

        when (adNameType) {
            AdNameType.BAIDU -> {
                if (adObject == null) {
                    return
                }
                adObject as NativeResponse
                Glide.with(context)
                        .load(adObject.baiduLogoUrl)
                        .into(mIvLogoBaidu)

                Glide.with(context)
                        .load(adObject.adLogoUrl)
                        .into(mIvAdBaidu)
            }
            AdNameType.CSJ -> {
                mIvLogoCsj?.setImageResource(R.drawable.ic_ad_logo_csj)
            }
            AdNameType.GDT -> {
                mIvLogoGdt?.setImageResource(R.drawable.ic_ad_logo_gdt)
            }
            AdNameType.NO -> {
            }
        }
    }

    /**
     * 设置可见性
     */
    private fun setAdLogoVisibility(adNameType: AdNameType) {
        when (adNameType) {
            AdNameType.BAIDU -> {
                mContainerLogoBaidu?.visibility = View.VISIBLE
                mContainerLogoCsj?.visibility = View.GONE
                mContainerLogoGdt?.visibility = View.GONE
            }
            AdNameType.CSJ -> {
                mContainerLogoBaidu?.visibility = View.GONE
                mContainerLogoCsj?.visibility = View.VISIBLE
                mContainerLogoGdt?.visibility = View.GONE
            }
            AdNameType.GDT -> {
                mContainerLogoBaidu?.visibility = View.GONE
                mContainerLogoCsj?.visibility = View.GONE
                mContainerLogoGdt?.visibility = View.VISIBLE
//                this.visibility = View.GONE
            }
            AdNameType.NO -> {
                this.visibility = View.GONE
            }
        }
    }
}