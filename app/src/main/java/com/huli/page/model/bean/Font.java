package com.huli.page.model.bean;

public class Font {

    private String name;
    private String file_name;
    private String file_size;
    private String fontPath;
    private String http_file;
    private String http_image;

    //是否选中
    private boolean isSelect;
    //是否下载
    private boolean isDownload;
    private int progress;
    private long currentSize;
    private long totalSize;
    private int state; //0=未开始 1=等待中 2=下载中 3=暂停中 4=已完成  5=下载失败 6=已取消

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getName() {
        return name;
    }

    public void setName(String fontName) {
        this.name = fontName;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getFile_size() {
        return file_size;
    }

    public void setFile_size(String file_size) {
        this.file_size = file_size;
    }

    public String getFontPath() {
        return fontPath;
    }

    public void setFontPath(String fontPath) {
        this.fontPath = fontPath;
    }

    public String getHttp_file() {
        return http_file;
    }

    public void setHttp_file(String http_file) {
        this.http_file = http_file;
    }

    public String getHttp_image() {
        return http_image;
    }

    public void setHttp_image(String http_image) {
        this.http_image = http_image;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public long getCurrentSize() {
        return currentSize;
    }

    public void setCurrentSize(long currentSize) {
        this.currentSize = currentSize;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public boolean isDownload() {
        return isDownload;
    }

    public void setDownload(boolean download) {
        isDownload = download;
    }
}