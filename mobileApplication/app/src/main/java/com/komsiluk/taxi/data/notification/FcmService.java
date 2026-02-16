package com.komsiluk.taxi.data.notification;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.komsiluk.taxi.BuildConfig;
import com.komsiluk.taxi.data.session.SessionManager;

import dagger.hilt.android.AndroidEntryPoint;
import jakarta.inject.Inject;

@AndroidEntryPoint
public class FcmService extends FirebaseMessagingService {

    @Inject
    SessionManager session;
    @Inject
    PushTokenService api;
    @Inject
    FcmTokenStore tokenStore;

    private static final String BASE_URL = "http://"+ BuildConfig.IP_ADDR +":8081";

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);

        Log.d("PUSH", "New FCM token: " + token);

        tokenStore.save(token);

        String jwt = session.getToken();
        if (jwt == null || jwt.isBlank()) return;

        new Thread(() -> api.register(BASE_URL, jwt, token)).start();
    }

    @Override
    public void onMessageReceived(com.google.firebase.messaging.RemoteMessage message) {
        super.onMessageReceived(message);

        String title = "Neighbourhood Taxi";
        String body = "New notification";

        if (message.getNotification() != null) {
            if (message.getNotification().getTitle() != null) title = message.getNotification().getTitle();
            if (message.getNotification().getBody() != null) body = message.getNotification().getBody();
        }

        if (message.getData() != null && !message.getData().isEmpty()) {
            if (message.getData().containsKey("title")) title = message.getData().get("title");
            if (message.getData().containsKey("message")) body = message.getData().get("message");
        }

        NotificationHelper.show(this, title, body);
    }
}
