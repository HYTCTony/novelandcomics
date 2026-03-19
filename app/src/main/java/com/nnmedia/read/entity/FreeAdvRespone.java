package com.nnmedia.read.entity;

/**
 * 确定完成免广告任务 --- 返回
 */
public class FreeAdvRespone {
    private int space;              //免广告时间，分钟;
    private int interval;           //任务冷却时间；秒;
    private long advert_time;       //免广告到期时间戳；
    private int site;               //剩余次数

    public int getSpace() {
        return space;
    }

    public void setSpace(int space) {
        this.space = space;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
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
}
