package com.huli.foxread.entity;

import java.io.Serializable;

public class PaymentInfoEntity implements Serializable {
    private String user_id;
    private String order_sn;
    private String body;
    private int monthly;
    private double payment_money;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getOrder_sn() {
        return order_sn;
    }

    public void setOrder_sn(String order_sn) {
        this.order_sn = order_sn;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getMonthly() {
        return monthly;
    }

    public void setMonthly(int monthly) {
        this.monthly = monthly;
    }

    public double getPayment_money() {
        return payment_money;
    }

    public void setPayment_money(double payment_money) {
        this.payment_money = payment_money;
    }
}
