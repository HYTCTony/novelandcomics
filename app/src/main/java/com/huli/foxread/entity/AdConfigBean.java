package com.huli.foxread.entity;

/**
 * 项目名称：FoxRead
 * 创建人：Bill
 * 创建时间：2020/7/2  14:57
 * 备注：不同平台广告出现几率配置
 * 如：baidu:0,gdt:1,csj:1（空:停用广告位）
 */
public class AdConfigBean {
    private String open_screen;         //开屏
    private String turn_page;           //翻页
    private String banner;              //轮播图
    private String information_flow;    //信息流
    private String bonuses;             //得奖币
    private String advert;              //免广告

    public String getOpen_screen() {
        return open_screen;
    }

    public void setOpen_screen(String open_screen) {
        this.open_screen = open_screen;
    }

    public String getTurn_page() {
        return turn_page;
    }

    public void setTurn_page(String turn_page) {
        this.turn_page = turn_page;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getInformation_flow() {
        return information_flow;
    }

    public void setInformation_flow(String information_flow) {
        this.information_flow = information_flow;
    }

    public String getBonuses() {
        return bonuses;
    }

    public void setBonuses(String bonuses) {
        this.bonuses = bonuses;
    }

    public String getAdvert() {
        return advert;
    }

    public void setAdvert(String advert) {
        this.advert = advert;
    }
}
