package com.nnmedia.read.entity;

public class SysAvatarEntity {
    private String id;
    private String name;
    private String httpImage;
    private String image;           //修改的时候传这个

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHttpImage() {
        return httpImage;
    }

    public void setHttpImage(String httpImage) {
        this.httpImage = httpImage;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
