package com.huli.page.model.bean;

public class Font {
    //是否选中
    private boolean isSelect;

    private String fontName;
    private String fontPath;

    public Font(String fontName, String fontPath) {
        this.fontName = fontName;
        this.fontPath = fontPath;
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

    public String getFontPath() {
        return fontPath;
    }

    public void setFontPath(String fontPath) {
        this.fontPath = fontPath;
    }
}
