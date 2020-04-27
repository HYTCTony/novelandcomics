package com.huli.foxread.engines;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.request.RequestOptions;
import com.huli.foxread.GlideApp;
import com.huli.foxread.R;
import com.huli.foxread.entity.BannerADEntity;
import com.huli.foxread.transforms.CenterCropRoundCornerTransform;
import com.huli.foxread.utils.DensityUtils;
import com.youth.banner.loader.ImageLoader;


public class GlideImageLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        /**
         注意：
         1.图片加载器由自己选择，这里不限制，只是提供几种使用方法
         2.返回的图片路径为Object类型，由于不能确定你到底使用的那种图片加载器，
         传输的到的是什么格式，那么这种就使用Object接收和返回，你只需要强转成你传输的类型就行，
         切记不要胡乱强转！
         */

        if (path instanceof BannerADEntity) {
            BannerADEntity adEntity = (BannerADEntity) path;
            //Glide 加载图片简单用法
            GlideApp.with(context)
                    .load(adEntity.getImageText())
                    .apply(RequestOptions.bitmapTransform(new CenterCropRoundCornerTransform(DensityUtils.dp2px(context, 8))))
                    .placeholder(R.mipmap.banner_place_holder)
                    .error(R.mipmap.banner_place_holder)
                    .into(imageView);
        }
    }

    //提供createImageView 方法，如果不用可以不重写这个方法，主要是方便自定义ImageView的创建
    @Override
    public ImageView createImageView(Context context) {
        int paddingPx = DensityUtils.dp2px(context, 8);

        //使用fresco，需要创建它提供的ImageView，当然你也可以用自己自定义的具有图片加载功能的ImageView
        ImageView imageView = new ImageView(context);
        imageView.setPadding(paddingPx, paddingPx / 2, paddingPx, paddingPx * 2);
        return imageView;
    }
}