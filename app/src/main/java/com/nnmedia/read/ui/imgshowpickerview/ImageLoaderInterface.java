package com.nnmedia.read.ui.imgshowpickerview;

import android.content.Context;
import android.view.View;

import java.io.Serializable;

import androidx.annotation.DrawableRes;

public interface ImageLoaderInterface<T extends View> extends Serializable {

    void displayImage(Context context, String path, T imageView);

    void displayImage(Context context, @DrawableRes Integer resId, T imageView);

    T createImageView(Context context);
}
