package com.example.news;

import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.news.Gson.News;
import com.example.news.Gson.NewsList;

import java.util.List;
//listView的适配器
public class QueryAdapter extends BaseAdapter {
    private Context mContext;
    private List<News> list;

    public QueryAdapter(Context mContext, List<News> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if(convertView == null) {
            view = View.inflate(mContext, R.layout.news_item, null);
        }
        else {
            view = convertView;
        }
        TextView title = (TextView)view.findViewById(R.id.title);
        TextView content = (TextView)view.findViewById(R.id.description);
        TextView date = (TextView)view.findViewById(R.id.time);
        ImageView imageView = (ImageView)view.findViewById(R.id.title_pic);
        News news = list.get(position);
        title.setText(news.getTitle());
        content.setText(news.getDescription());
        date.setText(news.getCtime());
        //根据picUrl加载图片到ImageView
        Glide.with(mContext).load(news.getPicUrl()).into(imageView);
        return view;
    }
    public List<News> getList() {
        return this.list;
    }
}
