package com.huli.foxread.callbacks.ookkggoo;

import android.support.v7.app.AppCompatActivity;

import com.alibaba.fastjson.JSONObject;
import com.huli.foxread.R;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.utils.PackageUtils;
import com.huli.foxread.utils.ParamUtil;
import com.kongzue.dialog.v3.TipDialog;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.request.base.Request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class EncryptCallback extends StringCallback {

    protected AppCompatActivity mContext;
    protected Object reqTag;

    protected EncryptCallback(AppCompatActivity mContext) {
        this.mContext = mContext;
    }


    @Override
    public void onStart(Request<String, ? extends Request> request) {
        super.onStart(request);
        reqTag = request.getTag();

        // 主要用于在所有请求之前添加公共的请求头或请求参数
        // 例如登录授权的 token
        // 使用的设备信息
        // 可以随意添加,也可以什么都不传
        // 还可以在这里对所有的参数进行加密，均在这里实现
        request.params(Consts.VER, PackageUtils.getVersionName(mContext));

        //以下是示例加密代码，根据自己的业务需求和服务器的配合
        sign(request.getParams());
    }


    @Override
    public void onError(com.lzy.okgo.model.Response<String> response) {
        super.onError(response);
        TipDialog.show(mContext, R.string.txt_network_maybe_exceptions, TipDialog.TYPE.ERROR);
    }


    /**
     * 加密普通参数
     *
     * @param params
     */
    private void sign(HttpParams params) {
        Map<String, String> map = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : params.urlParamsMap.entrySet()) {
            map.put(entry.getKey(), entry.getValue().get(0));
        }
        String jsonString = JSONObject.toJSONString(map);
        String encrypt = ParamUtil.encryptStr(jsonString);
        params.put(Consts.DATAS, encrypt);
    }
}