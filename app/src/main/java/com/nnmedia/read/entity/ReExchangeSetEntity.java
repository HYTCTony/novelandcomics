package com.nnmedia.read.entity;

import android.text.TextUtils;

public class ReExchangeSetEntity {

    private String id;
    private String title;
    private String description;
    private double gold;
    private double point;
    private double initial_price;
    private double discount_price;
    private int pay_type;
    private int is_hot;
    private int is_recommend;
    private int monthly_number;
    private String product_id;
    private int cdkey;
    private String alipay;
    private String weixin;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getGold() {
        return gold;
    }

    public void setGold(double gold) {
        this.gold = gold;
    }

    public double getPoint() {
        return point;
    }

    public void setPoint(double point) {
        this.point = point;
    }

    public double getInitial_price() {
        return initial_price;
    }

    public void setInitial_price(double initial_price) {
        this.initial_price = initial_price;
    }

    public double getDiscount_price() {
        return discount_price;
    }

    public void setDiscount_price(double discount_price) {
        this.discount_price = discount_price;
    }

    public int getPay_type() {
        return pay_type;
    }

    public void setPay_type(int pay_type) {
        this.pay_type = pay_type;
    }

    public int getIs_hot() {
        return is_hot;
    }

    public void setIs_hot(int is_hot) {
        this.is_hot = is_hot;
    }

    public int getIs_recommend() {
        return is_recommend;
    }

    public void setIs_recommend(int is_recommend) {
        this.is_recommend = is_recommend;
    }

    public int getMonthly_number() {
        return monthly_number;
    }

    public void setMonthly_number(int monthly_number) {
        this.monthly_number = monthly_number;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public int getCdkey() {
        return cdkey;
    }

    public void setCdkey(int cdkey) {
        this.cdkey = cdkey;
    }

    public String getAlipay() {
        if (TextUtils.isEmpty(alipay)) {
            return "";
        }
        return alipay;
    }

    public void setAlipay(String alipay) {
        this.alipay = alipay;
    }

    public String getWeixin() {
        if (TextUtils.isEmpty(weixin)) {
            return "";
        }
        return weixin;
    }

    public void setWeixin(String weixin) {
        this.weixin = weixin;
    }
}