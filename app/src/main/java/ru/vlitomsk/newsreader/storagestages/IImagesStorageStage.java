package ru.vlitomsk.newsreader.storagestages;

import ru.vlitomsk.newsreader.news.NewsImage;

public interface IImagesStorageStage {
    NewsImage getImage(String id);
    void setNextStage(IImagesStorageStage nextStage);
}
