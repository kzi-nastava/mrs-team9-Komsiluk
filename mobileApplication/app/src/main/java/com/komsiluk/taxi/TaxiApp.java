package com.komsiluk.taxi;
import android.app.Application;

import org.osmdroid.config.Configuration;

import dagger.hilt.android.HiltAndroidApp;
@HiltAndroidApp
public class TaxiApp extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
    }
}
