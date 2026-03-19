package com.nnmedia.read.entity;

import com.nnmedia.read.ui.imgshowpickerview.ImageShowPickerBean;

public class ImageBean extends ImageShowPickerBean {

    private int id;
    private String url;

    public ImageBean(int id, String url) {
        this.id = id;
        this.url = url;
    }

    @Override
    public String setImageShowPickerUrl() {
        return url;
    }

    @Override
    public int setImageShowPickerDelRes() {
        return id;
    }
}