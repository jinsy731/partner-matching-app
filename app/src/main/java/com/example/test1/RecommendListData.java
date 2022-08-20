package com.example.test1;

import android.view.View;

public class RecommendListData {
    private int img_src;
    private String title;

    public void setImgSrc(int src) {
        img_src = src;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImgSrc() {
        return img_src;
    }

    public String getTitle() {
        return title;
    }
}
