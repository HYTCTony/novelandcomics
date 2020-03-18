package com.huli.foxread.entity;

import java.io.Serializable;
import java.util.List;

public class HomePageEntity implements Serializable {
    private HpHotNovelET hot_novel;                         //高分精选
    private List<BookEntity> rank_novel;                    //今日大热榜
    private List<HpClassifyNvET> classify_novel;            //分类佳作
    private List<SearchEntity> search_novel;                  //实时热搜
    private List<HpNewBookET> new_or_original;              //新书抢先

    public HpHotNovelET getHot_novel() {
        return hot_novel;
    }

    public void setHot_novel(HpHotNovelET hot_novel) {
        this.hot_novel = hot_novel;
    }

    public List<BookEntity> getRank_novel() {
        return rank_novel;
    }

    public void setRank_novel(List<BookEntity> rank_novel) {
        this.rank_novel = rank_novel;
    }

    public List<HpClassifyNvET> getClassify_novel() {
        return classify_novel;
    }

    public void setClassify_novel(List<HpClassifyNvET> classify_novel) {
        this.classify_novel = classify_novel;
    }

    public List<SearchEntity> getSearch_novel() {
        return search_novel;
    }

    public void setSearch_novel(List<SearchEntity> search_novel) {
        this.search_novel = search_novel;
    }

    public List<HpNewBookET> getNew_or_original() {
        return new_or_original;
    }

    public void setNew_or_original(List<HpNewBookET> new_or_original) {
        this.new_or_original = new_or_original;
    }
}
