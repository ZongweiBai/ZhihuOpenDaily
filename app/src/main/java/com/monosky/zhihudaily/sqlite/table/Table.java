package com.monosky.zhihudaily.sqlite.table;

import android.database.sqlite.SQLiteDatabase;

/**
 * Table接口
 * Created by jonez_000 on 2015/9/22.
 */
public interface Table {
    /**
     * 创建数据库的代码
     */
    void onCreate(SQLiteDatabase db);

    /**
     * 升级数据库的代码
     */
    void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
}
