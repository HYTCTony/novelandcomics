package com.huli.foxread.rxhttp;

import android.util.Log;

import com.huli.foxread.RxHttp;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import rxhttp.wrapper.parse.SimpleParser;

/**
 * 项目名称：FoxRead
 * 创建人：Bill
 * 创建时间：2020/6/15  17:25
 * 备注：token 失效，自动刷新token，然后再次发送请求，用户无感知
 */
public class TokenInterceptor2 implements Interceptor {

    //token刷新时间
    private static volatile long SESSION_KEY_REFRESH_TIME = 0;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response originalResponse = chain.proceed(request);

        //不拦截获取token的方法
       /* String url = request.url().toString();
        if (url.contains(Consts.USE_UNIQUE_ID_LOGIN_OR_REG_API) || url.contains(Consts.ADS_TAIL_API)
                || url.contains(Consts.USER_MOBILE_LOGIN_API) || url.contains(Consts.USER_LOGOUT_API)
                || (Consts.USERS_INFO_API + "_launch").equals(request.tag())) {
            return originalResponse;
        }*/

        String url = request.url().toString();
        Log.e("gggggggggg", "url===" + url);
        if (url.contains("http://testcover.hongyutiancheng.com.cn/api/user/info")){

        String code = originalResponse.header("error_code");
            Log.e("gggggggggg", "header===" + originalResponse.headers().toString());
            Log.e("gggggggggg", "header---code===" + code);
       /* if ("-1".equals(code)) { //token 失效  1、这里根据自己的业务需求写判断条件
            return handleTokenInvalid(chain, request);
        }*/
        }
        return originalResponse;
    }


    //处理token失效问题
    private Response handleTokenInvalid(Chain chain, Request request) throws IOException {
        HashMap<String, String> mapParam = new HashMap<>();
        RequestBody body = request.body();
        if (body instanceof FormBody) {
            FormBody formBody = (FormBody) body;
            for (int i = 0; i < formBody.size(); i++) {
                mapParam.put(formBody.name(i), formBody.value(i));  //2、保存参数
            }
        }
        //同步刷新token
        String requestTime = mapParam.get("request_time");  //3、发请求前需要add("request_time",System.currentTimeMillis())
        boolean success = refreshToken(requestTime);
        Request newRequest;
        if (success) { //刷新成功，重新签名
            //TODO
//            mapParam.put("token", User.get().getToken()); //4、拿到最新的token,重新发起请求
            newRequest = RxHttp.postForm(request.url().toString())
                    .addAll(mapParam) //添加参数
                    .buildRequest();
        } else {
            newRequest = request;
        }
        return chain.proceed(newRequest);
    }

    //刷新token
    private boolean refreshToken(Object value) {
        long requestTime = 0;
        try {
            requestTime = Integer.parseInt(value.toString());
        } catch (Exception ignore) {
        }
        //请求时间小于token刷新时间，说明token已经刷新，则无需再次刷新
        if (requestTime <= SESSION_KEY_REFRESH_TIME) return true;
        synchronized (this) {
            //再次判断是否已经刷新
            if (requestTime <= SESSION_KEY_REFRESH_TIME) return true;
            try {
                //获取到最新的token，这里需要同步请求token,千万不能异步  5、根据自己的业务修改
                String token = RxHttp.postForm("/refreshToken/...")
                        .execute(SimpleParser.get(String.class));

                SESSION_KEY_REFRESH_TIME = System.currentTimeMillis() / 1000;
                //TODO
//                User.get().setToken(token); //保存最新的token
                return true;
            } catch (IOException e) {
                return false;
            }
        }
    }
}