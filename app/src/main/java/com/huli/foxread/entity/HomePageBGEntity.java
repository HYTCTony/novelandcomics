package com.huli.foxread.entity;

import com.huli.foxread.entity.multi.HpBGModuleEntity;

import java.io.Serializable;
import java.util.List;

public class HomePageBGEntity implements Serializable {
    private List<BannerADEntity> banner;                    //轮播广告
    private List<HpBGModuleEntity> module;

    public List<BannerADEntity> getBanner() {
        return banner;
    }

    public void setBanner(List<BannerADEntity> banner) {
        this.banner = banner;
    }

    public List<HpBGModuleEntity> getModule() {
        return module;
    }

    public void setModule(List<HpBGModuleEntity> module) {
        this.module = module;
    }
}
