package com.nnmedia.read.callbacks;

public interface ActivityState {

    /**
     * 判断应用是否处于前台，即是否可见
     *
     * @return
     */
    void isFront();


    void isBack();
}
