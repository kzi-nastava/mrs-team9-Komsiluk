package com.komsiluk.taxi.di;

import okhttp3.Interceptor;
import okhttp3.Response;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class RateLimitInterceptor implements Interceptor {
    private static final ReentrantLock lock = new ReentrantLock(true);

    @Override
    public Response intercept(Chain chain) throws IOException {
        String url = chain.request().url().toString();


        if (url.contains("photon.komoot.io") || url.contains("openstreetmap")) {
            lock.lock();
            try {
                try {
                    TimeUnit.MILLISECONDS.sleep(1500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return chain.proceed(chain.request());
            } finally {
                lock.unlock();
            }
        }

        return chain.proceed(chain.request());
    }
}