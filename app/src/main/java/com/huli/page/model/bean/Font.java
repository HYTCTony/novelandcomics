package com.huli.page.model.bean;

public class Font {
    //是否选中
    private boolean isSelect;

    private String fontName;

    public Font(String fontName) {
        this.fontName = fontName;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }
}
