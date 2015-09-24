package com.monosky.zhihudaily.module;

import java.io.Serializable;

/**
 * 日报主题列表查询
 * Created by jonez_000 on 2015/8/28.
 */
public class ThemeData implements Serializable {

    private String themeId;     //主题ID
    private String themeName;   //主题名称
    private String thumbnail;   //主题图片介绍
    private String description; //主题描述
    private int color;  // 颜色（不知道做什么用）
    private String imageSource; // 主题图片版权信息

    public ThemeData() {
    }

    public ThemeData(String themeId, String themeName) {
        this.themeId = themeId;
        this.themeName = themeName;
    }

    public ThemeData(String themeId, String themeName, String thumbnail, String description, int color) {
        this.themeId = themeId;
        this.themeName = themeName;
        this.thumbnail = thumbnail;
        this.description = description;
        this.color = color;
    }

    public String getThemeId() {
        return themeId;
    }

    public void setThemeId(String themeId) {
        this.themeId = themeId;
    }

    public String getThemeName() {
        return themeName;
    }

    public void setThemeName(String themeName) {
        this.themeName = themeName;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getImageSource() {
        return imageSource;
    }

    public void setImageSource(String imageSource) {
        this.imageSource = imageSource;
    }
}
