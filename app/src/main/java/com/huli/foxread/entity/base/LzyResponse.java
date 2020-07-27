package com.huli.foxread.entity.base;

import java.io.Serializable;

public class LzyResponse<T> implements Serializable {

    public int error_code;
    public String msg;
    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "LzyResponse{\n" +//
                "\terror_code=" + error_code + "\n" +//
                "\tmsg='" + msg + "\'\n" +//
                "\tdata=" + data + "\n" +//
                '}';
    }
}