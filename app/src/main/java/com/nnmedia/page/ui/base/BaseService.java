package com.nnmedia.page.ui.base;

import android.app.Service;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;


public abstract class BaseService extends Service {

    private CompositeDisposable mDisposable;

    protected void addDisposable(Disposable disposable) {
        if (mDisposable == null) {
            mDisposable = new CompositeDisposable();
        }
        mDisposable.add(disposable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }
}
