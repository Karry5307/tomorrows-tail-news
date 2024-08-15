package com.java.tomorrowstailnews;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.java.tomorrowstailnews.adapter.NewsListAdapter;
import com.java.tomorrowstailnews.db.HistoryDbHelper;
import com.java.tomorrowstailnews.entity.DateValidator;
import com.java.tomorrowstailnews.entity.NewsItemBindingData;
import com.java.tomorrowstailnews.entity.NewsItemInfo;
import com.java.tomorrowstailnews.entity.SearchKeywordInfo;
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
import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchResultActivity extends AppCompatActivity {
    private static final int SIZE = 15;
    private static final int MSG_WHAT = 3507;
    private static final int DELAY_TIME = 3000;
    private static final String URL_HEADER = "https://api2.newsminer.net/svc/news/queryNewsList?";
    private final String categories[] = {"科技", "社会", "文化", "军事", "体育", "娱乐", "健康", "财经", "汽车", "教育"};

    private int currentPage;
    private String keyword, currentTime;
    private DateValidator validator;

    private Toolbar toolbar;
    private TextView textSearch;
    private RecyclerView recyclerView;
    private RefreshLayout refreshLayout;
    private NewsListAdapter newsListAdapter;
    public Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_WHAT) {
                Log.d("--------", "handleMessage: " + (String) msg.obj);
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
                    Log.d("--------", "handleMessage: 炸了");
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_result);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        toolbar = findViewById(R.id.toolBarSearchResult);
        textSearch = findViewById(R.id.textSearchSearchResult);
        recyclerView = findViewById(R.id.recyclerViewSearchResult);
        refreshLayout = findViewById(R.id.refreshLayoutSearchResult);
        recyclerView.setAdapter(newsListAdapter = new NewsListAdapter());
        refreshLayout.setRefreshHeader(new ClassicsHeader(this));
        refreshLayout.setRefreshFooter(new ClassicsFooter(this));
        validator = new DateValidator();
        newsListAdapter.setOnItemClickListener(new NewsListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(NewsItemBindingData data, int position) {
                Intent intent = new Intent(SearchResultActivity.this, NewsDetailActivity.class);
                intent.putExtra("data", data.getData());
                startActivity(intent);
            }
        });
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SearchResultActivity.this, MainActivity.class));
            }
        });
        textSearch.setMovementMethod(LinkMovementMethod.getInstance());
        textSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SearchResultActivity.this, SearchActivity.class));
            }
        });
        textSearch.setText(keyword = (String) getIntent().getSerializableExtra("keyword"));
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
    protected void onResume() {
        super.onResume();
        newsListAdapter.setDataList(getBindingDataList(newsListAdapter.getDataList()));
    }

    public String getCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public SearchKeywordInfo parseKeyword(String keyword) {
        String[] keywordArray = keyword.split("(,|，)");
        String category = "", date = "";
        List<String> keywordList = new ArrayList<>();
        for (String word : keywordArray) {
            if (Arrays.asList(categories).contains(word)) {
                category = word;
            } else if (validator.isValid(word)) {
                if (date.isEmpty() || validator.isBefore(date, word)) {
                    date = word;
                }
            } else {
                keywordList.add(word);
            }
        }
        return new SearchKeywordInfo(category, date, keywordList);
    }

    public String getURL(SearchKeywordInfo info, int page) {
        String endDate = info.getDate().isEmpty() ? getCurrentTime() : info.getDate();
        String keyword = String.join(",", info.getKeywordList());
        String url = URL_HEADER + "size=" + SIZE + "&startDate=&endDate=" + endDate + "&words="
                + keyword + "&categories=" + info.getCategory() + "&page=" + page;
        Log.d("--------", url);
        return url;
    }

    public void getHttpData(int page) {
        OkHttpClient okHttpClient = new OkHttpClient();
        String url = getURL(parseKeyword(keyword), page);
        Request request = new Request.Builder().url(url).get().build();
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
        HistoryDbHelper helper = HistoryDbHelper.getInstance(this);
        for (NewsItemInfo.Data data : dataList) {
            bindingDataList.add(new NewsItemBindingData(data, helper.queryIsAdded(data.getNewsID())));
        }
        return bindingDataList;
    }
}