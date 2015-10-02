package com.monosky.zhihudaily;

import java.io.Serializable;

/**
 * 保存一些常量
 * Created by jonez_000 on 2015/8/20.
 */
public class ConstData implements Serializable {

    // 默认模板路径
    public static final String TEMPLATE_DEF_URL = "www/template.html";

    public static Boolean isDebug = true;
    public static String TAG = "monoSkyZhiHuDaily";

    /**
     * SharePreference常量
     */
    public static String LOADING_IMAGE = "LOADING_IMAGE";
    public static String LOADING_TEXT = "LOADING_TEXT";
    public static String THEMES_DATA = "THEMES_DATA";
    public static String THEME_LIKE = "THEME_LIKE";
}
