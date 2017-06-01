package ru.vlitomsk.newsreader.storagestages;

import java.io.IOException;

import ru.vlitomsk.newsreader.news.NewsImage;
import ru.vlitomsk.newsreader.parsers.AcademInfoParser;

public class ImagesStorageStageInternet implements IImagesStorageStage {

    @Override
    public NewsImage getImage(String id) {
        try {
            return AcademInfoParser.loadNewsImage(id);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void setNextStage(IImagesStorageStage nextStage) {
        System.out.println("ImagesStorageStageInternet is terminal stage!");
    }
}
