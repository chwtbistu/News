package com.example.news.Util;

import com.example.news.Gson.NewsList;
import com.google.gson.Gson;

public class Utility {
    //使用Gson解析获取的json数据
    public static NewsList parseJsonWithGson(final String requestText) {
        Gson gson = new Gson();
        return gson.fromJson(requestText, NewsList.class);
    }
}
