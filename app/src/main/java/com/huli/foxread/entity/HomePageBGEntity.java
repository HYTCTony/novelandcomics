package com.huli.foxread.entity;

import com.huli.foxread.entity.multi.BookMultiEntity;

import java.util.List;

public class HomePageBGEntity {
    private List<BannerADEntity> banner;                    //轮播广告
    private List<ExclusiveBookEntity> praise;               //重磅推荐
    private List<BookMultiEntity> favourable;               //好评佳作
    private List<HpSpecialEntity> special;                  //专题

    public List<BannerADEntity> getBanner() {
        return banner;
    }

    public void setBanner(List<BannerADEntity> banner) {
        this.banner = banner;
    }

    public List<ExclusiveBookEntity> getPraise() {
        return praise;
    }

    public void setPraise(List<ExclusiveBookEntity> praise) {
        this.praise = praise;
    }

    public List<BookMultiEntity> getFavourable() {
        return favourable;
    }

    public void setFavourable(List<BookMultiEntity> favourable) {
        this.favourable = favourable;
    }

    public List<HpSpecialEntity> getSpecial() {
        return special;
    }

    public void setSpecial(List<HpSpecialEntity> special) {
        this.special = special;
    }
}
