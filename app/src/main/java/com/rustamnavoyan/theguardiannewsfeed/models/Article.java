package com.rustamnavoyan.theguardiannewsfeed.models;

public class Article {
    private ArticleItem mArticleItem;
    private String mArticleBodyText;
    private boolean mSaved;

    public ArticleItem getArticleItem() {
        return mArticleItem;
    }

    public void setArticleItem(ArticleItem articleItem) {
        mArticleItem = articleItem;
    }

    public String getArticleBodyText() {
        return mArticleBodyText;
    }

    public void setArticleBodyText(String articleBodyText) {
        mArticleBodyText = articleBodyText;
    }

    public boolean isSaved() {
        return mSaved;
    }

    public void setSaved(boolean saved) {
        mSaved = saved;
    }
}
