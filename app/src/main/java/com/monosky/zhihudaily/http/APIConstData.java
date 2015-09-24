package com.monosky.zhihudaily.http;

/**
 * API访问常量
 * Created by jonez_000 on 2015/8/28.
 */
public class APIConstData {

    /**
     * 启动界面图像获取
     * start-image 后为图像分辨率，接受如下格式
     * 320*432
     * 480*728
     * 720*1184
     * 1080*1776
     */
    public static String START_IMAGE = "http://news-at.zhihu.com/api/4/start-image/1080*1776";

    /**
     * 软件版本查询
     * URL 最后部分的数字代表所安装『知乎日报』的版本
     */
    public static String VERSION = "http://news-at.zhihu.com/api/4/version/android/2.3.0";

    /**
     * 首页消息
     */
    public static String LASTEST = "http://news-at.zhihu.com/api/4/news/latest";

    /**
     * 首页历史消息查询
     * 如果需要查询 11 月 18 日的消息，before 后的数字应为 20131119
     * http://news.at.zhihu.com/api/4/news/before/20131119
     */
    public static String BEFORE = "http://news.at.zhihu.com/api/4/news/before/";

    /**
     * 主题日报列表查看
     */
    public static String THEMES = "http://news-at.zhihu.com/api/4/themes";

    /**
     * 主题日报内容查询
     * id为 THEMES接口中获取的ID， 如果要查询后面的，增加/before/lastId lastId为 THEME_LIST最后获取的ID
     * http://news-at.zhihu.com/api/4/theme/#{id}/before/#{lastId}
     */
    public static String THEME_LIST = "http://news-at.zhihu.com/api/4/theme/";

    /**
     * 消息内容获取与离线下载
     * URL: http://news-at.zhihu.com/api/4/news/3892357
     * 使用在 最新消息 中获得的 id，拼接在 http://news-at.zhihu.com/api/4/news/ 后，得到对应消息 JSON 格式的内容
     */
    public static String NEWS_GET = "http://news-at.zhihu.com/api/4/news/";

    /**
     * 新闻额外信息
     * URL: http://news-at.zhihu.com/api/4/story-extra/7104613
     * 输入新闻的ID，获取对应新闻的额外信息，如评论数量，所获的『赞』的数量。
     */
    public static String STORY_EXTRA = "http://news-at.zhihu.com/api/4/story-extra/";

    /**
     * 新闻对应评论查看
     * newsId:新闻ID
     * type:long-comments长评论 short-comments短评论
     * id:如果第一页加载不完，使用id来分页加载，id见JSON 数据末端的 id 属性
     * http://news-at.zhihu.com/api/4/story/#{newsId}/#{type}/before/#{id}
     */
    public static String NEWS_COMMENTS = "http://news-at.zhihu.com/api/4/story/";
    public static String COMMENTS_LONG = "long-comments";
    public static String COMMENTS_SHORT = "short-comments";

    /**
     * 栏目具体消息查看
     * id：具体栏目的ID
     * timestamp：如果需要翻页，需要的时间戳参数，从上一个请求中获取
     * http://news-at.zhihu.com/api/3/section/#{id}/before/#{timestamp}
     */
    public static String SECTION = "http://news-at.zhihu.com/api/3/section/";

    /**
     * 主编详细资料查看
     * id：主编唯一ID
     * http://news-at.zhihu.com/api/4/editor/#{id}/profile-page/android
     */
    public static String EDITOR = "http://news-at.zhihu.com/api/4/editor/";

}
