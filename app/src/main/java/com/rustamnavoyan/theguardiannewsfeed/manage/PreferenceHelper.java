package com.rustamnavoyan.theguardiannewsfeed.manage;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Date;

public class PreferenceHelper {
    private static final String LAST_UPDATED_KEY = "last_updated_key";

    private static PreferenceHelper instance;

    public static PreferenceHelper getInstance(Context context) {
        if (instance == null) {
            instance = new PreferenceHelper(context);
        }

        return instance;
    }



    private SharedPreferences mPreferences;

    private PreferenceHelper(Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void saveDate(Date date) {
        mPreferences.edit().putLong(LAST_UPDATED_KEY, date.getTime()).apply();
    }

    public Date getDate() {
        return new Date(mPreferences.getLong(LAST_UPDATED_KEY, new Date().getTime()));
    }
}
