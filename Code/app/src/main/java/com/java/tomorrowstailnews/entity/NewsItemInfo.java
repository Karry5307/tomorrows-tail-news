package com.java.tomorrowstailnews.entity;

import java.io.Serializable;
import java.util.List;

public class NewsItemInfo {
    private String pageSize;
    private int total;
    private List<Data> data;

    public List<Data> getData() {
        return data;
    }

    public static class Data implements Serializable {
        private String image, publishTime, language, video, title, content, url, newsID, crawlTime, publisher, category, currentPage;
        private List<WordItem> keywords, when, where, who;
        private List<PersonItem> persons, organizations, scholars;
        private List<LocationItem> locations;

        public String getTitle() {
            return title;
        }

        public String getPublishTime() {
            return publishTime;
        }

        public String getPublisher() {
            return publisher;
        }

        public String getContent() {
            return content;
        }

        public String getNewsID() {
            return newsID;
        }

        public String getImage() {
            return image;
        }

        public String getVideo() {
            return video;
        }

        public static class WordItem implements Serializable{
            private Double score;
            private String word;
        }

        public static class PersonItem implements Serializable{
            private int count;
            private String mention;
        }

        public static class LocationItem implements Serializable{
            private Double lng, lat;
            private int count;
            private String linkedURL, mention;
        }
    }
}