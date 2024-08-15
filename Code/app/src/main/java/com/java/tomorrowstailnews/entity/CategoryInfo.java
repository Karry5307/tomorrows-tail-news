package com.java.tomorrowstailnews.entity;

public class CategoryInfo {
    private String category;
    private int isVisible;

    public CategoryInfo(String category, int isVisible) {
        this.category = category;
        this.isVisible = isVisible;
    }

    public String getCategory() {
        return category;
    }

    public int getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(int isVisible) {
        this.isVisible = isVisible;
    }
}
