package com.monosky.zhihudaily.module;

import java.io.Serializable;

/**
 * 文章评论
 * Created by jonez_000 on 2015/9/12.
 */
public class CommentData implements Serializable {

    private String author;  // 评论者
    private String content; // 评论内容
    private String avatar;  // 评论者头像
    private String time;    // 评论时间（毫秒数）
    private String id;      // 评论ID
    private String likes = "0";   // 评论点赞数
    private CommentData replyComment;    // 引用的评论

    public CommentData() {
    }

    public CommentData(String author, String content, String id) {
        this.author = author;
        this.content = content;
        this.id = id;
    }

    public CommentData(String author, String avatar, String content, String id, String likes, String time) {
        this.author = author;
        this.avatar = avatar;
        this.content = content;
        this.id = id;
        this.likes = likes;
        this.time = time;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public CommentData getReplyComment() {
        return replyComment;
    }

    public void setReplyComment(CommentData replyComment) {
        this.replyComment = replyComment;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
