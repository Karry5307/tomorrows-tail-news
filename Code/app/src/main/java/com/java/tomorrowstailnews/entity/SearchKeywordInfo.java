package com.java.tomorrowstailnews.entity;

import java.util.List;

public class SearchKeywordInfo {
    private String category, date;
    private List<String> keywordList;

    public SearchKeywordInfo(String category, String date, List<String> keywordList) {
        this.category = category;
        this.date = date;
        this.keywordList = keywordList;
    }

    public String getCategory() {
        return category;
    }

    public String getDate() {
        return date;
    }

    public List<String> getKeywordList() {
        return keywordList;
    }
}
