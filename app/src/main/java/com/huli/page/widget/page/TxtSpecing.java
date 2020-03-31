package com.huli.page.widget.page;

/**
 * 作用：设置文章行间距/段落间距
 */
public enum TxtSpecing {
    SP_0(40),
    SP_1(80),
    SP_2(120);

    private int specingSize;

    TxtSpecing(int specingSize) {
        this.specingSize = specingSize;
    }

    public int getSpecingSize() {
        return specingSize;
    }
}