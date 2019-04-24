package com.rustamnavoyan.theguardiannewsfeed.manage;

import android.content.Context;
import android.os.AsyncTask;

import com.rustamnavoyan.theguardiannewsfeed.database.DatabaseOpenHelper;
import com.rustamnavoyan.theguardiannewsfeed.models.Article;

public class ArticleSaver extends AsyncTask<Article, Void, Void> {

    private Context mContext;

    public ArticleSaver(Context context) {
        mContext = context;
    }

    @Override
    protected Void doInBackground(Article... articles) {
        DatabaseOpenHelper.getInstance(mContext).getArticleTable().updateArticle(articles[0]);

        return null;
    }
}
