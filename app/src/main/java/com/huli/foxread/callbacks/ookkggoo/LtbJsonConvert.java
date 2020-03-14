package com.huli.foxread.callbacks.ookkggoo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lzy.okgo.convert.Converter;

import okhttp3.Response;
import okhttp3.ResponseBody;

public class LtbJsonConvert<T> implements Converter<T> {
    TypeReference<T> type;

    public LtbJsonConvert(TypeReference<T> type) {
        this.type = type;
    }

    @Override
    public T convertResponse(Response response) throws Throwable {
        ResponseBody body = response.body();
        if (body == null) return null;
        String bodyStr = body.string();
        return JSON.parseObject(bodyStr, type);
    }
}
