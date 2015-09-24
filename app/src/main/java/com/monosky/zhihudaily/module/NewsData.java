package com.monosky.zhihudaily.module;

import java.io.Serializable;
import java.util.List;

/**
 * 消息内容
 * Created by jonez_000 on 2015/9/4.
 */
public class NewsData implements Serializable {

    private String body;// HTML 格式的新闻
    private String imageSource;// 图片的内容提供方。为了避免被起诉非法使用图片，在显示图片时最好附上其版权信息。
    private String title;// 新闻标题
    private String image; // 获得的图片同 最新消息 获得的图片分辨率不同。这里获得的是在文章浏览界面中使用的大图。
    private String shareUrl;// 供在线查看内容与分享至 SNS 用的 URL
    private String js;// 供手机端的 WebView(UIWebView) 使用
    private String recommenders;// 这篇文章的推荐者
    private String gaPrefix;// 供 Google Analytics 使用
    private String sectionInfo; //栏目信息，格式如下：{"section_thumbnail": "http://pic4.zhimg.com/d91df748fa4cbdb02e8f6d6ca38a5dcf.jpg",  "section_id": 2, "section_name": "瞎扯"}
    private String type;// 新闻的类型
    private String id;// 新闻的 id
    private List<String> cssList; //供手机端的 WebView(UIWebView) 使用

    public NewsData() {
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<String> getCssList() {
        return cssList;
    }

    public void setCssList(List<String> cssList) {
        this.cssList = cssList;
    }

    public String getGaPrefix() {
        return gaPrefix;
    }

    public void setGaPrefix(String gaPrefix) {
        this.gaPrefix = gaPrefix;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImageSource() {
        return imageSource;
    }

    public void setImageSource(String imageSource) {
        this.imageSource = imageSource;
    }

    public String getJs() {
        return js;
    }

    public void setJs(String js) {
        this.js = js;
    }

    public String getRecommenders() {
        return recommenders;
    }

    public void setRecommenders(String recommenders) {
        this.recommenders = recommenders;
    }

    public String getSectionInfo() {
        return sectionInfo;
    }

    public void setSectionInfo(String sectionInfo) {
        this.sectionInfo = sectionInfo;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
