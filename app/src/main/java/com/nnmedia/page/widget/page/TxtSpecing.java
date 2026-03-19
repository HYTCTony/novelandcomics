package com.nnmedia.page.widget.page;

/**
 * 作用：设置文章行间距/段落间距
 */
public enum TxtSpecing {
    SP_0(42, 32),
    SP_1(52, 40),
    SP_2(73, 56);

    private int titleInterval;
    private int textInterval;

    TxtSpecing(int titleInterval, int textInterval) {
        this.titleInterval = titleInterval;
        this.textInterval = textInterval;
    }

    public int getTitleInterval() {
        return titleInterval;
    }

    public int getTextInterval() {
        return textInterval;
    }
}