package com.nnmedia.read.rxhttp;

import io.reactivex.rxjava3.functions.Consumer;

/**
 * 创建时间：2020/6/15  15:42
 * 备注：错误回调 ,加入网络异常处理
 */
public interface OnError extends Consumer<Throwable> {

    @Override
    default void accept(Throwable throwable) throws Exception {
        onError(new ErrorInfo(throwable));
    }

    void onError(ErrorInfo error) throws Exception;
}