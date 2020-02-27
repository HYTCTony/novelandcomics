package com.huli.foxread.callbacks.ookkggoo;

import com.huli.foxread.R;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.v3.WaitDialog;
import com.lzy.okgo.request.base.Request;

import androidx.appcompat.app.AppCompatActivity;

/**
 * okgo 带加载框
 */

public abstract class DialogCallback extends EncryptCallback {

    private boolean isShowDialog = true;       //是否显示加载框(默认显示)
    private BaseDialog show;

    public DialogCallback(AppCompatActivity mContext) {
        super(mContext);
        this.mContext = mContext;
    }

    public DialogCallback(AppCompatActivity mContext, boolean isShowDialog) {
        super(mContext);
        this.isShowDialog = isShowDialog;
    }

    @Override
    public void onStart(Request<String, ? extends Request> request) {
        super.onStart(request);
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
            show = WaitDialog.show(mContext, R.string.loading);
        }
    }

    public void dismissLoadingDialog() {
        if (isShowDialog && show != null) {
            show.doDismiss();
        }
    }

}
