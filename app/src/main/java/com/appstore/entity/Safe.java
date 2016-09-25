package com.appstore.entity;

/**
 * Created by 张艳琴 on 2016/9/22.
 */
public class Safe {
    private String safeUrl;
    private String safeDesUrl;
    private String safeDes;
    private String safeDesColor;
    private String appid;

    public String getSafeUrl() {
        return safeUrl;
    }

    public void setSafeUrl(String safeUrl) {
        this.safeUrl = safeUrl;
    }

    public String getSafeDesUrl() {
        return safeDesUrl;
    }

    public void setSafeDesUrl(String safeDesUrl) {
        this.safeDesUrl = safeDesUrl;
    }

    public String getSafeDes() {
        return safeDes;
    }

    public void setSafeDes(String safeDes) {
        this.safeDes = safeDes;
    }

    public String getSafeDesColor() {
        return safeDesColor;
    }

    public void setSafeDesColor(String safeDesColor) {
        this.safeDesColor = safeDesColor;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    @Override
    public String toString() {
        return "Safe{" +
                "safeUrl='" + safeUrl + '\'' +
                ", safeDesUrl='" + safeDesUrl + '\'' +
                ", safeDes='" + safeDes + '\'' +
                ", safeDesColor='" + safeDesColor + '\'' +
                ", appid='" + appid + '\'' +
                '}';
    }
}
