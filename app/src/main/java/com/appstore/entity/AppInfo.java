package com.appstore.entity;

/**
 * Created by 张艳琴 on 2016/9/19.
 */
public class AppInfo {

    private String name;
    private int id;

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

    @Override
    public String toString() {
        return "AppInfo{" +
                "name='" + name + '\'' +
                ", id=" + id +
                '}';
    }
}
