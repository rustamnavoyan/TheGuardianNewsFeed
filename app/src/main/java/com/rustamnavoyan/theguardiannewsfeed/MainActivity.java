package com.rustamnavoyan.theguardiannewsfeed;

import android.os.Bundle;

import com.rustamnavoyan.theguardiannewsfeed.manage.NotificationManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.container, new MainFragment(), MainFragment.class.getSimpleName())
                    .commit();
        }

        NotificationManager.clearNotifications(this);
    }
}
