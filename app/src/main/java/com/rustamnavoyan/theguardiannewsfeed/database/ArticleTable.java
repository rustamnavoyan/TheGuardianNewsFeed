package com.rustamnavoyan.theguardiannewsfeed.database;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

import com.rustamnavoyan.theguardiannewsfeed.models.Article;
import com.rustamnavoyan.theguardiannewsfeed.models.ArticleItem;
import com.rustamnavoyan.theguardiannewsfeed.utils.CursorUtil;
import com.rustamnavoyan.theguardiannewsfeed.utils.IOUtil;

public class ArticleTable {
    public static final String TABLE_NAME = "items";
    public static final Uri CONTENT_URI = Uri.parse("content://" + NewsFeedContentProvider.AUTHORITY + "/" + TABLE_NAME);

    public static final int ARTICLE = 0;

    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        MATCHER.addURI(NewsFeedContentProvider.AUTHORITY, TABLE_NAME, ARTICLE);
    }

    public static class Columns implements BaseColumns {
        public static final String ARTICLE_ID = "_article_id";
        public static final String THUMBNAIL_URL = "_thumbnail_url";
        public static final String TITLE = "_title";
        public static final String CATEGORY = "_category";
        public static final String API_URL = "_api_url";
        public static final String BODY_TEXT = "_body_text";
        public static final String PINNED = "_pinned";
    }

    private static final String CREATE_ARTICLE_TABLE = String.format(
            "CREATE TABLE %s ("
                    + "%s INTEGER PRIMARY KEY AUTOINCREMENT, "  // ID
                    + "%s TEXT, " // form id
                    + "%s TEXT, " // form id
                    + "%s TEXT, " // form id
                    + "%s TEXT, " // form id
                    + "%s TEXT, " // form id
                    + "%s TEXT, " // form id
                    + "%s INTEGER " // type
                    + ");",
            TABLE_NAME,
            Columns._ID,
            Columns.ARTICLE_ID,
            Columns.THUMBNAIL_URL,
            Columns.TITLE,
            Columns.CATEGORY,
            Columns.API_URL,
            Columns.BODY_TEXT,
            Columns.PINNED
    );

    private DatabaseOpenHelper mOpenHelper;

    public ArticleTable(DatabaseOpenHelper openHelper) {
        mOpenHelper = openHelper;
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ARTICLE_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean matches(Uri uri) {
        return MATCHER.match(uri) != UriMatcher.NO_MATCH;
    }

    protected void notifyChange() {
        mOpenHelper.getContext().getContentResolver().notifyChange(CONTENT_URI, null);
    }

    private SQLiteDatabase openReadable() {
        return mOpenHelper.getReadableDatabase();
    }

    private SQLiteDatabase openWritable() {
        return mOpenHelper.getWritableDatabase();
    }

    private boolean existsPinned(String articleId) {
        Cursor cursor = queryPinned(articleId);
        boolean exists = cursor.getCount() > 0;
        IOUtil.closeQuietly(cursor);
        return exists;
    }

    public Cursor queryPinned(String article_id) {
        String selection = Columns.ARTICLE_ID + " = ? AND " + Columns.PINNED + " = ?";
        return openReadable().query(TABLE_NAME,null, selection, new String[]{article_id, "1"}, null, null, null, null);
    }

    private boolean existsSaved(String articleId) {
        Cursor cursor = querySaved(articleId);
        boolean exists = cursor.getCount() > 0;
        IOUtil.closeQuietly(cursor);
        return exists;
    }

    public Cursor querySaved(String article_id) {
        String selection = Columns.ARTICLE_ID + " = ? AND " + Columns.BODY_TEXT + " IS NOT NULL";
        return openReadable().query(TABLE_NAME,null, selection, new String[]{article_id}, null, null, null, null);
    }

    public void updatePinnedArticle(ArticleItem articleItem) {
        ContentValues contentValues = makeContentValues(articleItem);
        String whereClause = Columns.ARTICLE_ID + " = ? AND " + Columns.PINNED + " != ?";
        String[] whereArgs = {articleItem.getId(), String.valueOf(0)};

        if (articleItem.isPinned()) {
            if (existsPinned(articleItem.getId())) {
                openWritable().update(TABLE_NAME,  contentValues, whereClause, whereArgs);
            } else {
                openWritable().insert(TABLE_NAME, null, contentValues);
            }
        } else {
            openWritable().delete(TABLE_NAME, whereClause, whereArgs);
        }

        notifyChange();
    }

    public void updateArticle(Article article) {
        ContentValues contentValues = makeContentValues(article);
        String whereClause = Columns.ARTICLE_ID + " = ? AND " + Columns.BODY_TEXT + " IS NOT NULL";
        if (article.isSaved()) {
            if (existsSaved(article.getArticleItem().getId())) {
                openWritable().update(TABLE_NAME,  contentValues,
                        whereClause, new String[]{article.getArticleItem().getId()});
            } else {
                openWritable().insert(TABLE_NAME, null, contentValues);
            }
        } else {
            openWritable().delete(TABLE_NAME,whereClause, new String[]{article.getArticleItem().getId()});
        }

        notifyChange();
    }

    private static ContentValues makeContentValues(Article article) {
        ContentValues values = makeContentValues(article.getArticleItem());
        values.put(Columns.BODY_TEXT, article.getArticleBodyText());

        return values;
    }

    private static ContentValues makeContentValues(ArticleItem article) {
        ContentValues values = new ContentValues();
        values.put(Columns.ARTICLE_ID, article.getId());
        values.put(Columns.THUMBNAIL_URL, article.getThumbnailUrl());
        values.put(Columns.TITLE, article.getTitle());
        values.put(Columns.CATEGORY, article.getCategory());
        values.put(Columns.API_URL, article.getApiUrl());
        values.put(Columns.PINNED, toInt(article.isPinned()));

        return values;
    }

    public static ArticleItem parse(Cursor cursor) {
        ArticleItem article = new ArticleItem(CursorUtil.getString(cursor, Columns.ARTICLE_ID));
        article.setThumbnailUrl(CursorUtil.getString(cursor, Columns.THUMBNAIL_URL));
        article.setTitle(CursorUtil.getString(cursor, Columns.TITLE));
        article.setCategory(CursorUtil.getString(cursor, Columns.CATEGORY));
        article.setApiUrl(CursorUtil.getString(cursor, Columns.API_URL));
        article.setPinned(CursorUtil.getBoolean(cursor, Columns.PINNED));

        return article;
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        switch (MATCHER.match(uri)) {
            case ARTICLE:
                String withPinnedSelection = selection == null || selection.isEmpty()
                        ? Columns.PINNED + " != ?" : selection + " AND " + Columns.PINNED + " != ?";
                int length = selectionArgs == null || selectionArgs.length == 0
                        ? 1 : selectionArgs.length + 1;
                String[] withPinnedSelectionArgs = new String[length];
                withPinnedSelectionArgs[length - 1] = String.valueOf(0);

                cursor = mOpenHelper.getReadableDatabase().query(TABLE_NAME, projection, withPinnedSelection,
                        withPinnedSelectionArgs, null, null, sortOrder);
                break;

            default:
                return null;
        }

        if (cursor != null) {
            cursor.setNotificationUri(mOpenHelper.getContext().getContentResolver(), uri);
        }

        return cursor;
    }

    public static int toInt(boolean value) {
        return value ? 1 : 0;
    }
}
