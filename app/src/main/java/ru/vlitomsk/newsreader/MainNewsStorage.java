package ru.vlitomsk.newsreader;

import ru.vlitomsk.newsreader.news.News;
import ru.vlitomsk.newsreader.storagestages.INewsStorageStage;
import ru.vlitomsk.newsreader.storagestages.NewsStorageStageDatabase;
import ru.vlitomsk.newsreader.storagestages.NewsStorageStageInet;
import ru.vlitomsk.newsreader.storagestages.NewsStorageStageRam;
import java.util.*;

public class MainNewsStorage {
    private static final MainNewsStorage instance = new MainNewsStorage();
    public static MainNewsStorage getInstance() {
        return instance;
    }

    private INewsStorageStage stRam, stDb, stInet, stHEAD;

    private MainNewsStorage() {
        stInet = new NewsStorageStageInet();
        stDb = new NewsStorageStageDatabase();
        stRam = new NewsStorageStageRam();

        stRam.setNextStage(stDb);
        stDb.setNextStage(stInet);

        stHEAD = stRam;
    }

    synchronized public News getNews(int id) {
        return stHEAD.getNews(id);
    }
    synchronized public List<News> getTopKMaybeTeaser(int k) { return stHEAD.getTopKMaybeTeaser(k);}
}
