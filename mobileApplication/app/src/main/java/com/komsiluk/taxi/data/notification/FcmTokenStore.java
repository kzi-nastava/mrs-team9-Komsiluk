package com.komsiluk.taxi.data.notification;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;
import jakarta.inject.Inject;

@Singleton
public class FcmTokenStore {
    private static final String PREF = "push";
    private static final String KEY = "fcm_token";

    private final SharedPreferences prefs;
    @Inject
    public FcmTokenStore(@ApplicationContext Context context) {
        this.prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    public void save(String token) {
        prefs.edit().putString(KEY, token).apply();
    }

    public String get() {
        return prefs.getString(KEY, null);
    }
}