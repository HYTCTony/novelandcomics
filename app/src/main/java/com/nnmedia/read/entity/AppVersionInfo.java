package com.nnmedia.read.entity;

/**
 * 版本详情
 */
public class AppVersionInfo {
    private UpdateInfo nowVersion;
    private UpdateInfo newVersion;

    public UpdateInfo getNowVersion() {
        return nowVersion;
    }

    public void setNowVersion(UpdateInfo nowVersion) {
        this.nowVersion = nowVersion;
    }

    public UpdateInfo getNewVersion() {
        return newVersion;
    }

    public void setNewVersion(UpdateInfo newVersion) {
        this.newVersion = newVersion;
    }
}
