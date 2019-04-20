package com.rustamnavoyan.theguardiannewsfeed.manage;

import com.rustamnavoyan.theguardiannewsfeed.models.ArticleItem;
import com.rustamnavoyan.theguardiannewsfeed.models.data.Result;

import java.util.ArrayList;
import java.util.List;

public class ArticleDownloader {
    public interface OnDownloadCallback {
        void onDownloaded(List<ArticleItem> articleItems);
    }

    private static final int PAGE_SIZE = 10;

    public void downloadArticleList(int page, OnDownloadCallback callback) {
        downloadArticleList(page, PAGE_SIZE, callback);
    }

    public void downloadArticleList(int page, int pageSize, OnDownloadCallback callback) {
        ArticlesApiClient articlesApiClient = new ArticlesApiClient();
        articlesApiClient.getArticleList(page, pageSize, response -> {
            List<Result> results = response.getResponse().getResults();
            List<ArticleItem> articleItems = new ArrayList<>();
            for (Result result : results) {
                ArticleItem articleItem = new ArticleItem(result.getId());
                articleItem.setTitle(result.getWebTitle());
                // TODO Probably this is not the category
                articleItem.setCategory(result.getPillarName());
                if (result.getFields() != null) {
                    articleItem.setThumbnailUrl(result.getFields().getThumbnail());
                }
                articleItem.setApiUrl(result.getApiUrl());
                articleItem.setPublishedDate(result.getWebPublicationDate());
                articleItems.add(articleItem);
            }

            callback.onDownloaded(articleItems);
        });
    }
}
