package com.huli.page.widget.page;

/**
 * 作用：设置文章行间距/段落间距
 */
public enum TxtSpecing {
    SP_0(54, 36),
    SP_1(63, 42),
    SP_2(96, 64);

    private int textPara;
    private int textInterval;

    TxtSpecing(int textPara, int textInterval) {
        this.textPara = textPara;
        this.textInterval = textInterval;
    }

    public int getTextPara() {
        return textPara;
    }

    public int getTextInterval() {
        return textInterval;
    }
}