package com.nnmedia.read.entity;

public class BillDetailEntity {
    private int id;
    private int user_id;
    private String order_sn;//订单号
    private String body;//描述
    private int payment_code;// 微信：4 支付宝：5 没有支付：0
    private String payment_money;//支付金额
    private String actual_money;//实际付款金额
    private int order_state;//取消：0  未付款：10 已付款：20
    private long createtime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
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

    public int getPayment_code() {
        return payment_code;
    }

    public void setPayment_code(int payment_code) {
        this.payment_code = payment_code;
    }

    public String getPayment_money() {
        return payment_money;
    }

    public void setPayment_money(String payment_money) {
        this.payment_money = payment_money;
    }

    public String getActual_money() {
        return actual_money;
    }

    public void setActual_money(String actual_money) {
        this.actual_money = actual_money;
    }

    public int getOrder_state() {
        return order_state;
    }

    public void setOrder_state(int order_state) {
        this.order_state = order_state;
    }

    public long getCreatetime() {
        return createtime;
    }

    public void setCreatetime(long createtime) {
        this.createtime = createtime;
    }
}