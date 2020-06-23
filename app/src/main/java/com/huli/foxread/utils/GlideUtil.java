package com.huli.foxread.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.huli.foxread.GlideApp;
import com.huli.foxread.R;

import androidx.annotation.DrawableRes;
import androidx.annotation.RawRes;

public class GlideUtil {

    /**
     * 加载圆形图片
     *
     * @param context
     * @param view
     * @param url
     */
    public static void loadCircle(Context context, ImageView view, String url) {
        GlideApp.with(context)
                .load(url)
                .placeholder(R.mipmap.holder_headimg)
                .error(R.mipmap.holder_headimg)
                .transform(new CircleCrop())
                .into(view);
    }

    /**
     * 加载圆形图片
     *
     * @param context
     * @param view
     * @param resId
     */
    public static void loadCircle(Context context, ImageView view, @RawRes @DrawableRes int resId) {
        GlideApp.with(context)
                .load(resId)
                .placeholder(R.mipmap.holder_headimg)
                .error(R.mipmap.holder_headimg)
                .transform(new CircleCrop())
                .into(view);
    }

    /**
     * 加载圆形图片
     *
     * @param context
     * @param view
     * @param drawable
     */
    public static void loadCircle(Context context, ImageView view, Drawable drawable) {
        GlideApp.with(context)
                .load(drawable)
                .placeholder(R.color.hl_read_bg_1)
                .error(R.color.hl_read_bg_1)
                .transform(new CircleCrop())
                .into(view);
    }

    /**
     * 加载圆角图片
     *
     * @param context 上下文
     * @param view    图片控件
     * @param url     网络图片地址
     */

    public static void loadRoundRect(Context context, ImageView view, String url) {
        loadRoundRect(context, view, url, DensityUtils.dp2px(context, 3));
    }


    /**
     * 加载圆角图片
     *
     * @param context 上下文
     * @param view    图片控件
     * @param url     网络图片地址
     * @param radius  圆角度数
     */

    public static void loadRoundRect(Context context, ImageView view, String url, int radius) {
        if (view == null) {
            return;
        }
        //设置图片圆角角度
        if (radius > 0) {
//            RequestOptions options = RequestOptions.bitmapTransform(new CenterCropRoundCornerTransform(radius));
//            RequestOptions transform = new RequestOptions().transform(new CenterCrop(), new RoundedCorners(radius));
            GlideApp.with(context)
                    .load(url)
//                    .apply(transform)
                    .transform(new CenterCrop(), new RoundedCorners(radius))
                    .placeholder(R.mipmap.img_holder_rect)
                    .error(R.mipmap.img_holder_rect)
                    .into(view);
        } else {
            GlideApp.with(context)
                    .load(url)
                    .placeholder(R.mipmap.img_holder_rect)
                    .error(R.mipmap.img_holder_rect)
                    .into(view);
        }
    }


    /**
     * 加载圆角图片
     *
     * @param context 上下文
     * @param view    图片控件
     * @param resId   资源图片
     */

    public static void loadRoundRect(Context context, ImageView view, @RawRes @DrawableRes int resId) {
        loadRoundRect(context, view, resId, DensityUtils.dp2px(context, 3));
    }


    /**
     * 加载圆角图片
     *
     * @param context 上下文
     * @param view    图片控件
     * @param resId   资源图片
     * @param radius  圆角度数
     */

    public static void loadRoundRect(Context context, ImageView view, @RawRes @DrawableRes int resId, int radius) {
        if (view == null) {
            return;
        }
//        RequestOptions options = RequestOptions.bitmapTransform(new CenterCropRoundCornerTransform(radius));
//        RequestOptions transform = new RequestOptions().transform(new CenterCrop(), new RoundedCorners(radius));
        GlideApp.with(context)
                .load(resId)
//                .apply(transform)
                .transform(new CenterCrop(), new RoundedCorners(radius))
//                .transition(DrawableTransitionOptions.withCrossFade(100))//淡入淡出
                .placeholder(R.mipmap.img_holder_rect)
                .error(R.mipmap.img_holder_rect)
                .into(view);
    }


    /**
     * 加载圆角图片
     *
     * @param context 上下文
     * @param view    图片控件
     * @param url     网络图片地址
     */

    public static void loadRoundSquare(Context context, ImageView view, String url) {
        loadRoundSquare(context, view, url, DensityUtils.dp2px(context, 3));
    }

    /**
     * 加载圆角图片
     *
     * @param context 上下文
     * @param view    图片控件
     * @param url     网络图片地址
     * @param radius  圆角度数
     */

    public static void loadRoundSquare(Context context, ImageView view, String url, int radius) {
        if (view == null) {
            return;
        }
        //设置图片圆角角度
//        RequestOptions options = RequestOptions.bitmapTransform(new CenterCropRoundCornerTransform(radius));
//        RequestOptions transform = new RequestOptions().transform(new CenterCrop(), new RoundedCorners(radius));
        GlideApp.with(context)
                .load(url)
//                .apply(transform)
                .transform(new CenterCrop(), new RoundedCorners(radius))
                .placeholder(R.mipmap.img_holder_square)
                .error(R.mipmap.img_holder_square)
                .into(view);
    }

    /**
     * 加载圆角图片
     *
     * @param context 上下文
     * @param view    图片控件
     * @param resId   资源图片
     */

    public static void loadRoundSquare(Context context, ImageView view, @RawRes @DrawableRes int resId) {
        loadRoundSquare(context, view, resId, DensityUtils.dp2px(context, 3));
    }

    /**
     * 加载圆角图片
     *
     * @param context 上下文
     * @param view    图片控件
     * @param resId   资源图片
     * @param radius  圆角度数
     */

    public static void loadRoundSquare(Context context, ImageView view, @RawRes @DrawableRes int resId, int radius) {
        if (view == null) {
            return;
        }
//        RequestOptions options = RequestOptions.bitmapTransform(new CenterCropRoundCornerTransform(radius));
//        RequestOptions transform = new RequestOptions().transform(new CenterCrop(), new RoundedCorners(radius));
        GlideApp.with(context)
                .load(resId)
//                .apply(transform)
                .transform(new CenterCrop(), new RoundedCorners(radius))
                .placeholder(R.mipmap.img_holder_square)
                .error(R.mipmap.img_holder_square)
                .into(view);
    }
}
