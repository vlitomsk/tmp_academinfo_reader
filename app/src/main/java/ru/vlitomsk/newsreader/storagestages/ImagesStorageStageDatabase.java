package ru.vlitomsk.newsreader.storagestages;

import ru.vlitomsk.newsreader.news.NewsImage;

public class ImagesStorageStageDatabase implements IImagesStorageStage {
    private IImagesStorageStage nextStage;

    @Override
    public NewsImage getImage(String id) {
        return nextStage.getImage(id);
    }

    @Override
    public void setNextStage(IImagesStorageStage nextStage) {
        this.nextStage = nextStage;
    }
}
