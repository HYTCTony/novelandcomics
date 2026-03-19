package com.nnmedia.page.model.bean;

public class Advert {
    private int space;  //免广告时间*分钟
    private long interval;//任务冷却时间
    private long advert_time;//免广告到期时间戳
    private int site;//剩余次数
    private long lapse;//剩余免广告时间

    public int getSpace() {
        return space;
    }

    public void setSpace(int space) {
        this.space = space;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public long getAdvert_time() {
        return advert_time;
    }

    public void setAdvert_time(long advert_time) {
        this.advert_time = advert_time;
    }

    public int getSite() {
        return site;
    }

    public void setSite(int site) {
        this.site = site;
    }

    public long getLapse() {
        return lapse;
    }

    public void setLapse(long lapse) {
        this.lapse = lapse;
    }
}
