package com.nnmedia.read.entity.multi;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.nnmedia.page.model.bean.BookShelfListBean;

import java.io.Serializable;

/**
 * 书架书籍或者广告
 */
public class BookShelfOrADsMultEntity implements MultiItemEntity, Serializable {

    public static final int ITEM_ADD_BOOK = 0;
    public static final int DETAILED = 1;
    public static final int TYPE_ADS_CSJ = 2;
    public static final int TYPE_ADS_GDT = 3;
    public static final int TYPE_ADS_BAIDU = 4;

    private int itemType;
    private BookShelfListBean book;
    private Object ads;


    public BookShelfOrADsMultEntity(int itemType, BookShelfListBean book, Object ads) {
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

    public Object getAds() {
        return ads;
    }

    public void setAds(Object ads) {
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
