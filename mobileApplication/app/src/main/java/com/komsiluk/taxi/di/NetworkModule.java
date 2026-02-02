package com.komsiluk.taxi.di;


import java.util.concurrent.TimeUnit;
import com.komsiluk.taxi.BuildConfig;
import com.komsiluk.taxi.auth.AuthInterceptor;
import com.komsiluk.taxi.auth.TokenAuthenticator;
import com.komsiluk.taxi.data.remote.auth.AuthService;
import com.komsiluk.taxi.data.remote.edit_requests.EditRequestsService;
import com.komsiluk.taxi.data.remote.profile.UserService;

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
    public static OkHttpClient provideOkHttpClient(AuthInterceptor authInterceptor, TokenAuthenticator tokenAuthenticator) {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(
                BuildConfig.DEBUG
                        ? HttpLoggingInterceptor.Level.BODY
                        : HttpLoggingInterceptor.Level.NONE
        );

        return new OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(logging)
                .authenticator(tokenAuthenticator)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
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

    @Provides
    @Singleton
    public static UserService provideUserService(Retrofit retrofit) {
        return retrofit.create(UserService.class);
    }

    @Provides
    @Singleton
    public static EditRequestsService provideEditRequestsService(Retrofit retrofit) {
        return retrofit.create(EditRequestsService.class);
    }
}

