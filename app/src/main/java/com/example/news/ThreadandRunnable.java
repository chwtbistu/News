package com.example.news;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.ListView;

import com.example.news.Gson.News;
import com.example.news.Gson.NewsList;
import com.example.news.Util.HttpUtil;
import com.example.news.Util.UserDao;
import com.example.news.Util.Utility;

import java.util.List;

//创建子线程获取json数据
public class ThreadandRunnable {
    private final static String httpUrl = "http://api.tianapi.com/";
    private final static String APIKEY = "/?key=51577d0fae9b107351d70e8ed3a8c54b";
    private List<News> news;
    private String jsonResult="";
    private Context mContext;
    private ListView listView;
    private Handler handler;
    private String sort;
    private UserDao userDao;
    private boolean Interr = true;
    public ThreadandRunnable(Context mContext, ListView listView, Handler handler, UserDao userDao, String str) {
        super();
        this.mContext = mContext;
        this.listView = listView;
        this.handler = handler;
        this.sort = str;
        this.userDao = userDao;
    }
    public List<News> ThreadandRunnable() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (Interr) {
                    jsonResult = HttpUtil.request(httpUrl + sort + APIKEY, "num=10");
                    handler.post(runnable);
                    Log.d("MYTAG", jsonResult);
                    Interr = false; //获取json数据后终止线程
                }
            }
        }).start();
        return news;
    }
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //解析获取的json数据
            NewsList newsList = Utility.parseJsonWithGson(jsonResult);
            //将获取的数据添加到数据库总
            userDao.addNews(newsList.getNewslist(), sort);
            //从数据库中检索
            news = userDao.selectNews(sort);
            QueryAdapter queryAdapter = new QueryAdapter(mContext, news);
            listView.setAdapter(queryAdapter);//设置listView的Adapter
        }
    };
    public List<News> getList() {
        return userDao.selectNews(sort);
    }
}
