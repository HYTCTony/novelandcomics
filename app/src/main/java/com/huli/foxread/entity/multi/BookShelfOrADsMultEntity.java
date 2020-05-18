package com.huli.foxread.entity.multi;

import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.huli.page.model.bean.BookShelfListBean;

import java.io.Serializable;

/**
 * 书架书籍或者广告
 */
public class BookShelfOrADsMultEntity implements MultiItemEntity, Serializable {

    public static final int ITEM_ADD_BOOK = 0;
    public static final int DETAILED = 1;
    public static final int ITEM_ADS = 2;

    private int itemType;
    private BookShelfListBean book;
    private TTNativeExpressAd ads;


    public BookShelfOrADsMultEntity(int itemType, BookShelfListBean book, TTNativeExpressAd ads) {
        this.itemType = itemType;
        this.book = book;
        this.ads = ads;
    }

    public BookShelfListBean getBook() {
        return book;
    }

    public void setBook(BookShelfListBean book) {
        this.book = book;
    }

    public TTNativeExpressAd getAds() {
        return ads;
    }

    public void setAds(TTNativeExpressAd ads) {
        this.ads = ads;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    @Override
    public int getItemType() {
        return itemType;
    }
}
