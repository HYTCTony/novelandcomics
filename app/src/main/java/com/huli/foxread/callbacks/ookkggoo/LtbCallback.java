package com.huli.foxread.callbacks.ookkggoo;

import com.huli.foxread.R;
import com.huli.foxread.cache.UserInfoCache;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.ui.dialogs.LoadingDialog;
import com.huli.foxread.ui.dialogs.base.BaseDialog;
import com.huli.foxread.utils.Tos;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import androidx.appcompat.app.AppCompatActivity;

/**
 * okgo 带加载框
 */
public abstract class LtbCallback extends StringCallback {

    protected AppCompatActivity mContext;
    private boolean isShowDialog = true;       //是否显示加载框(默认显示)
    private BaseDialog show;
    protected Object reqTag;

    public LtbCallback(AppCompatActivity mContext) {
        this.mContext = mContext;
    }

    public LtbCallback(AppCompatActivity mContext, boolean isShowDialog) {
        this.mContext = mContext;
        this.isShowDialog = isShowDialog;
    }

    @Override
    public void onStart(Request<String, ? extends Request> request) {
        super.onStart(request);
        reqTag = request.getTag();

//        request.headers(Consts.VER, PackageUtils.getVersionName(mContext));
        request.headers(Consts.TOKEN, UserInfoCache.getToken(mContext));

        showLoadingDialog();
    }

    @Override
    public void onFinish() {
        super.onFinish();
        dismissLoadingDialog();
    }


    public void showLoadingDialog() {
        if (mContext == null) return;
        if (isShowDialog) {
            try {
                show = LoadingDialog.newInstance()
                        .setDimAmout(0.1f)
                        .setOutCancel(false)
                        .setBackCancel(false)
                        .setOnDismissListener(dialog -> OkGo.getInstance().cancelTag(reqTag))
                        .show(mContext.getSupportFragmentManager());
            } catch (Exception e) {
            }
        }
//        LoadingView loadingView = new LoadingView(this);
    }

    public void dismissLoadingDialog() {
        if (isShowDialog && show != null) {
            show.dismiss();
        }
    }
}
