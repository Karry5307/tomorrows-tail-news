package com.java.tomorrowstailnews.entity;

public class HistoryInfo {
    private int historyId, isStarred;
    private String newsId, newsJson;

    public HistoryInfo(int historyId, int isStarred, String newsId, String newsJson) {
        this.historyId = historyId;
        this.isStarred = isStarred;
        this.newsId = newsId;
        this.newsJson = newsJson;
    }

    public String getNewsJson() {
        return newsJson;
    }
}