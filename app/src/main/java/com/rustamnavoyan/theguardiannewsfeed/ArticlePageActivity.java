package com.rustamnavoyan.theguardiannewsfeed;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rustamnavoyan.theguardiannewsfeed.manage.ArticlesApiClient;
import com.rustamnavoyan.theguardiannewsfeed.models.ArticleItem;
import com.rustamnavoyan.theguardiannewsfeed.models.data.Fields;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;

public class ArticlePageActivity extends AppCompatActivity {
    public static final String EXTRA_ARTICLE_ITEM = "com.rustamnavoyan.theguardiannewsfeed.ARTICLE_ITEM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_page);

        ArticleItem articleItem = getIntent().getParcelableExtra(EXTRA_ARTICLE_ITEM);

        View progressBar = findViewById(R.id.progress_bar);
        TextView title = findViewById(R.id.article_title);
        ImageView image = findViewById(R.id.article_image);
        TextView textView = findViewById(R.id.article_content);

        new ArticlesApiClient().getArticleContents(articleItem.getApiUrl(), response -> runOnUiThread(() -> {
            Fields fields = response.getResponse().getContent().getFields();
            title.setText(response.getResponse().getContent().getWebTitle());
            Picasso.get().load(articleItem.getThumbnailUrl()).into(image);
            if (fields != null) {
                textView.setText(fields.getBodyText());
                textView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        }));
    }
}
