package com.rustamnavoyan.theguardiannewsfeed.manage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ArticleDownloadReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, DownloadService.class);
        context.startService(service);
    }
}
