package com.huli.foxread.entity;

import java.io.Serializable;
import java.util.List;

public class HomePageEntity implements Serializable {
    private List<BannerADEntity> banner;                    //轮播广告
    private List<RankBookEntity> today;                     //今日大热榜
    private List<GemGroupEntity> poems;                     //分类佳作
    private List<HotSearchEntity> hot;                      //实时热搜
    private List<HpSpecialEntity> special;                  //专题
    private List<ForestallNewEntity> prior;                 //新书抢先

    public List<BannerADEntity> getBanner() {
        return banner;
    }

    public void setBanner(List<BannerADEntity> banner) {
        this.banner = banner;
    }

    public List<RankBookEntity> getToday() {
        return today;
    }

    public void setToday(List<RankBookEntity> today) {
        this.today = today;
    }

    public List<GemGroupEntity> getPoems() {
        return poems;
    }

    public void setPoems(List<GemGroupEntity> poems) {
        this.poems = poems;
    }

    public List<HotSearchEntity> getHot() {
        return hot;
    }

    public void setHot(List<HotSearchEntity> hot) {
        this.hot = hot;
    }

    public List<HpSpecialEntity> getSpecial() {
        return special;
    }

    public void setSpecial(List<HpSpecialEntity> special) {
        this.special = special;
    }

    public List<ForestallNewEntity> getPrior() {
        return prior;
    }

    public void setPrior(List<ForestallNewEntity> prior) {
        this.prior = prior;
    }
}
