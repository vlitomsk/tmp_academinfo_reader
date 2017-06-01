package ru.vlitomsk.newsreader;

import android.content.Context;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import android.content.*;
import java.util.*;
import ru.vlitomsk.newsreader.news.*;

class NewsItemAdapter extends BaseAdapter implements Filterable {

    Context context;
    private List<News> originalNewsList = new ArrayList<>();
    private List<News> newsList = new ArrayList<>();
    private static LayoutInflater inflater = null;

    public NewsItemAdapter(Context context, List<News> newsList) {
        // TODO Auto-generated constructor stub
        this.context = context;
        for (News n : newsList) {
            if (n != null) {
                this.newsList.add(n);
            }
        }
        originalNewsList.clear();
        originalNewsList.addAll(this.newsList);
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public List<String> getFilterVariants() {
        Collection<String> variants = new HashSet<>();
        for (News news : newsList) {
            variants.add(news.getTheme());
        }
        List<String> result = new ArrayList<>();
        result.add("<Any news>");
        for (String s : variants) {
            result.add(s);
        }
        return result;
    }

    public void updateNewsList(List<News> newsList) {
        this.newsList.clear();;
        this.originalNewsList.clear();
        this.newsList.addAll(newsList);
        this.originalNewsList.addAll(newsList);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return newsList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return newsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return newsList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.row, null);
        TextView textView = (TextView) vi.findViewById(R.id.text);
        TextView headerView = (TextView) vi.findViewById(R.id.header);

        News news = newsList.get(position);
        textView.setText(news.getTheme());
        headerView.setText(news.getTitle());
        return vi;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<News> filteredNews = new ArrayList<>();
                for (News news : originalNewsList) {
                    if (news.getTheme().equals(constraint)) {
                        filteredNews.add(news);
                    }
                }
                if (filteredNews.isEmpty()) {
                    filteredNews = originalNewsList;
                }
                results.count = filteredNews.size();
                results.values = filteredNews;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                newsList = (List<News>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}

public class NewsListActivity extends AppCompatActivity {
    private int topk = 0;
    private static final int TOPK_INCREMENT = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);

        ListView lvMain = (ListView) findViewById(R.id.newsList);
        Context ctx = this;
        NewsItemAdapter newsItemAdapter = new NewsItemAdapter(ctx, new ArrayList<>());
        lvMain.setAdapter(newsItemAdapter);

        lvMain.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(ctx, ShowNewsActivity.class);
            intent.putExtra(ShowNewsActivity.EXTR_NEWS_ID, (int) id);
            startActivity(intent);
        });

        Button btnRefresh = (Button) findViewById(R.id.btnRefresh);
        Button btnMore = (Button) findViewById(R.id.btnMore);
        Spinner filterSpinner = (Spinner) findViewById(R.id.filterSpinner);
        ArrayAdapter<String> filterSpinnerAdapter = new ArrayAdapter<String>(ctx, R.layout.support_simple_spinner_dropdown_item, newsItemAdapter.getFilterVariants());
        filterSpinner.setAdapter(filterSpinnerAdapter);

        Runnable doRefresh = () ->
        PriorityTaskManager.DOWNLOAD_MANAGER.schedule(PriorityTaskManager.HI_PRIO, () -> {
            List<News> newsList = MainNewsStorage.getInstance().getTopKMaybeTeaser(topk);
            runOnUiThread(() -> {
                newsItemAdapter.updateNewsList(newsList);
                newsItemAdapter.notifyDataSetChanged();
                filterSpinnerAdapter.clear();
                filterSpinnerAdapter.addAll(newsItemAdapter.getFilterVariants());
                filterSpinnerAdapter.notifyDataSetChanged();
            });
        });

        if (topk == 0) {
            topk += TOPK_INCREMENT;
            doRefresh.run();
        }
        btnMore.setOnClickListener(v -> { topk += TOPK_INCREMENT; doRefresh.run(); });
        btnRefresh.setOnClickListener(v -> doRefresh.run());

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String filterVar = filterSpinner.getSelectedItem().toString();
                newsItemAdapter.getFilter().filter(filterVar);
                //newsItemAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
}
