package com.rustamnavoyan.theguardiannewsfeed.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "article_database";

    private static final int VERSION_1 = 1; // Create database
    private static final int DATABASE_VERSION = VERSION_1;

    private static DatabaseOpenHelper instance;

    public static DatabaseOpenHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseOpenHelper(context);
        }

        return instance;
    }

    private Context mContext;
    private ArticleTable mArticleTable;

    public DatabaseOpenHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        mContext = context;

        // TODO Exposing not fully created object is a bad practice
        mArticleTable = new ArticleTable(this);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        mArticleTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        mArticleTable.onUpgrade(db, oldVersion, newVersion);
    }

    public Context getContext() {
        return mContext;
    }

    public ArticleTable getArticleTable() {
        return mArticleTable;
    }
}
