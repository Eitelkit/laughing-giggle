package com.example.mxx62.findit;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by mxx62 on 2017/8/17.
 */

public class FindItApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();

        if  (FirebaseApp.getApps(this).isEmpty()){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }
    }
}
