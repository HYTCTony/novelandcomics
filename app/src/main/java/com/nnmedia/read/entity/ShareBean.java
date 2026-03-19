package com.nnmedia.read.entity;

public class ShareBean {
    private String url;
    private String pic;
    private int point;
    private int point_sum;
    private int invitation_sum;
    private String distribution;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public int getPoint_sum() {
        return point_sum;
    }

    public void setPoint_sum(int point_sum) {
        this.point_sum = point_sum;
    }

    public int getInvitation_sum() {
        return invitation_sum;
    }

    public void setInvitation_sum(int invitation_sum) {
        this.invitation_sum = invitation_sum;
    }

    public String getDistribution() {
        return distribution;
    }

    public void setDistribution(String distribution) {
        this.distribution = distribution;
    }
}