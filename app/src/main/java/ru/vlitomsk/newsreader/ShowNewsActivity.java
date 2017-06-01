package ru.vlitomsk.newsreader;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.widget.Button;
import android.widget.TextView;

import java.util.*;

import ru.vlitomsk.newsreader.news.News;

public class ShowNewsActivity extends AppCompatActivity {

    public static final String EXTR_NEWS_ID = "newsId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_news);
        Context ctx = this;
        TextView headerView = (TextView) findViewById(R.id.newsHeader);
        TextView categoryView = (TextView) findViewById(R.id.newsCategory);
        TextView bodyView = (TextView) findViewById(R.id.newsBody);

        Intent intent = getIntent();
        int newsId = intent.getIntExtra(EXTR_NEWS_ID, 0);

        headerView.setText("Loading...");
        categoryView.setText("");
        bodyView.setText("");
        PriorityTaskManager.DOWNLOAD_MANAGER.schedule(PriorityTaskManager.HI_PRIO, ()-> {
            News news = MainNewsStorage.getInstance().getNews(newsId);

            runOnUiThread(()-> {
                if (news == null) {
                    headerView.setText("Failed: no Internet");
                    return;
                }
                headerView.setText(news.getTitle());
                categoryView.setText(news.getTheme());
                Spanned spanned = Html.fromHtml(news.getText()); // for 24 api and more
                bodyView.setText(spanned);
                bodyView.setMovementMethod(new ScrollingMovementMethod());
                Button btn = ((Button) findViewById(R.id.buttonViewImages));
                if (news.getImg_ids().length == 0) {
                    btn.setText("No images for this news");
                    btn.setEnabled(false);
                } else {
                    btn.setText("View images (" + news.getImg_ids().length + ")");
                    btn.setEnabled(true);
                }
                btn.setOnClickListener(v -> {
                    Intent showImgIntent = new Intent(ctx, ImagesViewActivity.class);
                    ArrayList<String> ids = new ArrayList<>();
                    for (String imgid : news.getImg_ids()) {
                        ids.add(imgid);
                    }
                    showImgIntent.putExtra(ImagesViewActivity.EXTR_IDS, ids);
                    startActivity(showImgIntent);
                });
            });
        });


    }
}
