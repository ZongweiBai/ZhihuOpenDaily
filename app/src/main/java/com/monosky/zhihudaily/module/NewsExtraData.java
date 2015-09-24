package com.monosky.zhihudaily.module;

import java.io.Serializable;

/**
 * 文章额外信息
 * Created by jonez_000 on 2015/9/9.
 */
public class NewsExtraData implements Serializable {

    private int popularity;  // 点赞数
    private int comments;    // 评论总数
    private int longComments;    // 长评总数
    private int shortComments;   // 短评总数

    public NewsExtraData() {
    }

    public NewsExtraData(int comments, int longComments, int popularity, int shortComments) {
        this.comments = comments;
        this.longComments = longComments;
        this.popularity = popularity;
        this.shortComments = shortComments;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public int getLongComments() {
        return longComments;
    }

    public void setLongComments(int longComments) {
        this.longComments = longComments;
    }

    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public int getShortComments() {
        return shortComments;
    }

    public void setShortComments(int shortComments) {
        this.shortComments = shortComments;
    }
}
