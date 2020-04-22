package com.example.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    // 数据库文件名
    public static final String DB_NAME = "account.db";
    // 数据库表名
    public static final String TABLE_NAME = "all_account";
    // 数据库版本号
    public static final int DB_VERSION = 1;

    public static final String TIME = "time";
    public static final String TYPE = "type";
    public static final String MONEY = "money";
    public static final String PERSON = "person";
    public static final String NOTE = "note";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // 当数据库文件创建时，执行初始化操作，并且只执行一次
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 建表
        String sql = "create table " +
                TABLE_NAME +
                "(_id integer primary key autoincrement, " +
                TIME + " varchar, " +
                TYPE + " integer, " +
                MONEY + " float, " +
                PERSON + " varchar, " +
                NOTE + " varchar"
                + ")";

        db.execSQL(sql);
    }

    // 当数据库版本更新执行该方法
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
