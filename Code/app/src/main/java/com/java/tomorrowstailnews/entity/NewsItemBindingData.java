package com.java.tomorrowstailnews.entity;

public class NewsItemBindingData {
    NewsItemInfo.Data data;
    boolean visited;

    public NewsItemBindingData(NewsItemInfo.Data data, boolean visited) {
        this.data = data;
        this.visited = visited;
    }

    public NewsItemInfo.Data getData() {
        return data;
    }

    public boolean getVisited() {
        return visited;
    }
}