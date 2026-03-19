package com.nnmedia.page.ui.base;


public abstract class BaseMvpActivity<P extends BaseContract.IBasePresenter> extends BaseActivity implements BaseContract.IBaseView {
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
        presenter.detachWindow();
    }
}
