package ru.vlitomsk.newsreader.storagestages;

import ru.vlitomsk.newsreader.news.News;

import java.util.List;

public class NewsStorageStageDatabase implements INewsStorageStage {
    private INewsStorageStage nextStage;

    @Override
    public News getNews(int id) {
        return nextStage.getNews(id);
        //throw new RuntimeException("put here in database");
        //return null;
    }

    @Override
    public List<News> getTopKMaybeTeaser(int k) {
        return nextStage.getTopKMaybeTeaser(k);
    }

    @Override
    public void setNextStage(INewsStorageStage nextStage) {
        this.nextStage = nextStage;
    }
}
