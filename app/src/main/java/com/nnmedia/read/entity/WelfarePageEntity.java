package com.nnmedia.read.entity;

import java.util.List;

/**
 * 福利页
 */
public class WelfarePageEntity {
    private List<MissionGroupEntity> list;
    private SignInMissionEntity sign_in;
    private List<BannerADEntity> banner;

    public List<MissionGroupEntity> getList() {
        return list;
    }

    public void setList(List<MissionGroupEntity> list) {
        this.list = list;
    }

    public SignInMissionEntity getSign_in() {
        return sign_in;
    }

    public void setSign_in(SignInMissionEntity sign_in) {
        this.sign_in = sign_in;
    }

    public List<BannerADEntity> getBanner() {
        return banner;
    }

    public void setBanner(List<BannerADEntity> banner) {
        this.banner = banner;
    }
}
