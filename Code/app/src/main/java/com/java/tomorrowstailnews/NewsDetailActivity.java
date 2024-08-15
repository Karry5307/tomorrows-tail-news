package com.java.tomorrowstailnews;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toolbar;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.java.tomorrowstailnews.db.HistoryDbHelper;
import com.java.tomorrowstailnews.entity.NewsItemInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 *
 * Currently there are some issues to be solved e.g. conflicts between video & img display
 * current handling logic: if there is video then show the video otherwise show the first img
 */

public class NewsDetailActivity extends AppCompatActivity {
    public static final int MSG_WHAT = 7053;
    public static final int TIMEOUT = 15000;
    public static final String URL = "https://open.bigmodel.cn/api/paas/v4/chat/completions";
    public static final String API_KEY = ""; // Replace it with your own API Key
    public static final String HINT = "请生成如下新闻的一则摘要：";

    private NewsItemInfo.Data data;
    private List<String> urlList;

    private Toolbar toolbar;
    private TextView titleText, newsTitle, newsPublisher, newsPublishTime, newsAbstract, newsText;
    private ImageView starImg, newsImg;
    private VideoView newsVideo;
    private Drawable star, starYellow;
    private HistoryDbHelper helper;

    public Handler handler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_WHAT) {
                String abstractText = "新闻摘要：" + (String) msg.obj + "（以上摘要为 AI 生成）";
                newsAbstract.setText(abstractText);
                helper.modifyAbstract(data.getNewsID(), abstractText);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_news_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        data = (NewsItemInfo.Data) getIntent().getSerializableExtra("data");
        toolbar = findViewById(R.id.toolbarNewsDetail);
        titleText = findViewById(R.id.titleNewsDetailToolbar);
        newsTitle = findViewById(R.id.titleNewsDetail);
        newsPublisher = findViewById(R.id.publisherNewsDetail);
        newsPublishTime = findViewById(R.id.publishTimeNewsDetail);
        newsAbstract = findViewById(R.id.abstractNewsDetail);
        newsText = findViewById(R.id.textNewsDetail);
        newsImg = findViewById(R.id.imgNewsDetail);
        newsVideo = findViewById(R.id.videoNewsDetail);
        starImg = findViewById(R.id.iconStarNewsDetail);
        star = AppCompatResources.getDrawable(this, R.drawable.round_star_border_24);
        starYellow = AppCompatResources.getDrawable(this, R.drawable.round_star_24_light_yellow);
        helper = HistoryDbHelper.getInstance(this);
        if (data != null) {
            String publisher = "新闻来源：" + data.getPublisher();
            String publishTime = "发布时间：" + data.getPublishTime();
            titleText.setText(data.getTitle());
            newsTitle.setText(data.getTitle());
            newsPublisher.setText(publisher);
            newsPublishTime.setText(publishTime);
            newsText.setText(data.getContent());
            if(!helper.queryIsAdded(data.getNewsID())) {
                helper.addHistory(data.getNewsID(), "", new Gson().toJson(data), 0);
            }
            int isStarred = helper.queryIsStarred(data.getNewsID());
            starImg.setImageDrawable(isStarred == 1 ? starYellow : star);
            if (!helper.queryAbstract(data.getNewsID()).isEmpty()) {
                newsAbstract.setText(helper.queryAbstract(data.getNewsID()));
            } else {
                testInvokeSync(HINT + data.getContent());
            }
            if (!data.getVideo().isEmpty()) {
                newsImg.setVisibility(View.GONE);
                Log.d("--------", "onCreate: " + data.getVideo());
                newsVideo.setVideoURI(Uri.parse(data.getVideo()));
                newsVideo.setMediaController(new MediaController(this));
                newsVideo.start();
            } else {
                newsVideo.setVisibility(View.GONE);
                if(!data.getImage().isEmpty()) {
                    urlList = parseImgUrlList(data.getImage());
                    if (urlList.size() != 0) {
                        Glide.with(this).load(urlList.get(0)).into(newsImg);
                    }
                }
            }
        }
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        starImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int isStarred = helper.queryIsStarred(data.getNewsID()) ^ 1;
                helper.modifyIsStarred(data.getNewsID(), isStarred);
                starImg.setImageDrawable(isStarred == 1 ? starYellow : star);
            }
        });
    }

    public List<String> parseImgUrlList(String imgUrlList) {
        List<String> urlList = new ArrayList<>();
        String[] urlArray = imgUrlList.substring(1, imgUrlList.length() - 1).split(",");
        for (String url: urlArray) {
            String urlTrim = url.trim();
            if(!urlTrim.isEmpty()) {
                urlList.add(url.trim());
            }
        }
        return urlList;
    }

    public void testInvokeSync(String hint) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                Log.d("--------", "run: 开始生成");
                String requestJson = new GsonBuilder().disableHtmlEscaping().create()
                        .toJson(new RequestDataInfo(hint)).toString();
                OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                        .connectTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                        .readTimeout(TIMEOUT, TimeUnit.MILLISECONDS).build();
                Request request = new Request.Builder().url(URL)
                        .addHeader("Authorization", "Bearer " + API_KEY)
                        .post(RequestBody.create(MediaType.parse("application/json"), requestJson))
                        .build();
                Call call = okHttpClient.newCall(request);
                try {
                    String data = call.execute().body().string();
                    String text = new Gson().fromJson(data, ResponseDataInfo.class).getChoices()
                            .get(0).getMessage().getContent();
                    Message message = new Message();
                    message.what = MSG_WHAT;
                    message.obj = text;
                    handler.sendMessage(message);
                    Log.d("--------", "run: " + text);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public class RequestDataInfo {
        public String model = "glm-4";
        public List<RequestDataInfoMsg> messages = new ArrayList<>();

        public RequestDataInfo(String hint) {
            this.messages.add(new RequestDataInfoMsg(hint));
        }

        public class RequestDataInfoMsg {
            public String role = "user", content;

            public RequestDataInfoMsg(String content) {
                this.content = content;
            }
        }
    }

    public class ResponseDataInfo {
        private int created;
        private String id, model, request_id;
        private TokenUsage usage;
        private List<ResponseDataItemInfo> choices;

        public List<ResponseDataItemInfo> getChoices() {
            return choices;
        }

        public class TokenUsage{
            int completion_tokens, prompt_tokens, total_tokens;
        }

        public class ResponseDataItemInfo {
            private int index;
            private String finish_reason;
            private ResponseMessageInfo message;

            public ResponseMessageInfo getMessage() {
                return message;
            }

            public class ResponseMessageInfo {
                private String content, role;

                public String getContent() {
                    return content;
                }
            }
        }
    }
}