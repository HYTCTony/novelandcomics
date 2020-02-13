package com.huli.foxread.interceptors;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.huli.foxread.FrApp;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.entity.LoginRpsEntity;
import com.huli.foxread.entity.base.BaseEntity;
import com.huli.foxread.utils.ParamUtil;
import com.huli.foxread.utils.PsuedoIDUtil;
import com.lzy.okgo.OkGo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


/**
 * 全局自动刷新Token的拦截器
 */
public class TokenInterceptor implements Interceptor {

    private Context context;

    public TokenInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);

        if (isTokenExpired(response)) {//根据和服务端的约定判断token过期
            //同步请求方式，获取最新的Token
            String newToken = syncNewToken();
            if (TextUtils.isEmpty(newToken)) {
                tryTime = 0;
                return response;
            }
            //TODO 然后刷新用户信息？要不要叫后台新写一个刷新token的同时刷新用户信息？

            //使用新的Token，创建新的请求
            Request newRequest = chain.request();
            Request.Builder requestBuilder = newRequest.newBuilder();
            requestBuilder.header(Consts.D_TOKEN, ParamUtil.dealTokenParam(newToken));      //添加到头

            /*if (newRequest.body() instanceof FormBody) {              //Okgo 在外面 封装了ProgressRequestBody,所以这个方法失效
                FormBody.Builder newFormBody = new FormBody.Builder();
                FormBody oidFormBody = (FormBody) newRequest.body();

                for (int i = 0; i < oidFormBody.size(); i++) {
                    newFormBody.addEncoded(oidFormBody.encodedName(i), oidFormBody.encodedValue(i));
                }
                newFormBody.add(Consts.D_TOKEN, ParamUtil.EncryptStr(newToken));

                Log.e("sssssss", "newToken==" + newToken);
                requestBuilder.method(newRequest.method(), newFormBody.build());
            }else {
                Log.e("sssssss", "不走不走");
            }*/
            //重新请求
            return chain.proceed(requestBuilder.build());
        }
        return response;
    }


    /**
     * 根据Response，判断Token是否失效
     *
     * @param response 状态码999  ---Token失效   998(空)
     * @return
     */
    private boolean isTokenExpired(Response response) {
        int code = response.code();
//        Log.e("ssssss", "token===" + code);
        if (code == 999 || code == 998) {
            response.close();
            return true;
        }
        return false;
    }

    private static int REFRESH_TOKEN_TIME = 3;
    private int tryTime = 0;

    /**
     * 同步请求方式，获取最新的Token
     *
     * @return
     */
    private String syncNewToken() throws IOException {
        if (tryTime >= REFRESH_TOKEN_TIME) {
            return null;
        }
        tryTime++;

        // 通过一个特定的接口获取新的token，此处要用到同步请求
        Response response = OkGo.<String>post(Consts.USE_UNIQUE_ID_LOGIN_OR_REG_API)//
                .params(Consts.DATAS, sign())
                .retryCount(3)
                .execute();
        try {
            if (response.body() == null) {
                return null;
            }
            BaseEntity<LoginRpsEntity> entity = JSONObject.parseObject(response.body().string(),
                    new TypeReference<BaseEntity<LoginRpsEntity>>() {
                    });
            if (entity.getError() == 0) {
                LoginRpsEntity data = entity.getData();
                String newToken = data.getToken();

                FrApp instance = FrApp.getInstance();
                instance.setToken(newToken);
                instance.setBindPhone(false);

                return newToken;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }


    private String sign() {
        String psuedoID = PsuedoIDUtil.getUniquePsuedoID();
        Map<String, Object> map = new HashMap<>();
        map.put(Consts.UNIQUE_ID, psuedoID);
        return ParamUtil.dealParams(map);
    }
}