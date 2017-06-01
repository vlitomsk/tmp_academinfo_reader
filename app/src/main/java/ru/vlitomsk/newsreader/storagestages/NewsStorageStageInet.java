package ru.vlitomsk.newsreader.storagestages;

import ru.vlitomsk.newsreader.news.News;
import ru.vlitomsk.newsreader.parsers.AcademInfoParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NewsStorageStageInet implements INewsStorageStage {

    @Override
    public News getNews(int id) {
        try {
            return AcademInfoParser.readNews(id);
        } catch (IOException e) {
            System.out.println("Can't load news from internet");
            return null;
        }
    }

    @Override
    public List<News> getTopKMaybeTeaser(int k) {
        if (k > 30000) {
            throw new RuntimeException("can't get more than 30000 news");
        }
        try {
            ArrayList<News> topknews = new ArrayList<>();
            int pageNo = 1;
            while (topknews.size() < k) {
                topknews.addAll(AcademInfoParser.readTeasers(pageNo++));
            }
            return topknews;
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public void setNextStage(INewsStorageStage nextStage) {
        throw new RuntimeException("NewsStorageStageInet is terminal stage!");
    }
}
