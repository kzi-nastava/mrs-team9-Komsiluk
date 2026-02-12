package com.komsiluk.taxi.di;


import java.util.concurrent.TimeUnit;
import com.komsiluk.taxi.BuildConfig;
import com.komsiluk.taxi.auth.AuthInterceptor;
import com.komsiluk.taxi.auth.TokenAuthenticator;
import com.komsiluk.taxi.data.remote.add_driver.UserTokenService;
import com.komsiluk.taxi.data.remote.admin_ride_history.AdminRideHistoryService;
import com.komsiluk.taxi.data.remote.auth.AuthService;
import com.komsiluk.taxi.data.remote.inconsistency_report.InconsistencyService;
import com.komsiluk.taxi.data.remote.passenger_ride_history.PassengerRideHistoryService;
import com.komsiluk.taxi.data.remote.block.BlockService;
import com.komsiluk.taxi.data.remote.driver_history.DriverService;
import com.komsiluk.taxi.data.remote.edit_requests.EditRequestsService;
import com.komsiluk.taxi.data.remote.favorite.FavoriteService;
import com.komsiluk.taxi.data.remote.location.LocationService;
import com.komsiluk.taxi.data.remote.price.PriceService;
import com.komsiluk.taxi.data.remote.profile.UserService;
import com.komsiluk.taxi.data.remote.rating.RatingService;
import com.komsiluk.taxi.data.remote.report.ReportService;
import com.komsiluk.taxi.data.remote.ride.RideRepository;
import com.komsiluk.taxi.data.remote.ride.RideService;
import com.komsiluk.taxi.data.remote.route.RouteService;
import com.komsiluk.taxi.ui.ride.map.GeoRepository;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import okhttp3.Request;
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
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(logging)

                .addInterceptor(chain -> {
                    okhttp3.Request request = chain.request().newBuilder()
                            .header("User-Agent", "KomsilukTaxiApp/1.0 (u.milinovic@gmail.com)")
                            .build();
                    return chain.proceed(request);
                })

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
    public static PassengerRideHistoryService providePassengerRideHistoryService(Retrofit retrofit) {
        return retrofit.create(PassengerRideHistoryService.class);
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
    @Provides
    @Singleton
    public static DriverService provideDriverService(Retrofit retrofit) {
        return retrofit.create(DriverService.class);
    }

    @Provides
    @Singleton
    public static UserTokenService provideUserTokenService(Retrofit retrofit) {
        return retrofit.create(UserTokenService.class);
    }

    @Provides
    @Singleton
    public static RideService provideRideApi(Retrofit retrofit) {
        return retrofit.create(RideService.class);
    }

    @Provides
    @Singleton
    public static RideRepository provideRideRepository(RideService api) {
        return new RideRepository(api);
    }

    @Provides
    @Singleton
    public static FavoriteService provideFavoriteService(Retrofit retrofit) {
        return retrofit.create(FavoriteService.class);
    }

    @Provides
    @Singleton
    public static RouteService provideRouteService(Retrofit retrofit) {
        return retrofit.create(RouteService.class);
    }

    @Provides
    @Singleton
    public static ReportService provideReportService(Retrofit retrofit) {
        return retrofit.create(ReportService.class);
    }

    @Provides
    @Singleton
    public static BlockService provideBlockService(Retrofit retrofit) {
        return retrofit.create(BlockService.class);
    }

    @Provides
    @Singleton
    public static LocationService provideLocationService(Retrofit retrofit) {
        return retrofit.create(LocationService.class);
    }

    @Provides
    @Singleton
    public static AdminRideHistoryService provideAdminRideHistoryService(Retrofit retrofit) {
        return retrofit.create(AdminRideHistoryService.class);
    }

    @Provides
    @Singleton
    public static InconsistencyService provideInconsistencyService(Retrofit retrofit) {
        return retrofit.create(InconsistencyService.class);
    }

    @Provides
    @Singleton
    public static RatingService provideRatingService(Retrofit retrofit) {
        return retrofit.create(RatingService.class);
    }

    @Provides
    @Singleton
    public static PriceService providePriceService(Retrofit retrofit) {
        return retrofit.create(PriceService.class);
    }

    @Provides
    @Singleton
    @Named("GeoClient")
    OkHttpClient provideGeoHttpClient() {
        okhttp3.Dispatcher dispatcher = new okhttp3.Dispatcher();
        dispatcher.setMaxRequests(1);
        dispatcher.setMaxRequestsPerHost(1);

        return new OkHttpClient.Builder()
                .dispatcher(dispatcher)
                .addNetworkInterceptor(new RateLimitInterceptor()) // Naša kočnica
                .addInterceptor(chain -> {
                    okhttp3.Request newRequest = chain.request().newBuilder()
                            .header("User-Agent", "Mozilla/5.0 (Linux; Android 13; SM-S908B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Mobile Safari/537.36")
                            .header("Accept", "text/html,application/xhtml+xml,application/json,xml;q=0.9,*/*;q=0.8")
                            .header("Accept-Language", "sr-RS,sr;q=0.9,en-US;q=0.8,en;q=0.7")
                            .removeHeader("Referer") // Obavezno ukloni ovo
                            .build();
                    return chain.proceed(newRequest);
                })
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    @Provides
    @Singleton
    GeoRepository provideGeoRepository(@Named("GeoClient") OkHttpClient client) {
        return new GeoRepository(client);
    }
}

