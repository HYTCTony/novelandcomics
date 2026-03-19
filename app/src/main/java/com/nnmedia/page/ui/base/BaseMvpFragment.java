package com.nnmedia.page.ui.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import butterknife.ButterKnife;

public abstract class BaseMvpFragment<P extends BaseContract.IBasePresenter> extends BaseFragment implements BaseContract.IBaseView {

    public P presenter;
    private View mV = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        if (mV == null) {
            mV = inflater.inflate(getContentViewResId(), container, false);
            ButterKnife.bind(this, mV);
        }
        ViewGroup parent = (ViewGroup) mV.getParent();
        if (parent != null)
            parent.removeView(mV);
        presenter = initPresenter();
        if (presenter != null) {
            presenter.attatchWindow(this);
        }
        init();
        return mV;
    }

    protected abstract P initPresenter();

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.detachWindow();
    }
}
