package com.huli.page.ui.base;

public interface BaseContract {

    interface IBasePresenter<V> {
        void attatchWindow(V view);

        void detachWindow();
    }

    interface IBaseView {

        void showProgress();

        void hideProgress();

        void onFailure(int code, String err);

        void showToast(String msg);
    }
}
