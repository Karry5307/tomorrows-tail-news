package com.java.tomorrowstailnews.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.java.tomorrowstailnews.entity.NewsItemBindingData;
import com.java.tomorrowstailnews.entity.NewsItemInfo;
import com.java.tomorrowstailnews.R;

import java.util.ArrayList;
import java.util.List;

public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.NewsListHolder> {
    private int colorBlack, colorGray;

    private List<NewsItemBindingData> dataList = new ArrayList<>();
    private OnItemClickListener onItemClickListener;

    @NonNull
    @Override
    public NewsListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_news_item, null);
        colorBlack = view.getResources().getColor(R.color.black);
        colorGray = view.getResources().getColor(R.color.darkGray);
        return new NewsListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsListHolder holder, int position) {
        NewsItemInfo.Data data = dataList.get(position).getData();
        boolean visited = dataList.get(position).getVisited();
        String publishTime = "时间：" + data.getPublishTime();
        String publisher = "新闻来源：" + data.getPublisher();
        holder.title.setText(data.getTitle());
        holder.title.setTextColor(visited ? colorGray : colorBlack);
        holder.publishTime.setText(publishTime);
        holder.publisher.setText(publisher);
        Log.d("--------", "onBindViewHolder: " + data.getImage());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(dataList.get(position), position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void setDataList(List<NewsItemBindingData> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
        Log.d("--------", "setDataList: 好的");
    }

    public void appendDataList(List<NewsItemBindingData> dataList) {
        this.dataList.addAll(dataList);
        notifyDataSetChanged();
        Log.d("--------", "emplaceDataList: 好的");
    }

    static class NewsListHolder extends RecyclerView.ViewHolder {

        TextView title, publishTime, publisher;

        public NewsListHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.listNewsItemTitle);
            publishTime = itemView.findViewById(R.id.listNewsItemPublishTime);
            publisher = itemView.findViewById(R.id.listNewsItemPublisher);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(NewsItemBindingData data, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public List<NewsItemInfo.Data> getDataList() {
        List<NewsItemInfo.Data> newsDataList = new ArrayList<>();
        for (NewsItemBindingData data : dataList) {
            newsDataList.add(data.getData());
        }
        return newsDataList;
    }
}