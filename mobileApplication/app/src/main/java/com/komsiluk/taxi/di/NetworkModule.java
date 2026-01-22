package com.komsiluk.taxi.di;


import java.util.concurrent.TimeUnit;
import com.komsiluk.taxi.BuildConfig;
import com.komsiluk.taxi.auth.AuthInterceptor;
import com.komsiluk.taxi.data.remote.auth.AuthService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {

    @Provides
    @Singleton
    public static OkHttpClient provideOkHttpClient(AuthInterceptor authInterceptor) {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(
                BuildConfig.DEBUG
                        ? HttpLoggingInterceptor.Level.BODY
                        : HttpLoggingInterceptor.Level.NONE
        );

        return new OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    @Provides
    @Singleton
    public static Retrofit provideRetrofit(OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl("http://"+ BuildConfig.IP_ADDR +":8081/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

    @Provides
    @Singleton
    public static AuthService provideAuthService(Retrofit retrofit) {
        return retrofit.create(AuthService.class);
    }
}

