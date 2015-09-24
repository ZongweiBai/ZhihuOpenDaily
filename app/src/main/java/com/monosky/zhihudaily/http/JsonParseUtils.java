package com.monosky.zhihudaily.http;

import android.text.TextUtils;

import com.monosky.zhihudaily.module.CommentData;
import com.monosky.zhihudaily.module.EditorData;
import com.monosky.zhihudaily.module.NewsData;
import com.monosky.zhihudaily.module.SectionData;
import com.monosky.zhihudaily.module.StoryData;
import com.monosky.zhihudaily.module.ThemeData;
import com.monosky.zhihudaily.util.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 解析JSON数据
 * Created by jonez_000 on 2015/8/28.
 */
public class JsonParseUtils {

    /**
     * 解析主题数据
     *
     * @param jsonStr
     * @return
     */
    public static List<ThemeData> getThemeDatas(String jsonStr) {
        if (TextUtils.isEmpty(jsonStr)) {
            return null;
        }

        List<ThemeData> themeDatas = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            if (jsonObject != null && jsonObject.length() > 0) {
                JSONArray themsArr = jsonObject.optJSONArray("others");
                if (themsArr != null && themsArr.length() > 0) {
                    JSONObject otherObject = null;
                    for (int index = 0; index < themsArr.length(); index++) {
                        otherObject = themsArr.optJSONObject(index);
                        themeDatas.add(new ThemeData(otherObject.optString("id"),
                                otherObject.optString("name"), otherObject.optString("thumbnail"),
                                otherObject.optString("description"), otherObject.optInt("color")));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return themeDatas;
    }

    /**
     * 解析首页（今日热闻）其他日期数据
     *
     * @param jsonStr
     * @return
     */
    public static List<StoryData> getLatestStory(String jsonStr) {
        if (TextUtils.isEmpty(jsonStr)) {
            return null;
        }
        List<StoryData> storyDataList = null;
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            if (jsonObject == null || jsonObject.length() <= 0) {
                return null;
            }
            JSONArray todayArr = jsonObject.optJSONArray("stories");
            if (todayArr == null || todayArr.length() <= 0) {
                return null;
            }
            storyDataList = new ArrayList<>();

            String date = jsonObject.optString("date");
            if ((DateUtils.getDate()).equals(date)) {
                storyDataList.add(new StoryData("今日热闻"));
            } else {
                storyDataList.add(new StoryData(DateUtils.getFormatData(date)));
            }
            JSONObject todayJson = null;
            JSONArray imageArr = null;
            StoryData storyData = null;
            for (int index = 0; index < todayArr.length(); index++) {
                todayJson = todayArr.optJSONObject(index);
                storyData = new StoryData(todayJson.optString("title"), todayJson.optString("id"),
                        todayJson.optBoolean("multipic", false));
                imageArr = todayJson.optJSONArray("images");
                if (imageArr != null && imageArr.length() > 0) {
                    storyData.setImage(imageArr.optString(0));
                }
                JSONObject themeJson = todayJson.optJSONObject("theme");
                if (themeJson != null && themeJson.length() > 0) {
                    storyData.setThemeData(new ThemeData(themeJson.optString("id"), themeJson.optString("name")));
                }
                storyDataList.add(storyData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return storyDataList;
    }

    /**
     * 解析首页（今日热闻）的顶部数据
     *
     * @param jsonStr
     * @return
     */
    public static List<StoryData> getTopStorys(String jsonStr) {
        if (TextUtils.isEmpty(jsonStr)) {
            return null;
        }
        List<StoryData> storyDataList = null;
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            if (jsonObject == null || jsonObject.length() <= 0) {
                return null;
            }
            JSONArray todayArr = jsonObject.optJSONArray("top_stories");
            if (todayArr == null || todayArr.length() <= 0) {
                return null;
            }
            storyDataList = new ArrayList<>();

            JSONObject todayJson = null;
            StoryData storyData = null;
            for (int index = 0; index < todayArr.length(); index++) {
                todayJson = todayArr.optJSONObject(index);
                storyData = new StoryData(todayJson.optString("title"), todayJson.optString("id"),
                        todayJson.optString("image"), todayJson.optBoolean("multipic", false));
                JSONObject themeJson = todayJson.optJSONObject("theme");
                if (themeJson != null && themeJson.length() > 0) {
                    storyData.setThemeData(new ThemeData(themeJson.optString("id"), themeJson.optString("name")));
                }
                storyDataList.add(storyData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return storyDataList;
    }

    /**
     * 解析日报新闻内容
     *
     * @param jsonStr
     * @return
     */
    public static NewsData getNewsData(String jsonStr) {
        if (TextUtils.isEmpty(jsonStr)) {
            return null;
        }
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            if (jsonObject == null || jsonObject.length() <= 0) {
                return null;
            }
            NewsData newsData = new NewsData();
            newsData.setBody(jsonObject.optString("body"));
            newsData.setImageSource(jsonObject.optString("image_source"));
            newsData.setTitle(jsonObject.optString("title"));
            newsData.setImage(jsonObject.optString("image"));
            newsData.setShareUrl(jsonObject.optString("share_url"));
            newsData.setGaPrefix(jsonObject.optString("ga_prefix"));
            newsData.setType(jsonObject.optString("type"));
            newsData.setId(jsonObject.optString("id"));
            JSONArray cssArr = jsonObject.optJSONArray("css");
            if (cssArr != null && cssArr.length() > 0) {
                List<String> cssList = new ArrayList<>();
                for (int i = 0; i < cssArr.length(); i++) {
                    cssList.add(cssArr.optString(i));
                }
                newsData.setCssList(cssList);
            }

            JSONObject sectinJson = jsonObject.optJSONObject("section");
            if (sectinJson != null && sectinJson.length() > 0) {
                JSONObject sectionInfo = new JSONObject();
                sectionInfo.put("section_thumbnail", sectinJson.optString("thumbnail"));
                sectionInfo.put("section_id", sectinJson.optString("id"));
                sectionInfo.put("section_name", sectinJson.optString("name"));

                newsData.setSectionInfo(sectionInfo.toString());
            }

            return newsData;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解析评论数据
     *
     * @param jsonStr
     * @return
     */
    public static List<CommentData> getCommentDatas(String jsonStr) {
        if (TextUtils.isEmpty(jsonStr)) {
            return null;
        }
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            if (jsonObject != null && jsonObject.length() > 0) {
                List<CommentData> commentDatas = new ArrayList<>();
                JSONArray jsonArray = jsonObject.optJSONArray("comments");
                JSONObject commentJson = null;
                CommentData commentData = null;
                JSONObject replyToJson = null;
                for (int index = 0; index < jsonArray.length(); index++) {
                    commentJson = jsonArray.optJSONObject(index);
                    commentData = new CommentData();
                    commentData.setId(commentJson.optString("id"));
                    commentData.setAuthor(commentJson.optString("author"));
                    commentData.setAvatar(commentJson.optString("avatar"));
                    commentData.setContent(commentJson.optString("content"));
                    commentData.setLikes(commentJson.optString("likes"));
                    commentData.setTime(commentJson.optString("time"));

                    replyToJson = commentJson.optJSONObject("reply_to");
                    if (replyToJson != null && replyToJson.length() > 0) {
                        commentData.setReplyComment(new CommentData(replyToJson.optString("author"),
                                replyToJson.optString("content"), replyToJson.optString("id")));
                    }
                    commentDatas.add(commentData);
                }
                return commentDatas;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取主题日报的文章内容
     *
     * @param jsonStr
     * @return
     */
    public static List<StoryData> getThemeStoryDatas(String jsonStr) {
        if (TextUtils.isEmpty(jsonStr)) {
            return null;
        }
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            if (jsonObject == null || jsonObject.length() <= 0) {
                return null;
            }
            JSONArray storyArr = jsonObject.optJSONArray("stories");
            if (storyArr == null || storyArr.length() <= 0) {
                return null;
            }

            List<StoryData> storyDataList = new ArrayList<>();
            JSONObject storyJson = null;
            StoryData storyData = null;
            JSONArray imageArr = null;
            for (int index = 0; index < storyArr.length(); index++) {
                storyJson = storyArr.optJSONObject(index);
                storyData = new StoryData();
                storyData.setType(storyJson.optString("type"));
                storyData.setStoryId(storyJson.optString("id"));
                storyData.setTitle(storyJson.optString("title"));
                imageArr = storyJson.optJSONArray("images");
                if (imageArr != null && imageArr.length() > 0) {
                    storyData.setImage(imageArr.optString(0));
                }

                storyDataList.add(storyData);
            }
            return storyDataList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取主题日报的主编信息
     *
     * @param jsonStr
     * @return
     */
    public static List<EditorData> getThemeEditorDatas(String jsonStr) {
        if (TextUtils.isEmpty(jsonStr)) {
            return null;
        }
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            if (jsonObject == null || jsonObject.length() <= 0) {
                return null;
            }
            JSONArray editorArr = jsonObject.optJSONArray("editors");
            if (editorArr == null || editorArr.length() <= 0) {
                return null;
            }

            List<EditorData> editorDataList = new ArrayList<>();
            JSONObject editorJson = null;
            EditorData editorData = null;
            for (int index = 0; index < editorArr.length(); index++) {
                editorJson = editorArr.optJSONObject(index);
                editorData = new EditorData();
                editorData.setUrl(editorJson.optString("url"));
                editorData.setId(editorJson.optString("id"));
                editorData.setBio(editorJson.optString("bio"));
                editorData.setAvatar(editorJson.optString("avatar"));
                editorData.setName(editorJson.optString("name"));

                editorDataList.add(editorData);
            }
            return editorDataList;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取主题日报的基本信息
     *
     * @param jsonStr
     * @return
     */
    public static ThemeData getThemeBaseData(String jsonStr) {
        if (TextUtils.isEmpty(jsonStr)) {
            return null;
        }
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            if (jsonObject == null || jsonObject.length() <= 0) {
                return null;
            }

            ThemeData themeData = new ThemeData();
            themeData.setDescription(jsonObject.optString("description"));
            themeData.setThumbnail(jsonObject.optString("background"));

            return themeData;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解析栏目数据
     *
     * @param jsonStr
     * @return
     */
    public static SectionData getSectionData(String jsonStr) {
        if (TextUtils.isEmpty(jsonStr)) {
            return null;
        }
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            if (jsonObject == null || jsonObject.length() <= 0) {
                return null;
            }
            SectionData sectionData = new SectionData();
            List<StoryData> storyDataList = new ArrayList<>();
            sectionData.setName(jsonObject.optString("name"));
            sectionData.setTimestamp(jsonObject.optString("timestamp"));

            JSONArray storyArr = jsonObject.optJSONArray("stories");
            if (storyArr != null && storyArr.length() > 0) {
                JSONObject todayJson = null;
                StoryData storyData = null;
                JSONArray imageArr = null;
                for (int index = 0; index < storyArr.length(); index++) {
                    todayJson = storyArr.optJSONObject(index);
                    storyData = new StoryData(todayJson.optString("title"), todayJson.optString("id"),
                            todayJson.optBoolean("multipic", false));
                    imageArr = todayJson.optJSONArray("images");
                    if (imageArr != null && imageArr.length() > 0) {
                        storyData.setImage(imageArr.optString(0));
                    }
                    storyData.setTime(todayJson.optString("display_date"));
                    storyDataList.add(storyData);
                }
            }
            sectionData.setStoryDataList(storyDataList);
            return sectionData;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
