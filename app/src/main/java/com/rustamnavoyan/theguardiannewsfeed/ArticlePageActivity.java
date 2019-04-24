package com.rustamnavoyan.theguardiannewsfeed;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rustamnavoyan.theguardiannewsfeed.manage.ArticleSaver;
import com.rustamnavoyan.theguardiannewsfeed.manage.ArticlesApiClient;
import com.rustamnavoyan.theguardiannewsfeed.models.Article;
import com.rustamnavoyan.theguardiannewsfeed.models.ArticleItem;
import com.rustamnavoyan.theguardiannewsfeed.models.data.Fields;
import com.squareup.picasso.Picasso;

import com.rustamnavoyan.theguardiannewsfeed.manage.PinnedArticleSaver;

import androidx.appcompat.app.AppCompatActivity;

public class ArticlePageActivity extends AppCompatActivity {
    public static final String EXTRA_ARTICLE_ITEM = "com.rustamnavoyan.theguardiannewsfeed.ARTICLE_ITEM";

    private ArticleItem mArticleItem;
    private Article mArticle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_page);

        mArticleItem = getIntent().getParcelableExtra(EXTRA_ARTICLE_ITEM);

        mArticle = new Article();
        mArticle.setArticleItem(mArticleItem);

        View progressBar = findViewById(R.id.progress_bar);
        TextView title = findViewById(R.id.article_title);
        ImageView image = findViewById(R.id.article_image);
        TextView textView = findViewById(R.id.article_content);

        new ArticlesApiClient().getArticleContents(mArticleItem.getApiUrl(), response -> runOnUiThread(() -> {
            Fields fields = response.getResponse().getContent().getFields();
            title.setText(response.getResponse().getContent().getWebTitle());
            Picasso.get().load(mArticleItem.getThumbnailUrl()).into(image);
            if (fields != null) {
                mArticle.setArticleBodyText(fields.getBodyText());
                textView.setText(fields.getBodyText());
                textView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);

                invalidateOptionsMenu();
            }
        }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.article, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.pin:
                togglePin();
                invalidateOptionsMenu();
                return true;

            case R.id.save:
                toggleSave();
                invalidateOptionsMenu();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void togglePin() {
        mArticleItem.setPinned(!mArticleItem.isPinned());
        new PinnedArticleSaver(this).execute(mArticleItem);
    }

    private void toggleSave() {
        mArticle.setSaved(!mArticle.isSaved());
        new ArticleSaver(this).execute(mArticle);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.pin).setTitle(mArticleItem.isPinned() ? R.string.unpin : R.string.pin);
        menu.findItem(R.id.save).setEnabled(mArticle.getArticleBodyText() != null);
        menu.findItem(R.id.save).setTitle(mArticle != null && mArticle.isSaved() ? R.string.delete : R.string.save);

        return true;
    }
}
