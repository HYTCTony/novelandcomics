package com.huli.foxread.entity.base;

/**
 * 项目名称：FoxRead
 * 创建人：Bill
 * 创建时间：2020/7/18  18:00
 * 备注：
 */
public class TTPageList<T> {
    private String title;
    private PageList<T> list;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public PageList<T> getList() {
        return list;
    }

    public void setList(PageList<T> list) {
        this.list = list;
    }
}
