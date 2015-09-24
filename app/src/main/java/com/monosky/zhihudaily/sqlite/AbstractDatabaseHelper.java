package com.monosky.zhihudaily.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.monosky.zhihudaily.sqlite.table.Table;

import java.util.List;

/**
 * 抽象数据库操作类
 * Created by jonez_000 on 2015/9/22.
 */
public abstract class AbstractDatabaseHelper extends SQLiteOpenHelper {

    //数据库名称
    public static final String DB_NAME = "zhihudaily.db";

    //数据库版本号
    public static final int VERSION = 1;

    //数据库表list
    private final List<Table> tableList;

    public AbstractDatabaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
        this.tableList = getTables();
    }

    /**
     * 获取需要加入到数据库中的表的实例
     */
    protected abstract List<Table> getTables();

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (Table table : tableList) {
            table.onCreate(db);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (Table table : tableList) {
            table.onUpgrade(db, oldVersion, newVersion);
        }
    }
}
