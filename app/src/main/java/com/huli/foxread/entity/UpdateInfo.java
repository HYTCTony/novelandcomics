package com.huli.foxread.entity;

/**
 * 检测更新
 */

public class UpdateInfo {
    private int enforce;            //1强制更新， 2不强制
    private int version;            //当前版本号
    private int newversion;         //最新版本号
    private String downloadurl;     //下载地址
    private String packagesize;     //apk大小“50M”
    private String upgradetext;     //更新提示
    private String versionname;     //最新版本名

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

    public int getNewversion() {
        return newversion;
    }

    public void setNewversion(int newversion) {
        this.newversion = newversion;
    }

    public String getDownloadurl() {
        return downloadurl;
    }

    public void setDownloadurl(String downloadurl) {
        this.downloadurl = downloadurl;
    }

    public String getPackagesize() {
        return packagesize;
    }

    public void setPackagesize(String packagesize) {
        this.packagesize = packagesize;
    }

    public String getUpgradetext() {
        return upgradetext;
    }

    public void setUpgradetext(String upgradetext) {
        this.upgradetext = upgradetext;
    }

    public String getVersionname() {
        return versionname;
    }

    public void setVersionname(String versionname) {
        this.versionname = versionname;
    }
}
