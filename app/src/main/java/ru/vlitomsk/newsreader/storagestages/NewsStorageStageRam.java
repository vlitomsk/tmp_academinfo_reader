package ru.vlitomsk.newsreader.storagestages;

import ru.vlitomsk.newsreader.news.News;

import java.util.*;

public class NewsStorageStageRam implements INewsStorageStage {
    private INewsStorageStage nextStage;
    private SortedMap<Integer, News> loadedNews = new TreeMap<>();

    private void put(News news) {
        if (news == null) {
            return;
        }
        if (loadedNews.containsKey(news.getId())) {
            if (news.isTeaser() && !loadedNews.get(news.getId()).isTeaser()) {
                return;
            }
        }
        loadedNews.put(news.getId(), news);
    }

    @Override
    public News getNews(int id) {
        if (loadedNews.containsKey(id)) {
            final News n = loadedNews.get(id);
            if (n != null && !n.isTeaser()) {
                return n;
            }
        }
        final News newNews = nextStage.getNews(id);
        put(newNews);
        return newNews;
    }

    @Override
    public List<News> getTopKMaybeTeaser(int k) {
        List<News> nextNews = nextStage.getTopKMaybeTeaser(k);
        if (nextNews == null) {
            return null;
        }
        for (News n : nextNews) {
            put(n);
        }
        return nextNews;
    }

    @Override
    public void setNextStage(INewsStorageStage nextStage) {
        this.nextStage = nextStage;
    }
}
