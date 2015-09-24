package com.monosky.zhihudaily.sqlite;

import android.content.Context;

import com.monosky.zhihudaily.sqlite.table.NewsTable;
import com.monosky.zhihudaily.sqlite.table.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库操作类
 * Created by jonez_000 on 2015/9/22.
 */
public class DatabaseHelper extends AbstractDatabaseHelper {
    private static List<Table> tables = new ArrayList<Table>();

    /**
     * 将添加表的代码写在这里
     * */
    static {
        // Table列表
        tables.add(new NewsTable());
    }

    /**
     * @param context
     */
    public DatabaseHelper(Context context) {
        super(context);
    }

    @Override
    protected List<Table> getTables() {
        return tables;
    }
}
