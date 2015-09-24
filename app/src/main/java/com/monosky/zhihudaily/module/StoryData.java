package com.monosky.zhihudaily.module;

import java.io.Serializable;

/**
 * 文章实体类
 * Created by jonez_000 on 2015/8/28.
 */
public class StoryData implements Serializable {

    private String title;   //标题
    private String storyId; //id
    private String image;   //图片
    private Boolean multipic;//是否多图
    private String type;    //作用未知
    private ThemeData themeData;    // 所属主题
    private String ga_prefix;//供 Google Analytics 使用
    private String time;    // 时间

    public StoryData() {
    }

    public StoryData(String title) {
        this.title = title;
    }

    public StoryData(String title, String storyId, Boolean multipic) {
        this.multipic = multipic;
        this.storyId = storyId;
        this.title = title;
    }

    public StoryData(String title, String storyId, String image, Boolean multipic) {
        this.title = title;
        this.storyId = storyId;
        this.image = image;
        this.multipic = multipic;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStoryId() {
        return storyId;
    }

    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Boolean getMultipic() {
        return multipic;
    }

    public void setMultipic(Boolean multipic) {
        this.multipic = multipic;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGa_prefix() {
        return ga_prefix;
    }

    public void setGa_prefix(String ga_prefix) {
        this.ga_prefix = ga_prefix;
    }

    public ThemeData getThemeData() {
        return themeData;
    }

    public void setThemeData(ThemeData themeData) {
        this.themeData = themeData;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
