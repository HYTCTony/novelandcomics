package com.nnmedia.comics.fresco;

import android.content.Context;
import android.graphics.Bitmap;

import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.nnmedia.read.FrApp;

import okhttp3.Headers;


public class ImagePipelineFactoryBuilder {

    public static ImagePipelineFactory build(Context context, Headers header, boolean down) {
        ImagePipelineConfig.Builder builder =
                ImagePipelineConfig.newBuilder(context.getApplicationContext())
                        .setDownsampleEnabled(down)
                        .setBitmapsConfig(down ? Bitmap.Config.RGB_565 : Bitmap.Config.ARGB_8888);
        if (header != null) {
            builder.setNetworkFetcher(new OkHttpNetworkFetcher(FrApp.getHttpClient(), header));
        }
        return new ImagePipelineFactory(builder.build());
    }

}
