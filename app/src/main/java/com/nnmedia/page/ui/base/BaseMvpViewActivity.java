package com.nnmedia.page.ui.base;


public abstract class BaseMvpViewActivity<P extends BaseContract.IBasePresenter> extends BaseViewActivity implements BaseContract.IBaseView {
    public P presenter;

    @Override
    protected void initP() {
        presenter = initPresenter();
        if (presenter != null) {
            presenter.attatchWindow(this);
        }
    }

    protected abstract P initPresenter();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.detachWindow();
            presenter = null;
            System.gc();
        }
    }
}
