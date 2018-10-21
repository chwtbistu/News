package com.example.news.activity;


import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.example.news.Gson.News;
import com.example.news.R;
import com.example.news.Util.UserDao;
//显示新闻详情页面
public class des_activity extends AppCompatActivity {
    //注册组件和对象
    private WebView webView;
    private News news;
    private Button button;
    private UserDao userDao;
    private String user_id;
    private String sort;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_des_activity);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        button = (Button)findViewById(R.id.shoucang);
        news = (News)getIntent().getSerializableExtra("news");
        userDao = new UserDao();
        userDao.createDB(this);     //创建数据库
        user_id = getIntent().getStringExtra("user_id");
        sort = getIntent().getStringExtra("sort");
        String url = news.getUrl();
        final String title = getIntent().getStringExtra("title");
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        //如果新闻已收藏
        if(userDao.selectShouCangByTitle(user_id, news.getTitle()))
            button.setText("已收藏");
        //新闻没被收藏
        else
            button.setText("收藏");
        //点击收藏按钮
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = button.getText().toString();
                //如果已收藏
                // 则删除数据库中的数据
                // 弹出提示
                // 把button上的按钮文字设置为“收藏”
                if(str.equals("已收藏")) {
                    userDao.deleteShouCang(user_id, news.getTitle());
                    Toast.makeText(des_activity.this, "已移除收藏", Toast.LENGTH_SHORT).show();
                    button.setText("收藏");
                }
                //如果未收藏
                // 则添加到数据库
                // 弹出提示
                // 把button上的按钮文字设置为“已收藏”
                else if(str.equals("收藏")){
                    userDao.addShouCang(news, user_id, sort);
                    Toast.makeText(des_activity.this, "已添加至收藏", Toast.LENGTH_SHORT).show();
                    button.setText("已收藏");
                }
            }
        });
        webView = (WebView)findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(title);
        webView.loadUrl(url);   //webView加载新闻

    }

    /**
     * 点击返回键做了处理
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            default:
        }
        return true;
    }
}
