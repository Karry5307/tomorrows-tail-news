package com.java.tomorrowstailnews;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.java.tomorrowstailnews.adapter.NewsListAdapter;
import com.java.tomorrowstailnews.db.HistoryDbHelper;
import com.java.tomorrowstailnews.entity.NewsItemBindingData;
import com.java.tomorrowstailnews.entity.NewsItemInfo;
import com.google.gson.Gson;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CategorizedNewsFragment extends Fragment {
    private static final String ARG_CATEGORY = "category";
    private String mCategory;
    private static final int SIZE = 15;
    private static final int MSG_WHAT = 7035;
    private static final int DELAY_TIME = 3000;
    private static final String URL_HEADER = "https://api2.newsminer.net/svc/news/queryNewsList?";

    private int currentPage;
    private String currentTime;

    private View rootView;
    private RecyclerView recyclerView;
    private NewsListAdapter newsListAdapter;
    private RefreshLayout refreshLayout;

    public Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_WHAT) {
                NewsItemInfo newsItemInfo = new Gson().fromJson((String) msg.obj, NewsItemInfo.class);
                if (newsItemInfo != null) {
                    if (newsListAdapter != null) {
                        if (currentPage == 1) {
                            newsListAdapter.setDataList(getBindingDataList(newsItemInfo.getData()));
                        } else {
                            newsListAdapter.appendDataList(getBindingDataList(newsItemInfo.getData()));
                        }
                    }
                } else {
                    Log.d("--------", "onResponse: 炸了");
                }
            }
        }
    };

    public CategorizedNewsFragment() {
        // Required empty public constructor
    }

    public static CategorizedNewsFragment newInstance(String category) {
        CategorizedNewsFragment fragment = new CategorizedNewsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCategory = getArguments().getString(ARG_CATEGORY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_categorized_news, container, false).getRootView();
        recyclerView = rootView.findViewById(R.id.recyclerViewNews);
        refreshLayout = rootView.findViewById(R.id.refreshLayoutNews);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView.setAdapter(newsListAdapter = new NewsListAdapter());
        refreshLayout.setRefreshHeader(new ClassicsHeader(getContext()));
        refreshLayout.setRefreshFooter(new ClassicsFooter(getContext()));
        newsListAdapter.setOnItemClickListener(new NewsListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(NewsItemBindingData data, int position) {
                Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
                intent.putExtra("data", data.getData());
                startActivity(intent);
            }
        });
        getHttpData(currentPage = 1);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                Log.d("--------", "onRefresh: 刷新中");
                currentTime = getCurrentTime();
                for (int page = 1; page <= currentPage ; page++) {
                    getHttpData(page);
                }
                refreshLayout.finishRefresh(DELAY_TIME);
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                Log.d("--------", "onLoadMore: 加载更多");
                getHttpData(++currentPage);
                refreshLayout.finishLoadMore(DELAY_TIME);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        newsListAdapter.setDataList(getBindingDataList(newsListAdapter.getDataList()));
    }

    public String getCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public String generateURL(int page) {
        if (currentTime == null) {
            currentTime = getCurrentTime();
        }
        String url = URL_HEADER + "size=" + SIZE + "&startDate=&endDate=" + currentTime
                + "&words=&categories=" + ("全部".equals(mCategory) ? "" : mCategory) + "&page=" + page;
        Log.d("--------", url);
        return url;
    }

    public void getHttpData(int page) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(generateURL(page)).get().build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("--------", "onFailure: " + e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String data = response.body().string();
                Log.d("--------", "onResponse: " + data);
                Message message = new Message();
                message.what = MSG_WHAT;
                message.obj = data;
                handler.sendMessage(message);
            }
        });
    }

    public List<NewsItemBindingData> getBindingDataList(List<NewsItemInfo.Data> dataList) {
        List<NewsItemBindingData> bindingDataList = new ArrayList<>();
        HistoryDbHelper helper = HistoryDbHelper.getInstance(getContext());
        for (NewsItemInfo.Data data : dataList) {
            bindingDataList.add(new NewsItemBindingData(data, helper.queryIsAdded(data.getNewsID())));
        }
        return bindingDataList;
    }
}