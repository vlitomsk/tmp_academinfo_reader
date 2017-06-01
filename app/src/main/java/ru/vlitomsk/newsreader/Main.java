package ru.vlitomsk.newsreader;

import ru.vlitomsk.newsreader.news.News;
import ru.vlitomsk.newsreader.parsers.AcademInfoParser;

import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException {
        News testNews = AcademInfoParser.readNews(38494);
        Collection<News> testTeasers = AcademInfoParser.readTeasers(1);
        System.out.println("bye.");
        // write your code here
    }
}
