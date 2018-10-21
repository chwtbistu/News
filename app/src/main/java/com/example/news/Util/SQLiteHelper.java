package com.example.news.Util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.io.Serializable;

public class SQLiteHelper extends SQLiteOpenHelper{
    //数据库建表语句--News
    public static final String CREATE_NEWS = "create table News (" +
            "title text primary key, " +
            "description text, " +
            "time text," +
            "uri text," +
            "picUrl text," +
            "sort text)";
    //数据库建表语句--News_SHOUCANG
    public static final String CREATE_SHOUCANG = "create table News_SHOUCANG (" +
            "id integer primary key autoincrement," +
            "title text , " +
            "description text, " +
            "time text," +
            "uri text," +
            "picUrl text," +
            "sort text," +
            "user_id text)";
    //数据库建表语句--Users
    public static final String CREATE_USERS = "create table Users(" +
            "id text primary key," +
            "password text)";
    private Context mContext;
    public SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    //创建数据库
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_NEWS);
        db.execSQL(CREATE_SHOUCANG);
        db.execSQL(CREATE_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
