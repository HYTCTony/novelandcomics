package com.huli.foxread.entity.base;

public class BaseEntity<T>{
    //  判断标示
    private int error;
    //    提示信息
    private String info;
    //显示数据（用户需要关心的数据）
    private T data;

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
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
                "error='" + error + '\'' +
                ", info='" + info + '\'' +
                ", data=" + data +
                '}';
    }
}
