package com.appstore.entity;

/**
 * Created by heyzqt on 2016/9/25.
 */
public class DownLoadInfo {

    private int id;
    private String appId;
    private String appname;
    private String packagename;
    private String status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getPackagename() {
        return packagename;
    }

    public void setPackagename(String packagename) {
        this.packagename = packagename;
    }

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    @Override
    public String toString() {
        return "DownLoadInfo{" +
                "id=" + id +
                ", appId='" + appId + '\'' +
                ", appname='" + appname + '\'' +
                ", packagename='" + packagename + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
