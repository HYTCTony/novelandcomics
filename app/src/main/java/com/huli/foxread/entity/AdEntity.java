package com.huli.foxread.entity;

import java.io.Serializable;

public class AdEntity implements Serializable {
    private String id;
    //    private String advertiser;
//    private String name;
//    private String introduce;
//    private int type;
//    private int stance;
//    private int outer;
//    private int show;
    private String image;
    private String video;
    private String links;
//    private int weigh;
//    private int clicks;
//    private int views;
//    private int status;
//    private long createtime;
//    private long updatetime;
//    private long deletetime;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getLinks() {
        return links;
    }

    public void setLinks(String links) {
        this.links = links;
    }
}
