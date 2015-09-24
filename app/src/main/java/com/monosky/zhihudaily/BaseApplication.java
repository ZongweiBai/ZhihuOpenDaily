package com.monosky.zhihudaily;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import com.monosky.zhihudaily.http.HttpStartImageGet;
import com.monosky.zhihudaily.http.HttpThemesGet;
import com.monosky.zhihudaily.module.ThemeData;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

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

    /**
     * 主题排序
     */
    public static List<ThemeData> themesDatas;

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
    }

    public static Context getContext() {
        return mContext;
    }

    public static BaseApplication getInstance() {
        return baseApplicationInstance;
    }
}
