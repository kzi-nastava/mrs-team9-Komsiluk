package com.komsiluk.taxi.auth;

import com.komsiluk.taxi.data.session.SessionManager;

import java.io.IOException;

import javax.inject.Inject;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;



public class AuthInterceptor implements Interceptor {

    private final SessionManager sessionManager;

    @Inject
    public AuthInterceptor(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }


    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        String path = original.url().encodedPath();

        boolean isPublicPath = (path.contains("/api/auth/login")
                || path.contains("/api/auth/registration")
                || path.contains("/api/auth/forgot-password")
                || path.contains("/api/tokens")
                || path.equals("/api/drivers/locations")
                || path.equals("/api/drivers/basic"));

        if (!isPublicPath) {
            String token = sessionManager.getToken();
            if (token != null) {
                Request request = original.newBuilder()
                        .addHeader("Authorization", "Bearer " + token)
                        .build();
                return chain.proceed(request);
            }
        }

        return chain.proceed(original);
    }

}

