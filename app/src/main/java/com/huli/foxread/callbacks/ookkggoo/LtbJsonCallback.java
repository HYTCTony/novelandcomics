package com.huli.foxread.callbacks.ookkggoo;

import com.alibaba.fastjson.TypeReference;
import com.huli.foxread.R;
import com.huli.foxread.cache.UserInfoCache;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.ui.dialogs.LoadingDialog;
import com.huli.foxread.ui.dialogs.base.BaseDialog;
import com.huli.foxread.utils.Tos;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.request.base.Request;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Response;

public abstract class LtbJsonCallback<T> extends AbsCallback<T> {

    protected AppCompatActivity mContext;
    private boolean isShowDialog = true;       //是否显示加载框(默认显示)
    private BaseDialog show;
    protected Object reqTag;

    private LtbJsonConvert<T> convert;

    public LtbJsonCallback(AppCompatActivity mContext, TypeReference<T> type) {
        this.mContext = mContext;
        convert = new LtbJsonConvert<>(type);
    }

    public LtbJsonCallback(AppCompatActivity mContext, boolean isShowDialog, TypeReference<T> type) {
        this.mContext = mContext;
        this.isShowDialog = isShowDialog;
        convert = new LtbJsonConvert<>(type);
    }

    @Override
    public T convertResponse(Response response) throws Throwable {
        T t = convert.convertResponse(response);
        response.close();
        return t;
    }


    @Override
    public void onStart(Request<T, ? extends Request> request) {
        super.onStart(request);
        reqTag = request.getTag();

//        request.headers(Consts.VER, PackageUtils.getVersionName(mContext));
        request.headers(Consts.TOKEN, UserInfoCache.getToken(mContext));

        showLoadingDialog();
    }

    @Override
    public void onError(com.lzy.okgo.model.Response<T> response) {
        super.onError(response);
        Tos.showShort(mContext, R.string.txt_network_maybe_exceptions);
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
                        .setDimAmout(0f)
                        .setOutCancel(false)
                        .setBackCancel(false)
                        .setOnDismissListener(dialog -> OkGo.getInstance().cancelTag(reqTag))
                        .show(mContext.getSupportFragmentManager());
            } catch (Exception e) {
            }
        }
    }

    public void dismissLoadingDialog() {
        if (isShowDialog && show != null) {
            show.dismiss();
        }
    }
}