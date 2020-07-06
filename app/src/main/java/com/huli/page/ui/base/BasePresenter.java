package com.huli.page.ui.base;


import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class BasePresenter<V extends BaseContract.IBaseView> implements BaseContract.IBasePresenter<V> {

    public V view;
//    protected BaseSchedulerProvider schedulerProvider;
    protected CompositeDisposable mDisposable;

    public BasePresenter() {
//        this.schedulerProvider = SchedulerProvider.getInstance();
        mDisposable = new CompositeDisposable();
    }

    public void despose() {
        mDisposable.dispose();
    }


    @Override
    public void attatchWindow(V v) {
        this.view = v;
    }

    @Override
    public void detachWindow() {
        this.view = null;
    }

    public boolean isViewAttached() {
        return view != null;
    }

    public V getMvpView() {
        return view;
    }

    public void checkViewAttached() {
        if (!isViewAttached()) {
            despose();
            throw new MvpViewNotAttachedException();
        }
    }

    public static class MvpViewNotAttachedException extends RuntimeException {
        public MvpViewNotAttachedException() {
            super("请求数据前请先调用 attachView(MvpView) 方法与View建立连接");
        }
    }
}
