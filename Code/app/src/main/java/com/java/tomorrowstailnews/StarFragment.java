package com.java.tomorrowstailnews;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.java.tomorrowstailnews.adapter.NewsListAdapter;
import com.java.tomorrowstailnews.db.HistoryDbHelper;
import com.java.tomorrowstailnews.entity.HistoryInfo;
import com.java.tomorrowstailnews.entity.NewsItemBindingData;
import com.java.tomorrowstailnews.entity.NewsItemInfo;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StarFragment extends Fragment {
    private View rootView;
    private RecyclerView recyclerView;
    private NewsListAdapter newsListAdapter;
    private List<NewsItemInfo.Data> dataList = new ArrayList<>();
    private List<HistoryInfo> historyInfoList;

    public StarFragment() {
        // Required empty public constructor
    }

    public static StarFragment newInstance() {
        StarFragment fragment = new StarFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_star, container, false).getRootView();
        recyclerView = rootView.findViewById(R.id.recyclerViewStar);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView.setAdapter(newsListAdapter = new NewsListAdapter());
        newsListAdapter.setOnItemClickListener(new NewsListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(NewsItemBindingData data, int position) {
                Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
                intent.putExtra("data", data.getData());
                startActivity(intent);
            }
        });
        refresh();
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    public void refresh() {
        dataList.clear();
        historyInfoList = HistoryDbHelper.getInstance(getContext()).queryStarredHistoryInfoList();
        for (HistoryInfo historyInfo : historyInfoList) {
            dataList.add(new Gson().fromJson(historyInfo.getNewsJson(), NewsItemInfo.Data.class));
        }
        Collections.reverse(dataList);
        if (dataList != null) {
            if (newsListAdapter != null) {
                newsListAdapter.setDataList(getBindingDataList(dataList));
            }
        }
    }

    public List<NewsItemBindingData> getBindingDataList(List<NewsItemInfo.Data> dataList) {
        List<NewsItemBindingData> bindingDataList = new ArrayList<>();
        for (NewsItemInfo.Data data : dataList) {
            bindingDataList.add(new NewsItemBindingData(data, false));
        }
        return bindingDataList;
    }
}