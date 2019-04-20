package com.rustamnavoyan.theguardiannewsfeed;

import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rustamnavoyan.theguardiannewsfeed.database.ArticleTable;
import com.rustamnavoyan.theguardiannewsfeed.manage.ArticleSaver;
import com.rustamnavoyan.theguardiannewsfeed.manage.ArticlesApiClient;
import com.rustamnavoyan.theguardiannewsfeed.manage.PeriodicDownloadManager;
import com.rustamnavoyan.theguardiannewsfeed.models.Article;
import com.rustamnavoyan.theguardiannewsfeed.models.ArticleItem;
import com.rustamnavoyan.theguardiannewsfeed.models.data.Fields;
import com.rustamnavoyan.theguardiannewsfeed.utils.ConnectionUtil;
import com.squareup.picasso.Picasso;

import com.rustamnavoyan.theguardiannewsfeed.manage.PinnedArticleSaver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

public class ArticlePageActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    public static final String EXTRA_ARTICLE_ITEM = "com.rustamnavoyan.theguardiannewsfeed.ARTICLE_ITEM";
    private static final int SAVED_ARTICLE_LOADER_ID = 1;

    private ArticleItem mArticleItem;
    private Article mArticle;
    private boolean mConnected;

    private ImageView mImageView;
    private TextView mTitleView;
    private TextView mContentView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_page);

        mArticleItem = getIntent().getParcelableExtra(EXTRA_ARTICLE_ITEM);

        mArticle = new Article();
        mArticle.setArticleItem(mArticleItem);

        mImageView = findViewById(R.id.article_image);
        mTitleView = findViewById(R.id.article_title);
        mContentView = findViewById(R.id.article_content);
        mProgressBar = findViewById(R.id.progress_bar);

        mConnected = ConnectionUtil.isConnected(this);
        if (mConnected) {
            new ArticlesApiClient().getArticleContents(mArticleItem.getApiUrl(), response -> runOnUiThread(() -> {
                Fields fields = response.getResponse().getContent().getFields();
                if (fields != null) {
                    mArticle.setArticleBodyText(fields.getBodyText());
                }
                updateUI();

                invalidateOptionsMenu();
            }));
        } else {
            LoaderManager.getInstance(this).initLoader(SAVED_ARTICLE_LOADER_ID, null, this);
        }
    }

    void updateUI() {
        Picasso.get().load(mArticleItem.getThumbnailUrl()).into(mImageView);
        mTitleView.setText(mArticleItem.getTitle());
        mContentView.setText(mArticle.getArticleBodyText());
        mContentView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }


    @Override
    public void onResume() {
        super.onResume();

        PeriodicDownloadManager.cancel(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        PeriodicDownloadManager.schedule(this);
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

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(this, ArticleTable.SAVED_CONTENT_URI, null,
                ArticleTable.Columns.ARTICLE_ID + " = ? ", new String[]{mArticleItem.getId()}, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (data != null && data.getCount() > 0 && data.moveToFirst()) {
            mArticle = ArticleTable.parseArticle(data);
            updateUI();
            invalidateOptionsMenu();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
