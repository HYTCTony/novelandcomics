package com.huli.foxread.entity.base;

public class BaseEntity<T>{
    //  判断标示
    private int error_code;
    //    提示信息
    private String msg;
    //显示数据（用户需要关心的数据）
    private T data;

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BaseEntity{" +
                "error_code=" + error_code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
