package ru.vlitomsk.newsreader;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.*;

import android.content.*;
import android.view.*;
import android.widget.*;

import com.squareup.picasso.Picasso;

import ru.vlitomsk.newsreader.news.News;

public class ImagesViewActivity extends AppCompatActivity {

    public static final String EXTR_IDS = "img_ids";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images_view);


        ListView lvImg = (ListView)findViewById(R.id.imgsViewLst);

        Context ctx = this;
        Intent intent = getIntent();
        List<String> imageIds = intent.getStringArrayListExtra(EXTR_IDS);

        ArrayAdapter listadaptor = new ArrayAdapter<String>(this, R.layout.activity_images_view, R.id.title, imageIds) {
            public View getView(int position, View convertView, ViewGroup container) {
                LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.imgview, null);
                ImageView imageView = (ImageView) convertView.findViewById(R.id.imgview);

                String imageId = imageIds.get(position);
                PriorityTaskManager.DOWNLOAD_MANAGER.schedule(PriorityTaskManager.HI_PRIO, ()-> {
//                    //Bitmap bmp = MainImagesStorage.getInstance().getImage(imageId).getImg();
                    runOnUiThread(() -> {
                        //imageView.setImageBitmap(bmp);
                        Picasso.with(ctx).load(imageId).into(imageView);
                    });
                });

                return convertView;
            }
        };
        lvImg.setAdapter(listadaptor);
    }
}
