package com.nnmedia.read.entity;

import java.io.Serializable;

public class PaymentInfoEntity implements Serializable {
    private String user_id;
    private String order_sn;
    private String body;
    private int monthly;
    private int type;
    private int hpoint;
    private int added;
    private double payment_money;
    private String link;

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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public double getPayment_money() {
        return payment_money;
    }

    public void setPayment_money(double payment_money) {
        this.payment_money = payment_money;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getHpoint() {
        return hpoint;
    }

    public void setHpoint(int hpoint) {
        this.hpoint = hpoint;
    }

    public int getAdded() {
        return added;
    }

    public void setAdded(int added) {
        this.added = added;
    }
}