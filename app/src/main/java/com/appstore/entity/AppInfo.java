package com.appstore.entity;

import java.util.Arrays;

/**
 * Created by 张艳琴 on 2016/9/19.
 */
public class AppInfo {

    private int id;
    private String name;
    private String packageName;
    private String iconUrl;
    private String stars;
    private String downLoadNum;
    private String vesion;
    private String date;
    private String size;
    private String downloadUrl;
    private String des;
    private String author;
    private String[] screen;

    public String[] getScreen() {
        return screen;
    }

    public void setScreen(String[] screen) {
        this.screen = screen;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getVesion() {
        return vesion;
    }

    public void setVesion(String vesion) {
        this.vesion = vesion;
    }

    public String getDownLoadNum() {
        return downLoadNum;
    }

    public void setDownLoadNum(String downLoadNum) {
        this.downLoadNum = downLoadNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getStars() {
        return stars;
    }

    public void setStars(String stars) {
        this.stars = stars;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", packageName='" + packageName + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                ", stars='" + stars + '\'' +
                ", downLoadNum='" + downLoadNum + '\'' +
                ", vesion='" + vesion + '\'' +
                ", date='" + date + '\'' +
                ", size='" + size + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", des='" + des + '\'' +
                ", author='" + author + '\'' +
                ", screen=" + Arrays.toString(screen) +
                '}';
    }
}
