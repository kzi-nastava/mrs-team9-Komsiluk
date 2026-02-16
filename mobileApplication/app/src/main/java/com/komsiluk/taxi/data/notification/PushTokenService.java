package com.komsiluk.taxi.data.notification;

import android.util.Log;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import jakarta.inject.Inject;

public class PushTokenService {

    @Inject
    public PushTokenService() {}

    public void register(String baseUrl, String jwt, String fcmToken) {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(baseUrl + "/api/push/register");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(8000);
            conn.setDoOutput(true);

            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + jwt);

            JSONObject body = new JSONObject();
            body.put("token", fcmToken);

            byte[] bytes = body.toString().getBytes("UTF-8");
            try (OutputStream os = conn.getOutputStream()) {
                os.write(bytes);
                os.flush();
            }

            int code = conn.getResponseCode();
            Log.d("PUSH", "register token -> HTTP " + code);
        } catch (Exception e) {
            Log.w("PUSH", "register token failed: " + e.getMessage());
        } finally {
            if (conn != null) conn.disconnect();
        }
    }
}

