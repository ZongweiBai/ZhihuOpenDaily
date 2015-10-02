package com.monosky.zhihudaily;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.monosky.zhihudaily.http.HttpStartImageGet;
import com.monosky.zhihudaily.http.HttpThemesGet;
import com.monosky.zhihudaily.module.ThemeData;
import com.monosky.zhihudaily.util.SPUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Application基类
 * Created by jonez_000 on 2015/8/16.
 */
public class BaseApplication extends Application {

    private static BaseApplication baseApplicationInstance;
    private static Context mContext;

    public static Map<String, Bitmap> tempImgMap = new HashMap<>();
    public static List<String> themeLikes = new ArrayList<>();

    /**
     * 主题排序
     */
    public static List<ThemeData> themesDatas;
    public static Map<String, ThemeData> themeDataMap = new HashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();
        baseApplicationInstance = this;

        //创建默认的ImageLoader配置参数
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);
        //Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(configuration);

        mContext = getApplicationContext();

        // 获取APP Loading页
        new HttpStartImageGet().startImageDataGet();
        // 获取主题信息
        new HttpThemesGet().startThemsDatasTask();

        // 获取关注的主题
        getLikeThemes();
    }

    private void getLikeThemes() {
        String themesLikeStr = (String) SPUtils.get(ConstData.THEME_LIKE, "");
        if(!TextUtils.isEmpty(themesLikeStr)) {
            try {
                String[] themeArr = themesLikeStr.split(",");
                for (String theme : themeArr) {
                    themeLikes.add(theme);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static Context getContext() {
        return mContext;
    }

    public static BaseApplication getInstance() {
        return baseApplicationInstance;
    }
}
