package com.example.news.Util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.news.Gson.News;
import java.util.ArrayList;
import java.util.List;

public class UserDao{
    private SQLiteHelper sqLiteHelper;
    private SQLiteDatabase dbReader;
    private SQLiteDatabase dbWriter;
    //数据库语句
    private final String NAME = "News.db";
    private final String select_news = "select * from News where sort = ? order by time desc";
    private final String select_shoucang = "select distinct title, description, uri, picUrl, time from News_SHOUCANG where user_id = ? order by id desc";
    private final String select_shoucangbytitle = "select * from News_SHOUCANG where user_id = ? and title = ?";
    private final String insert_news = "insert into news(title, description, time, uri, picUrl, sort) values(?,?,?,?,?,?)";
    private final String insert_shoucang = "insert into News_SHOUCANG(title, description, time, uri, picUrl, sort, user_id) values(?,?,?,?,?,?,?)";
    private final String delete_shouchang = "delete from News_shoucang where user_id = ? and title = ?";
    private final String select_user = "select * from Users where id = ?";
    private final String add_user = "insert into Users(id, password) values(?, ?)";
    //新建数据库
    public void createDB(Context mContext) {
        sqLiteHelper = new SQLiteHelper(mContext, NAME, null, 1);
    }
    //数据库查找操作，根据新闻种类查找
    //在News表中查找
//    "create table News (" +
//            "title text primary key, " +
//            "description text, " +
//            "time text," +
//            "uri text," +
//            "picUrl text," +
//            "sort text)";
    public List<News> selectNews(String sort) {
        List<News> newsList = new ArrayList<News>();
        dbReader = sqLiteHelper.getReadableDatabase();
        Cursor cursor = dbReader.rawQuery(select_news,new String[] {sort});
        while(cursor.moveToNext()) {
            News news = new News();
            news.setCtime(cursor.getString(cursor.getColumnIndex("time")));
            news.setDescription(cursor.getString(cursor.getColumnIndex("description")));
            news.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            news.setPicUrl(cursor.getString(cursor.getColumnIndex("picUrl")));
            news.setUrl(cursor.getString(cursor.getColumnIndex("uri")));
            newsList.add(news);
        }
        return newsList;
    }
    //数据库查找，根据用户名查找
    //在news_shoucang中查找
    //"create table News_SHOUCANG (" +
    //            "title text primary key, " +
    //            "description text, " +
    //            "time text," +
    //            "uri text," +
    //            "picUrl text," +
    //            "sort text," +
    //            "user_id text)";
    public List<News> selectShouCang(String user_id) {
        List<News> newsList = new ArrayList<News>();
        dbReader = sqLiteHelper.getReadableDatabase();
        Cursor cursor = dbReader.rawQuery(select_shoucang, new String[]{user_id});
        while (cursor.moveToNext()) {
            News news = new News();
            news.setCtime(cursor.getString(cursor.getColumnIndex("time")));
            news.setDescription(cursor.getString(cursor.getColumnIndex("description")));
            news.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            news.setPicUrl(cursor.getString(cursor.getColumnIndex("picUrl")));
            news.setUrl(cursor.getString(cursor.getColumnIndex("uri")));
            newsList.add(news);
        }
        return newsList;
    }
    //根据标题和用户名查找News_SHOUCANG
    public boolean selectShouCangByTitle(String user_id, String title) {
        dbReader = sqLiteHelper.getReadableDatabase();
        Cursor cursor = dbReader.rawQuery(select_shoucangbytitle, new String[] {user_id, title});
        if(cursor.moveToNext())
            return true;
        return false;
    }
    //在数据库中增加记录
    //在news增加记录
//    "insert into news(title, description, time, uri, picUrl, sort) values(?,?,?,?,?,?)"
    public void addNews(List<News> list, String sort) {
        dbWriter = sqLiteHelper.getWritableDatabase();
        int i = 0;
        while(i < list.size()) {
            News news = list.get(i);
            Cursor cursor = dbWriter.rawQuery("select * from News where title = ?", new String[] {news.getTitle()});
            if(!cursor.moveToNext())
                dbWriter.execSQL(insert_news,new String[] {news.getTitle(), news.getDescription(), news.getCtime(), news.getUrl(), news.getPicUrl(), sort});
            i++;
        }
    }
    //在news_shoucang中添加记录
    //"insert into News_SHOUCANG(title, description, time, uri, picUrl, sort, user_id) values(?,?,?,?,?,?,?)"
    public void addShouCang(News news, String user_id, String sort) {
        dbWriter = sqLiteHelper.getWritableDatabase();
        dbWriter.execSQL(insert_shoucang, new String[] {news.getTitle(), news.getDescription(), news.getCtime(), news.getUrl(), news.getPicUrl(), sort, user_id});
    }
    //删除收藏
    public void deleteShouCang(String user_id, String title) {
        dbWriter = sqLiteHelper.getWritableDatabase();
        dbWriter.execSQL(delete_shouchang, new String[]{user_id, title});
    }
    //根据user_ids搜索Users表
    public Cursor selectUser(String user_id) {
        Cursor cursor;
        dbReader = sqLiteHelper.getReadableDatabase();
        cursor = dbReader.rawQuery(select_user, new String[] {user_id});
        return cursor;
    }
    //添加用户到Users
    public void addUser(String user_id, String password) {
        dbWriter = sqLiteHelper.getWritableDatabase();
        dbWriter.execSQL(add_user, new String[] {user_id, password});
    }
}
