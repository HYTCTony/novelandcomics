package com.nnmedia.read.rxhttp.interceptors;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.nnmedia.novel.RxHttp;
import com.nnmedia.read.cache.TokenCache;
import com.nnmedia.read.cache.UserInfoCache;
import com.nnmedia.read.contact.Consts;
import com.nnmedia.read.entity.LoginRpsEntity;
import com.nnmedia.read.entity.base.LzyResponse;
import com.nnmedia.read.ui.activities.TransparencyActivity;
import com.nnmedia.read.utils.UniqueIdManager;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import rxhttp.wrapper.parse.SimpleParser;

/**
 * 创建时间：2020/6/15  17:25
 * 备注：token 失效，获取游客token重新请求，并提醒用户去登陆
 */
public class TokenInterceptor2 implements Interceptor {

    //token刷新时间
    private static volatile long SESSION_KEY_REFRESH_TIME = 0;

    private Context context;

    public TokenInterceptor2(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response originalResponse = chain.proceed(request);

        //不拦截获取token的方法
        String url = request.url().toString();
        if (url.contains(Consts.USE_UNIQUE_ID_LOGIN_OR_REG_API) || url.contains(Consts.USER_MOBILE_LOGIN_API)
                || url.contains(Consts.USER_LOGOUT_API)) {
            return originalResponse;
        }

        String code = originalResponse.header("error_code");
        // error_code 状态码10001  ---Token失效    10010 --启动页检测TOKEN
        if (!TextUtils.isEmpty(code) && (("10001".equals(code) || "10010".equals(code)))) {
            if (!"10010".equals(code)) {
                Intent intent = new Intent(context, TransparencyActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
            return handleTokenInvalid(chain, request);
        }


       /* if ("-1".equals(code)) { //token 失效  1、这里根据自己的业务需求写判断条件
            return handleTokenInvalid(chain, request);
        }*/
        return originalResponse;
    }


    //处理token失效问题
    private Response handleTokenInvalid(Chain chain, Request request) throws IOException {
        /*HashMap<String, String> mapParam = new HashMap<>();
        RequestBody body = request.body();
        if (body instanceof FormBody) {
            FormBody formBody = (FormBody) body;
            for (int i = 0; i < formBody.size(); i++) {
                mapParam.put(formBody.name(i), formBody.value(i));  //2、保存参数
            }
        }*/
        //同步刷新token
        String requestTime = request.header("request_time");  //3、发请求前需要add("request_time",System.currentTimeMillis())
        boolean success = refreshToken(requestTime);
        Request newRequest;
        if (success) { //刷新成功，重新签名
            /*newRequest = RxHttp.postForm(request.url().toString())
                    .addHeader(Consts.TOKEN, TokenCache.getToken(context))      //4、拿到最新的token,重新发起请求
                    .addAll(mapParam) //添加参数
                    .buildRequest();*/
            newRequest = chain.request();
            Request.Builder requestBuilder = newRequest.newBuilder();
            requestBuilder.removeHeader(Consts.TOKEN);             //添加(替换)到头
            requestBuilder.addHeader(Consts.TOKEN, TokenCache.getToken(context));      //添加(替换)到头
            newRequest = requestBuilder.build();
        } else {
            newRequest = request;
        }
        return chain.proceed(newRequest);
    }

    //刷新token
    private boolean refreshToken(Object value) {
     /*   long requestTime = 0;
        try {
            requestTime = Long.parseLong(value.toString());
        } catch (Exception ignore) {
        }
        //请求时间小于token刷新时间，说明token已经刷新，则无需再次刷新
        if (requestTime <= SESSION_KEY_REFRESH_TIME) return true;*/
        synchronized (this) {
            //再次判断是否已经刷新
//            if (requestTime <= SESSION_KEY_REFRESH_TIME) return true;
            try {
                //获取到最新的token，这里需要同步请求token,千万不能异步  5、根据自己的业务修改
                String respStr = RxHttp.postForm(Consts.USE_UNIQUE_ID_LOGIN_OR_REG_API)
                        .setAssemblyEnabled(false)
                        .add(Consts.UNIQUE_ID, UniqueIdManager.getUniqueID(context))
                        .execute(SimpleParser.get(String.class));

//                SESSION_KEY_REFRESH_TIME = System.currentTimeMillis() / 1000;
                LzyResponse<LoginRpsEntity> entity = JSONObject.parseObject(respStr,
                        new TypeReference<LzyResponse<LoginRpsEntity>>() {
                        });
                if (entity.error_code == 0) {
                    LoginRpsEntity loginRpsEntity = entity.getData();
                    String newToken = loginRpsEntity.getToken();
                    if (!TextUtils.isEmpty(newToken)) {
                        //清除保存的用户信息
                        UserInfoCache.clearCache(context);
                        //保存新的token
                        TokenCache.saveToken(context, newToken);
                        EventBus.getDefault().postSticky(UserInfoCache.getUserInfo(context));      //通知改变UI
                    }
                }
                return true;
            } catch (IOException e) {
                return false;
            }
        }
    }

    /*
     * 备注：token 失效，自动刷新token，然后再次发送请求，用户无感知
     */

   /* //处理token失效问题
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
//                User.get().setToken(token); //保存最新的token
                return true;
            } catch (IOException e) {
                return false;
            }
        }
    }*/
}