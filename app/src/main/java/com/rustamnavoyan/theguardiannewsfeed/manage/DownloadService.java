package com.rustamnavoyan.theguardiannewsfeed.manage;

import android.content.Intent;

import com.rustamnavoyan.theguardiannewsfeed.utils.DateTimeUtil;

import java.util.Date;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

public class DownloadService extends JobIntentService {
    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        new ArticleDownloader().downloadArticleList(1, 1, articleItems -> {
            Date savedDate = PreferenceHelper.getInstance(this).getDate();
            Date date = DateTimeUtil.parseDate(articleItems.get(0).getPublishedDate());

            if (date.getTime() > savedDate.getTime()) {
                NotificationManager.showNewsFeedNotification(this);
            }
        });

    }
}
