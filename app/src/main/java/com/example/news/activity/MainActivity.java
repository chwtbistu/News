package com.example.news.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.news.Gson.News;
import com.example.news.QueryAdapter;
import com.example.news.R;
import com.example.news.ThreadandRunnable;
import com.example.news.Util.UserDao;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    //注册组件和对象
    private ListView listView;
    private List<News> news;
    private Handler handler;
    private UserDao userDao;
    private String user_id;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Map<String, String> map;
    private TextView show_user_name;
    private ActionBar actionBar;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userDao = new UserDao();
        userDao.createDB(this); //创建数据库
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headView = navigationView.getHeaderView(0);
        show_user_name = headView.findViewById(R.id.show_username); //获取navigationview的textview
        map = new HashMap<>();      //创建分类和关键字的映射
        map.put("社会新闻", "social");
        map.put("体育新闻", "tiyu");
        map.put("足球新闻", "football");
        map.put("国内新闻", "guonei");
        map.put("国际新闻", "world");
        map.put("娱乐新闻", "huabian");
        map.put("科技新闻", "keji");
        map.put("创业新闻", "startup");
        map.put("苹果新闻", "apple");
        map.put("IT资讯", "it");
        map.put("已收藏", "shoucang");
        actionBar = getSupportActionBar();
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        listView = (ListView)findViewById(R.id.list);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe);
        handler = new Handler();
        sharedPreferences = getSharedPreferences("loginUser", Context.MODE_PRIVATE);
        Intent intent = getIntent();    //获取传递
        //若intent的user_id不是null
        // 说明是其他页面跳转
        // 则设置user_id变量的值为intent传递的user_id
        // NavigationView上的用户名也为该值
        if(intent.getStringExtra("user_id") != null) {  //若intent的user_id不是null
            this.user_id = intent.getStringExtra("user_id");
            show_user_name.setText(user_id);
        }else {
            //若intent的user_id为null
            // 说明刚打开app
            // 则获取目录下名为"loginUser"的SharedPreference文件
            // 查看是否上一次关闭时是登录状态
            // 如果是，则设置this.user_id为从"loginUser.xml"文件中获取的内容
            // 同时设置NavigationView的用户名
            if(!sharedPreferences.getString("user_id", "").equals("")) {
                this.user_id = sharedPreferences.getString("user_id", "");
                show_user_name.setText(user_id);
            }
            //若"loginUser.xml"内没有内容，则设置user_id为Android Studio
            else
                this.user_id = "Android Studio";
        }
        //获取intent传递的sort变量
        // 若sort为null
        // 则获取“社会新闻”分类的内容
        if(intent.getStringExtra("sort")==null) {
            ThreadandRunnable threadandRunnable = new ThreadandRunnable(this, listView, handler, userDao, "social");
            threadandRunnable.ThreadandRunnable();
            news = threadandRunnable.getList();
        }
        //获取intent传递的sort变量
        // 若sort为"shoucang"
        // 则获取关键字为sort的内容
        // 同时将标题设为intent传递的title变量
        else if(intent.getStringExtra("sort").equals("shoucang")) {
            actionBar.setTitle(intent.getStringExtra("title"));
            Log.d("MYTAG", "user_id is "+user_id);
            news = userDao.selectShouCang(user_id);
            QueryAdapter queryAdapter = new QueryAdapter(this, news);
            listView.setAdapter(queryAdapter);
        }
        //获取intent传递的sort变量
        // 若sort不为null且不为"shoucang"
        // 则获取关键字为sort的内容
        // 同时将标题设为intent传递的title变量
        else {
            actionBar.setTitle(intent.getStringExtra("title"));
            ThreadandRunnable threadandRunnable = new ThreadandRunnable(this, listView, handler, userDao, intent.getStringExtra("sort"));
            threadandRunnable.ThreadandRunnable();
            news = threadandRunnable.getList();
        }
        //设置listView的点击事件
        // 点击listView
        // 跳转到des_activity中
        // 同时将此时的title、sort和相应的News对象传递
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                News news1 = news.get(position);
                Intent intent = new Intent(MainActivity.this, des_activity.class);
                String title = actionBar.getTitle().toString();
                Log.d("MYTAG", "title is "+ title);
                if(title.equals("News")) {
                    intent.putExtra("title", "社会新闻");
                    title = "社会新闻";
                }
                else
                    intent.putExtra("title", title);
                Bundle bundle = new Bundle();
                bundle.putSerializable("news",news1);
                intent.putExtra("user_id", user_id);
                intent.putExtra("sort", map.get(title));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        //设置当标题为“已收藏”的界面的listView长按弹出菜单功能
        // 长按后弹出菜单栏
        // 可以选择是否取消收藏
        if(actionBar.getTitle().toString().equals("已收藏")) {
            listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    menu.add(0, 0, 0, "取消收藏");
                }
            });
        }
        //swipeRefreshLayout的刷新功能
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                String sort="";
                //获取当前actionBar的标题
                if(actionBar.getTitle().equals("News"))
                    sort = "social";
                else {
                    String title = actionBar.getTitle().toString();
                    sort = map.get(title);
                    Log.d("MYTAG", "sort is "+ sort);
                }
                //获取相关关键字的新闻
                ThreadandRunnable threadandRunnable = new ThreadandRunnable(MainActivity.this, listView, handler, userDao, sort);
                int size_before = news.size();
                threadandRunnable.ThreadandRunnable();
                news = threadandRunnable.getList();
                int size_after = news.size();
                swipeRefreshLayout.setRefreshing(false);
                //显示此次刷新新增了多少条内容
                Toast.makeText(MainActivity.this, "新增"+(size_after-size_before)+"条", Toast.LENGTH_SHORT).show();
            }
        });
        //设置navigationView的菜单栏
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                //点击跳转到MainActivity
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                String sort = "";
                String title = "";
                //不同的菜单id对应不同的关键字和标题
                switch (menuItem.getItemId()) {
                    case R.id.nav_social:
                        sort = "social";
                        title = "社会新闻";
                        break;
                    case R.id.nav_tiyu:
                        sort = "tiyu";
                        title = "体育新闻";
                        break;
                    case R.id.nav_football:
                        sort = "football";
                        title = "足球新闻";
                        break;
                    case R.id.nav_keji:
                        sort = "keji";
                        title = "科技新闻";
                        break;
                    case R.id.nav_startup:
                        sort = "startup";
                        title = "创业新闻";
                        break;
                    case R.id.nav_guonei:
                        sort = "guonei";
                        title = "国内新闻";
                        break;
                    case R.id.nav_world:
                        sort = "world";
                        title = "国际新闻";
                        break;
                    case R.id.nav_huabian:
                        sort = "huabian";
                        title = "娱乐新闻";
                        break;
                    case R.id.nav_apple:
                        sort = "apple";
                        title = "苹果新闻";
                        break;
                    case R.id.nav_it:
                        sort = "it";
                        title = "IT资讯";
                        break;
                    case R.id.nav_shoucang:
                        sort = "shoucang";
                        title = "已收藏";
                        break;
                }
                menuItem.setChecked(true);
                drawer.closeDrawers();
                intent.putExtra("sort", sort);
                intent.putExtra("title", title);
                intent.putExtra("user_id", user_id);
                startActivity(intent);
                return true;
            }
        });

    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    //设置右上角菜单功能
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //注销账户
        if (id == R.id.action_login_out) {
            //若已经登录
            if(!user_id.equals("Android Studio")) {
                AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("退出登录")//设置对话框的标题
                    .setMessage("是否退出登录？")//设置对话框的内容
                    //设置对话框的按钮
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //确认注销，则user_id的值为"Android Studio"
                            // navigationView的用户名设置为默认
                            // 将"loginUser.xml"的内容清空
                            //提示已经退出登录
                            user_id = "Android Studio";
                            show_user_name.setText(MainActivity.this.getResources().getString(R.string.nav_header_title));
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.clear();
                            editor.commit();
                            Toast.makeText(MainActivity.this, "你已经退出登录！", Toast.LENGTH_SHORT).show();                            }
                    }).create();
                dialog.show();

            }
            //如果还没登录
            // 即navigationView的用户名显示为Android Studio
            // 则提示还没有登录
            else {
                Toast.makeText(MainActivity.this, "你还没有登录！", Toast.LENGTH_SHORT).show();
            }
        }
        //登录
        if(id == R.id.action_login_in) {
            //还没登陆跳
            // 转到LoginActivity
            // 同时传递当前的actionBar和title使得登陆完后跳转回原来的界面
            if(user_id.equals("Android Studio")) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.putExtra("sort", map.get(actionBar.getTitle().toString()));
                intent.putExtra("title", actionBar.getTitle().toString());
                startActivity(intent);
            }
            //若已经登录
            // 则提示你已经登录
            else
                Toast.makeText(MainActivity.this, "你已经登录！", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
    //实现长按弹出菜单的方法
    public boolean onContextItemSelected(final MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            //移除收藏
            // 弹出对话框提示
            // 是否取消收藏
            case 0:
                AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("取消收藏")//设置对话框的标题
                    .setMessage("是否移除收藏？")//设置对话框的内容
                    //设置对话框的按钮
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        //确认移除收藏
                        // 删除数据库中的记录
                        // 刷新界面
                        public void onClick(DialogInterface dialog, int which) {
                            userDao.deleteShouCang(user_id, news.get(info.position).getTitle());
                            dialog.dismiss();
                            listView.setAdapter(new QueryAdapter(MainActivity.this, userDao.selectShouCang(user_id)));
                        }
                    }).create();
                dialog.show();
        }
        return true;
    }

}
