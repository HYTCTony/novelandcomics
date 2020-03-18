package com.huli.foxread.entity;

import java.util.List;

public class HomePageBGEntity {
    private HpHotNovelET hot_novel;                         //男生|女生都喜欢
    private List<HpBGPraiseNvET> praise_novel;              //重磅推荐、好评佳作等
    private List<HpSpecialEntity> special;                          //专题

    public HpHotNovelET getHot_novel() {
        return hot_novel;
    }

    public void setHot_novel(HpHotNovelET hot_novel) {
        this.hot_novel = hot_novel;
    }

    public List<HpBGPraiseNvET> getPraise_novel() {
        return praise_novel;
    }

    public void setPraise_novel(List<HpBGPraiseNvET> praise_novel) {
        this.praise_novel = praise_novel;
    }

    public List<HpSpecialEntity> getSpecial() {
        return special;
    }

    public void setSpecial(List<HpSpecialEntity> special) {
        this.special = special;
    }
}
