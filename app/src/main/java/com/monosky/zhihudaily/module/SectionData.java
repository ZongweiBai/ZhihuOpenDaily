package com.monosky.zhihudaily.module;

import java.util.List;

/**
 * 栏目数据
 * Created by jonez_000 on 2015/9/21.
 */
public class SectionData {

    private String timestamp;
    private List<StoryData> storyDataList;
    private String name;

    public SectionData() {
    }

    public SectionData(String name, List<StoryData> storyDataList, String timestamp) {
        this.name = name;
        this.storyDataList = storyDataList;
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<StoryData> getStoryDataList() {
        return storyDataList;
    }

    public void setStoryDataList(List<StoryData> storyDataList) {
        this.storyDataList = storyDataList;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
