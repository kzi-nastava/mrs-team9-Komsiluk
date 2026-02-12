package com.komsiluk.taxi.ui.ride.map;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okio.Timeout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class GeoRepository {

    private final GeoApiService api;

    interface GeoApiService {
        @GET("https://photon.komoot.io/api/")
        Call<PhotonResponse> searchLocation(@Query("q") String query, @Query("limit") int limit);

        @GET("http://router.project-osrm.org/route/v1/driving/{coordinates}?overview=full&geometries=geojson")
        Call<OsrmRouteResponse> route(@Path(value = "coordinates", encoded = true) String coordinates);
    }

    @Inject
    public GeoRepository(OkHttpClient client) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://photon.komoot.io/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.api = retrofit.create(GeoApiService.class);
    }

    public Call<OsrmRouteResponse> routeMulti(List<GeoPoint> points) {
        if (points == null || points.isEmpty()) return null;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < points.size(); i++) {
            GeoPoint p = points.get(i);
            sb.append(p.getLongitude())
                    .append(",")
                    .append(p.getLatitude());

            if (i < points.size() - 1) {
                sb.append(";");
            }
        }

        return api.route(sb.toString());
    }
    public Call<List<NominatimPlace>> searchNoviSad(String query, String viewboxIgnored) {
        String finalQuery = query + " Novi Sad";

        return new CallAdapter<>(api.searchLocation(finalQuery, 5));
    }

    public Call<OsrmRouteResponse> route(double startLon, double startLat, double endLon, double endLat) {
        List<GeoPoint> points = new ArrayList<>();
        points.add(new GeoPoint(startLat, startLon));
        points.add(new GeoPoint(endLat, endLon));

        return routeMulti(points);
    }

    private static class CallAdapter<T> implements Call<List<NominatimPlace>> {
        private final Call<PhotonResponse> originalCall;

        CallAdapter(Call<PhotonResponse> originalCall) {
            this.originalCall = originalCall;
        }

        @Override
        public void enqueue(Callback<List<NominatimPlace>> callback) {
            originalCall.enqueue(new Callback<PhotonResponse>() {
                @Override
                public void onResponse(Call<PhotonResponse> call, Response<PhotonResponse> response) {
                    List<NominatimPlace> list = new ArrayList<>();

                    if (response.isSuccessful() && response.body() != null && response.body().features != null) {
                        for (PhotonFeature f : response.body().features) {
                            NominatimPlace p = new NominatimPlace();
                            if (f.geometry != null && f.geometry.coordinates != null && f.geometry.coordinates.size() >= 2) {
                                p.lon = String.valueOf(f.geometry.coordinates.get(0));
                                p.lat = String.valueOf(f.geometry.coordinates.get(1));
                            }
                            if (f.properties != null) {
                                p.displayName = f.properties.getDisplayName();
                            }
                            list.add(p);
                        }
                    }
                    callback.onResponse(CallAdapter.this, Response.success(list));
                }

                @Override
                public void onFailure(Call<PhotonResponse> call, Throwable t) {
                    callback.onFailure(CallAdapter.this, t);
                }
            });
        }

        @Override public boolean isExecuted() { return originalCall.isExecuted(); }
        @Override public void cancel() { originalCall.cancel(); }
        @Override public boolean isCanceled() { return originalCall.isCanceled(); }
        @Override public Call<List<NominatimPlace>> clone() { return new CallAdapter<>(originalCall.clone()); }
        @Override public Request request() { return originalCall.request(); }
        @Override public Timeout timeout() { return originalCall.timeout(); }
        @Override public Response<List<NominatimPlace>> execute() { return null; }
    }
}