package ru.vlitomsk.newsreader.storagestages;

import ru.vlitomsk.newsreader.news.News;

import java.util.HashMap;
import java.util.List;

public interface INewsStorageStage {
    News getNews(int id);
    List<News> getTopKMaybeTeaser(int k);
    void setNextStage(INewsStorageStage nextStage);
}
