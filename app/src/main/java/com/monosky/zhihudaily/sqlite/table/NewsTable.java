package com.monosky.zhihudaily.sqlite.table;

import android.database.sqlite.SQLiteDatabase;

/**
 * 文章表
 * Created by jonez_000 on 2015/9/22.
 */
public class NewsTable implements Table {

    /**
     * 表名
     */
    public static String TNAME = "t_news";

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TNAME
                + "(news_id text PRIMARY KEY, title text ,image text , body text ,"
                + "imageSource text , shareUrl text ,recommenders text ,sectionInfo text, type text)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
