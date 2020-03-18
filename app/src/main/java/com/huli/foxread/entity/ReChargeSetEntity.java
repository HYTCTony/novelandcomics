package com.huli.foxread.entity;

public class ReChargeSetEntity {
    private String id;
    private String title;
    private double initial_price;
    private double discount_price;
    private int is_hot;
    private int is_recommend;
    private int monthly_number;

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
}
