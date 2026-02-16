package com.komsiluk.taxi;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import org.osmdroid.config.Configuration;

import dagger.hilt.android.HiltAndroidApp;
@HiltAndroidApp
public class TaxiApp extends Application{

    public static final String CHANNEL_ID_GENERAL = "general_notifications";

    @Override
    public void onCreate() {
        super.onCreate();
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);
        createNotificationChannels();
    }

    private void createNotificationChannels() {
        NotificationChannel ch = new NotificationChannel(
                CHANNEL_ID_GENERAL,
                "General",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        ch.setDescription("Ride updates and reminders");

        NotificationManager nm = getSystemService(NotificationManager.class);
        nm.createNotificationChannel(ch);
    }
}
