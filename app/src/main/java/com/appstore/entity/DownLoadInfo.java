package com.appstore.entity;

/**
 * Created by heyzqt on 2016/9/25.
 */
public class DownLoadInfo {

    private int id;
    private String appId;
    private String packagename;
    private int pos;
    private int status;

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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    @Override
    public String toString() {
        return "DownLoadInfo{" +
                "id=" + id +
                ", appId='" + appId + '\'' +
                ", packagename='" + packagename + '\'' +
                ", pos=" + pos +
                ", status=" + status +
                '}';
    }
}
