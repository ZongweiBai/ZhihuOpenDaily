package com.monosky.zhihudaily.module;

import java.io.Serializable;

/**
 * 主题日报的编辑信息
 * Created by jonez_000 on 2015/9/16.
 */
public class EditorData implements Serializable {

    private String url; //主编的知乎用户主页
    private String bio;//主编的个人简介
    private String id;//  数据库中的唯一表示符
    private String avatar;// 主编的头像
    private String name; // 主编的姓名

    public EditorData() {
    }

    public EditorData(String url, String name, String id, String bio, String avatar) {
        this.url = url;
        this.bio = bio;
        this.id = id;
        this.avatar = avatar;
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
