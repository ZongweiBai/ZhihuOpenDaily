package com.monosky.zhihudaily.sqlite.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.monosky.zhihudaily.module.NewsData;
import com.monosky.zhihudaily.sqlite.DatabaseHelper;
import com.monosky.zhihudaily.sqlite.table.NewsTable;
import com.monosky.zhihudaily.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 文章详情DAO
 * Created by jonez_000 on 2015/9/22.
 */
public class NewsDao {

    private DatabaseHelper dbHelper;//数据库的辅助类

    public NewsDao(Context context) {
        dbHelper = new DatabaseHelper(context);//构造一个数据库的辅助类
    }

    /**
     * 向数据库中添加一条记录
     *
     * @param values
     * @return
     */
    public int save(ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();//通过数据库的辅助类，来获取数据库访问类，
        Long id = db.insert(NewsTable.TNAME, null, values);
        db.close();//关闭数据库
        return id.intValue();

    }

    /**
     * 向数据库中删除一条记录
     *
     * @param id
     * @return
     */
    public int delete(String id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();//通过数据库的辅助类，来获取数据库访问类，
        int sum = db.delete(NewsTable.TNAME, "news_id=?", new String[]{id});
        db.close();
        return sum;
    }

    /**
     * 更新数据库中的某条记录
     *
     * @param values
     * @return
     */
    public int update(ContentValues values) {
        int effectNum = -1;
        SQLiteDatabase db = dbHelper.getWritableDatabase();// 通过数据库的辅助类，来获取数据库访问类，
        String id = values.getAsString("id");//取得用户传过来的id值
        /**更新操作
         * table：表
         * values：ContentValues  内容值， 好比Hashmap  key：字段名  value ：字段值
         * whereClause：条件
         * whereArgs：条件参数
         */
        effectNum = db.update(NewsTable.TNAME, values, "news_id=?", new String[]{id});
        db.close();
        return effectNum;
    }


    /**
     * 事务操作
     *
     * @param from :从哪个id来
     * @param to   ：到哪个id去
     */
    public void pay(int from, int to) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();// 通过数据库的辅助类，来获取数据库访问类
        try {
            db.beginTransaction();//开始事务
            //具体的数据库操作写在该处

            db.setTransactionSuccessful();//设置事务为成功，默认为假
        } finally {
            db.endTransaction();//结束事务
            db.close();
        }
    }

    /**
     * 保存文章内容
     *
     * @param newsData
     */
    public void saveNewsData(NewsData newsData) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();// 通过数据库的辅助类，来获取数据库访问类
        ContentValues values = new ContentValues();
        values.put("news_id", newsData.getId());
        values.put("title", newsData.getTitle());
        values.put("image", newsData.getImage());
        values.put("body", newsData.getBody());
        values.put("imageSource", newsData.getImageSource());
        values.put("shareUrl", newsData.getShareUrl());
        values.put("recommenders", newsData.getRecommenders());
        values.put("sectionInfo", newsData.getSectionInfo());
        values.put("type", newsData.getType());
        save(values);
        LogUtils.i("save news info success");
        db.close();
    }

    /**
     * 根据ID获取文章内容
     *
     * @param newsId
     * @return
     */
    public NewsData getNewsDataById(String newsId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();// 通过数据库的辅助类，来获取数据库访问类
        List<NewsData> list = new ArrayList<>();
        NewsData newsData;
        String sql = " select * from " + NewsTable.TNAME + " where news_id = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{newsId});
        while (cursor.moveToNext()) {
            newsData = new NewsData();
            newsData.setId(cursor.getString(cursor.getColumnIndex("news_id")));
            newsData.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            newsData.setImage(cursor.getString(cursor.getColumnIndex("image")));
            newsData.setBody(cursor.getString(cursor.getColumnIndex("body")));
            newsData.setImageSource(cursor.getString(cursor.getColumnIndex("imageSource")));
            newsData.setShareUrl(cursor.getString(cursor.getColumnIndex("shareUrl")));
            newsData.setRecommenders(cursor.getString(cursor.getColumnIndex("recommenders")));
            newsData.setSectionInfo(cursor.getString(cursor.getColumnIndex("sectionInfo")));
            newsData.setType(cursor.getString(cursor.getColumnIndex("type")));
            list.add(newsData);
        }
        cursor.close();
        db.close();
        if(list!=null && !list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }
}
