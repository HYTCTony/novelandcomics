package com.huli.foxread.interceptors;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.huli.foxread.cache.UserInfoCache;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.entity.FUser;
import com.huli.foxread.utils.UniqueIdManager;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.utils.IOUtils;
import com.lzy.okgo.utils.OkLogger;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;

import static com.alibaba.fastjson.util.IOUtils.UTF8;


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

        //不拦截获取token的方法
        if (request.url().toString().contains(Consts.USE_UNIQUE_ID_LOGIN_OR_REG_API)) {
            return response;
        }
        return checkTokenResponse(chain, response);

      /*  if (isTokenExpired(response)) {//根据和服务端的约定判断token过期
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
            requestBuilder.header(Consts.TOKEN, newToken);      //添加(替换)到头

            //重新请求
            return chain.proceed(requestBuilder.build());
        }

        Log.e("isTokenExpired", "没过期====" + response.body());
        return response;*/
    }

    /*token失效时自动取获取一次，然后判断重新请求原接口*/
    private Response checkTokenResponse(Chain chain, Response response) {
        Response.Builder builder = response.newBuilder();
        Response clone = builder.build();
        ResponseBody responseBody = clone.body();
        try {
            if (HttpHeaders.hasBody(clone)) {
                if (responseBody == null) return response;
                if (isPlaintext(responseBody.contentType())) {
                    byte[] bytes = IOUtils.toByteArray(responseBody.byteStream());
                    MediaType contentType = responseBody.contentType();
                    String body = new String(bytes, getCharset(contentType));

                    if (isTokenExpired(body)) {
                        //同步请求方式，获取最新的Token
                        String newToken = syncNewToken();
                        if (!TextUtils.isEmpty(newToken)) {
                            //使用新的Token，创建新的请求
                            Request newRequest = chain.request();
                            Request.Builder requestBuilder = newRequest.newBuilder();
                            requestBuilder.addHeader(Consts.TOKEN, newToken);      //添加(替换)到头
                            //重新请求原接口
                            return chain.proceed(requestBuilder.build());

                            //这样做要想办法跳出checkTokenResponse循环
//                            Response proceed = chain.proceed(requestBuilder.build());
//                            return checkTokenResponse(chain,proceed);
                        }
                    }
                    responseBody = ResponseBody.create(responseBody.contentType(), bytes);
                    return response.newBuilder().body(responseBody).build();
                } else {
                }
            }
        } catch (Exception e) {
            OkLogger.printStackTrace(e);
        } finally {
        }
        return response;
    }

    private static Charset getCharset(MediaType contentType) {
        Charset charset = contentType != null ? contentType.charset(UTF8) : UTF8;
        if (charset == null) charset = UTF8;
        return charset;
    }

    /**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     */
    private static boolean isPlaintext(MediaType mediaType) {
        if (mediaType == null) return false;
        if (mediaType.type() != null && mediaType.type().equals("text")) {
            return true;
        }
        String subtype = mediaType.subtype();
        if (subtype != null) {
            subtype = subtype.toLowerCase();
            if (subtype.contains("x-www-form-urlencoded") || subtype.contains("json") || subtype.contains("xml") || subtype.contains("html")) //
                return true;
        }
        return false;
    }

    /**
     * 根据Response，判断Token是否失效
     *
     * @return error_code 状态码10001  ---Token失效
     */
    private boolean isTokenExpired(String body) {
        JSONObject jsonObject = JSONObject.parseObject(body);
        int errorCode = jsonObject.getIntValue("error_code");
        if (errorCode == 10001) {
            return true;
        }
        return false;
    }


    /**
     * 同步请求方式，获取最新的Token
     *
     * @return
     */
    private String syncNewToken() throws IOException {
        String psuedoID = UniqueIdManager.getUniqueID(context);
        // 通过一个特定的接口获取新的token，此处要用到同步请求
        Response response = OkGo.<LzyResponse<FUser>>post(Consts.USE_UNIQUE_ID_LOGIN_OR_REG_API)
                .params(Consts.UNIQUE_ID, psuedoID)
                .retryCount(3)
                .execute();
        try {
            if (response.body() == null) {
                return null;
            }
            LzyResponse<String> entity = JSONObject.parseObject(response.body().string(),
                    new TypeReference<LzyResponse<String>>() {
                    });
            if (entity.error_code == 0) {
                JSONObject object = JSONObject.parseObject(entity.getData());
                String newToken = object.getString("token");
                if (!TextUtils.isEmpty(newToken)) {
                    //清除保存的用户信息
                    UserInfoCache.clearCache(context);
                    //保存新的token
                    UserInfoCache.saveToken(context, newToken);
                    return newToken;
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

}