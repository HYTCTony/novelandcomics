package com.nnmedia.read.entity;

/**
 * 检测更新
 */
public class UpdateInfo {
    private int enforce;            //1强制更新， 2不强制
    private int version;            //当前版本号
    private String versionName;     //最新版本名
    private String downloadurl;     //下载地址
    private float packagesize;      //apk大小“50M”
    private String content;         //更新提示
    private long releaseTime;       //发布时间

    public int getEnforce() {
        return enforce;
    }

    public void setEnforce(int enforce) {
        this.enforce = enforce;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getDownloadurl() {
        return downloadurl;
    }

    public void setDownloadurl(String downloadurl) {
        this.downloadurl = downloadurl;
    }

    public float getPackagesize() {
        return packagesize;
    }

    public void setPackagesize(float packagesize) {
        this.packagesize = packagesize;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public long getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(long releaseTime) {
        this.releaseTime = releaseTime;
    }
}
