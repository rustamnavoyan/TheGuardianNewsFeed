package com.rustamnavoyan.theguardiannewsfeed.manage;

import android.content.Context;
import android.os.AsyncTask;

import com.rustamnavoyan.theguardiannewsfeed.database.DatabaseOpenHelper;
import com.rustamnavoyan.theguardiannewsfeed.models.ArticleItem;

public class PinnedArticleSaver extends AsyncTask<ArticleItem, Void, Void> {

    private Context mContext;

    public PinnedArticleSaver(Context context) {
        mContext = context;
    }

    @Override
    protected Void doInBackground(ArticleItem... articles) {
        DatabaseOpenHelper.getInstance(mContext).getArticleTable().updatePinnedArticle(articles[0]);

        return null;
    }
}
