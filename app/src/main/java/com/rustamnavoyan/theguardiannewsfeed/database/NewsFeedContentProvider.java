package com.rustamnavoyan.theguardiannewsfeed.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.rustamnavoyan.theguardiannewsfeed.BuildConfig;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class NewsFeedContentProvider extends ContentProvider {

    public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".article";

    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor = get(uri).query(uri, projection, selection, selectionArgs, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        return 0;
    }

    private ArticleTable get(Uri uri) {
        DatabaseOpenHelper helper = DatabaseOpenHelper.getInstance(getContext());
        if (helper.getArticleTable().matches(uri)) {
            return helper.getArticleTable();
        }

        throw new IllegalArgumentException("Unknown URI: " + uri);
    }
}
