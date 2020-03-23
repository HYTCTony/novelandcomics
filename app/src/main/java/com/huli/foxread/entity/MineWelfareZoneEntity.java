package com.huli.foxread.entity;

public class MineWelfareZoneEntity extends BannerADEntity{
    private String name;
    private String link;
    private String image;
    private String gradation_start;
    private String gradation_end;
    private int number;
    private String http_image;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getGradation_start() {
        return gradation_start;
    }

    public void setGradation_start(String gradation_start) {
        this.gradation_start = gradation_start;
    }

    public String getGradation_end() {
        return gradation_end;
    }

    public void setGradation_end(String gradation_end) {
        this.gradation_end = gradation_end;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getHttp_image() {
        return http_image;
    }

    public void setHttp_image(String http_image) {
        this.http_image = http_image;
    }
}
