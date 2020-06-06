package com.huli.page.widget.page;

/**
 * 作用：设置文章行间距/段落间距
 */
public enum TxtSpecing {
    SP_0(47, 36),
    SP_1(55, 42),
    SP_2(83, 64);

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