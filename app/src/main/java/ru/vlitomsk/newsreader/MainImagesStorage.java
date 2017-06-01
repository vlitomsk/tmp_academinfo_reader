package ru.vlitomsk.newsreader;

import ru.vlitomsk.newsreader.news.NewsImage;
import ru.vlitomsk.newsreader.storagestages.IImagesStorageStage;
import ru.vlitomsk.newsreader.storagestages.ImagesStorageStageDatabase;
import ru.vlitomsk.newsreader.storagestages.ImagesStorageStageInternet;

public class MainImagesStorage {
    private static final MainImagesStorage instance = new MainImagesStorage();
    public static MainImagesStorage getInstance() {
        return instance;
    }

    private IImagesStorageStage stDb, stInet;

    private MainImagesStorage() {
        stInet = new ImagesStorageStageInternet();
        stDb = new ImagesStorageStageDatabase();

        stDb.setNextStage(stInet);
    }

    synchronized public NewsImage getImage(String id) {
        return stDb.getImage(id);
    }
}
