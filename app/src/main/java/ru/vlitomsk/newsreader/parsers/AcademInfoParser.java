package ru.vlitomsk.newsreader.parsers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import ru.vlitomsk.newsreader.news.News;
import ru.vlitomsk.newsreader.news.NewsImage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AcademInfoParser {
    private static final String SITE_ROOT = "http://academ.info/";
    private static final String NEWS_ROOT = SITE_ROOT + "/news/";
    private static final String NEWS_PAGE_ROOT = SITE_ROOT + "/genre/novosti?page=";

    private static String loadUrl(String url) throws IOException {
        URL website = new URL(url);
        URLConnection connection = website.openConnection();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        connection.getInputStream()));

        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null)
            response.append(inputLine);

        in.close();

        return response.toString();
    }

    public static NewsImage loadNewsImage(String id) throws IOException {
        URL url = new URL(SITE_ROOT + id);
        Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        return new NewsImage(id, bmp);
    }

    private static final Pattern pat_teaser = Pattern.compile("<a href=\"/news/([0-9]+)\" title=\"([^\"]+)\"");
    public static List<News> readTeasers(int pageNo) throws IOException {
        final String page = loadUrl(NEWS_PAGE_ROOT + pageNo);
        Hashtable<Integer, News> teasers = new Hashtable<>();
        Matcher m_teaserHead = pat_teaser.matcher(page);
        while (m_teaserHead.find()) {
            final int newsId = Integer.parseInt(m_teaserHead.group(1));
            if (teasers.contains(newsId)) {
                continue;
            }
            final String newsTitle = m_teaserHead.group(2);
            final int teaserEndIdx = m_teaserHead.end();
            final int a = page.indexOf("a href=\"/themes/", teaserEndIdx);
            final int b = page.indexOf(">", a);
            final int c = page.indexOf("<", b + 1);

            if (a < 0 || b < 0 || c < 0) {
                return null;
            }

            final String newsTheme = page.substring(b + 1, c);
            teasers.put(newsId, new News(newsId, newsTitle, newsTheme));
        }
        return new ArrayList<>(teasers.values());
    }

    private static int nPages() throws IOException {
        return 3500;
    }

    private static final Pattern pat_ogTitle = Pattern.compile("meta property=\"og:title\" content=\"(.*)\" /><meta property=\"og:descrip.*");
    private static final Pattern pat_theme = Pattern.compile("<span class=\"terms\">.*<a href=\"/themes/[a-zA-Z_0-9]*\">([а-яА-Яa-zA-Z0-9 _]*)</a>");
    private static final String pat_newsStart = "<div class=\"lead\">";
    private static final String pat_newsEnd = "<p class=\"rtejustify\"><a href=\"http://forum.academ.info/index.php?showtopic".toUpperCase();
    private static final Pattern pat_imgUrl = Pattern.compile("<a href=\"([^\"]*)\" target=\"_blank\" rel=\"lightbox\\[photo_carousel\\]\"");
    public static News readNews(int id) throws IOException {
        final String page = loadUrl(NEWS_ROOT + "/" + id);
        Matcher m_ogTitle = pat_ogTitle.matcher(page);
        Matcher m_theme = pat_theme.matcher(page);
        Matcher m_imgUrl = pat_imgUrl.matcher(page);
        String title, theme, text;
        ArrayList<String> imgIdsList = new ArrayList<>();

        if (m_ogTitle.find()) {
            title = m_ogTitle.group(1);
        } else {
            title = "Can't parse title";
        }

        if (m_theme.find()) {
            theme = m_theme.group(1);
        } else {
            theme = "Can't parse theme";
        }

        int newsStartIdx = page.indexOf(pat_newsStart);
        int newsEndIdx = page.toUpperCase().indexOf(pat_newsEnd);

        if (newsStartIdx < 0 || newsEndIdx < 0) {
            return null;
        } else {
            newsStartIdx += pat_newsStart.length();
        }

        final String newsRawBody = page.substring(newsStartIdx, newsEndIdx);

        while (m_imgUrl.find()) {
            String imgurl = m_imgUrl.group(1);
            if (imgurl.charAt(0) == '/') { imgurl = "http://academ.info" + imgurl; }
            imgIdsList.add(imgurl);
        }

        String[] img_ids = new String[imgIdsList.size()];
        img_ids = imgIdsList.toArray(img_ids);
        return new News(id, title, theme, newsRawBody, img_ids);
    }
}
