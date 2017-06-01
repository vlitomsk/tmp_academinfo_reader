package ru.vlitomsk.newsreader.news;

import android.graphics.Bitmap;

public class NewsImage {
    private String id;
    private Bitmap img;

    public NewsImage(String id, Bitmap img) {
        this.id = id;
        this.img = img;
    }

    public String getId() {
        return id;
    }

    public Bitmap getImg() {
        return img;
    }
}
