package ru.vlitomsk.newsreader.news;

import java.io.Serializable;

public class News implements Serializable {
    private int id;
    private String title;
    private String theme;
    private String text;
    private String[] img_ids;
    private boolean isTeaser;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getTheme() {
        return theme;
    }

    public String getText() {
        return text;
    }

    public String[] getImg_ids() {
        return img_ids;
    }

    public boolean isTeaser() {
        return isTeaser;
    }

    public News(int id, String title, String theme, String text, String[] img_ids) {
        this.id = id;
        this.title = title;
        this.theme = theme == null ? "" : theme.trim();
        this.text = text.trim();
        if (img_ids == null) {
            this.img_ids = new String[0];
            return;
        } else {
            this.img_ids = img_ids;
            for (int i = 0; i < this.img_ids.length; ++i) {
                this.img_ids[i] = this.img_ids[i].trim();
            }
        }
        isTeaser = false;
    }

    public News(int id, String title, String theme) {
        this.id = id;
        this.title = title;
        this.theme = theme;
        this.text = "";
        this.img_ids = new String[0];
        isTeaser = true;
    }
}
