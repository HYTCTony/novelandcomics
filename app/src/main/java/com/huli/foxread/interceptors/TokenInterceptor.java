package com.huli.foxread.interceptors;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.huli.foxread.cache.TokenCache;
import com.huli.foxread.cache.UserInfoCache2;
import com.huli.foxread.callbacks.ookkggoo.LzyResponse;
import com.huli.foxread.contact.Consts;
import com.huli.foxread.entity.FUser;
import com.huli.foxread.entity.LoginRpsEntity;
import com.huli.foxread.ui.activities.TransparencyActivity;
import com.huli.foxread.utils.UniqueIdManager;
import com.lzy.okgo.OkGo;

import org.greenrobot.eventbus.EventBus;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;
import okio.BufferedSource;

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
        String url = request.url().toString();
        if (url.contains(Consts.USE_UNIQUE_ID_LOGIN_OR_REG_API) || url.contains(Consts.ADS_TAIL_API)
                || url.contains(Consts.USER_MOBILE_LOGIN_API) || url.contains(Consts.USER_LOGOUT_API)
                || request.tag().equals(Consts.USERS_INFO_API + "_launch")) {
            return response;
        }

        ResponseBody responseBody = response.body();
        long contentLength = responseBody.contentLength();

        //注意 >>>>>>>>> okhttp3.4.1这里变成了 !HttpHeader.hasBody(response)
        //if (!HttpEngine.hasBody(response)) {
        if (!HttpHeaders.hasBody(response)) { //HttpHeader -> 改成了 HttpHeaders，看版本进行选择
            //END HTTP
        } else if (bodyEncoded(response.headers())) {
            //HTTP (encoded body omitted)
        } else {
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.buffer();

            Charset charset = UTF8;
            MediaType contentType = responseBody.contentType();
            if (contentType != null) {
                try {
                    charset = contentType.charset(UTF8);
                } catch (UnsupportedCharsetException e) {
                    //Couldn't decode the response body; charset is likely malformed.
                    return response;
                }
            }

            if (!isPlaintext(buffer)) {
                return response;
            }

            if (contentLength != 0) {
                //获取到response的body的string字符串
                //do something .... <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                String result = buffer.clone().readString(charset);

                JSONObject jsonObject = JSONObject.parseObject(result);
                int errorCode = jsonObject.getIntValue("error_code");
                // error_code 状态码10001  ---Token失效    10010 被顶号
                if (errorCode == 10001 || errorCode == 10010) {
//                    OkGo.getInstance().cancelAll();
                    String newToken = syncNewToken();
                    Intent intent = new Intent(context, TransparencyActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

                    Request newRequest = chain.request();
                    Request.Builder requestBuilder = newRequest.newBuilder();
                    requestBuilder.removeHeader(Consts.TOKEN);             //添加(替换)到头
                    requestBuilder.addHeader(Consts.TOKEN, newToken);      //添加(替换)到头
                    //重新请求原接口
                    return chain.proceed(requestBuilder.build());

                }
            }
        }
        return response;


       /* Request request = chain.request();
        Response response = chain.proceed(request);

        //不拦截获取token的方法
        if (request.url().toString().contains(Consts.USE_UNIQUE_ID_LOGIN_OR_REG_API)) {
            return response;
        }
        return checkTokenResponse(chain, response);*/

    }


    /*token失效时自动取获取一次，然后判断重新请求原接口*/
   /* private Response checkTokenResponse(Chain chain, Response response) {
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

                    JSONObject jsonObject = JSONObject.parseObject(body);
                    int errorCode = jsonObject.getIntValue("error_code");
                    if (errorCode == 10001 || errorCode == 10010) {
                        context.startActivity(new Intent(context, TransparencyActivity.class));
                    }
                    return builder.body(responseBody).build();

                    if (isTokenExpired(body)) {
                        //同步请求方式，获取最新的Token
                        String newToken = syncNewToken();
                        if (!TextUtils.isEmpty(newToken)) {
                            //使用新的Token，创建新的请求
                            Request newRequest = chain.request();
                            Request.Builder requestBuilder = newRequest.newBuilder();
                            requestBuilder.removeHeader(Consts.TOKEN);             //添加(替换)到头
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
    }*/

   /* private static Charset getCharset(MediaType contentType) {
        Charset charset = contentType != null ? contentType.charset(UTF8) : UTF8;
        if (charset == null) charset = UTF8;
        return charset;
    }

    *//**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     *//*
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
    }*/


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
            LzyResponse<LoginRpsEntity> entity = JSONObject.parseObject(response.body().string(),
                    new TypeReference<LzyResponse<LoginRpsEntity>>() {
                    });
            if (entity.error_code == 0) {
                LoginRpsEntity loginRpsEntity = entity.getData();
                String newToken = loginRpsEntity.getToken();
                if (!TextUtils.isEmpty(newToken)) {
                    //清除保存的用户信息
                    UserInfoCache2.clearCache(context);
                    //保存新的token
                    TokenCache.saveToken(context, newToken);

                    EventBus.getDefault().postSticky(UserInfoCache2.getUserInfo(context));      //通知改变UI
                    return newToken;
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    static boolean isPlaintext(Buffer buffer) throws EOFException {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }

    private boolean bodyEncoded(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null && !contentEncoding.equalsIgnoreCase("identity");
    }

}