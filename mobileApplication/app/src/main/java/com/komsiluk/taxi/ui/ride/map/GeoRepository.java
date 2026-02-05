package com.komsiluk.taxi.ui.ride.map;

import org.osmdroid.util.GeoPoint;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GeoRepository {

    private final NominatimService nominatim;
    private final OsrmService osrm;

    public GeoRepository(OkHttpClient client) {
        Retrofit nom = new Retrofit.Builder()
                .baseUrl("https://nominatim.openstreetmap.org/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Retrofit o = new Retrofit.Builder()
                .baseUrl("https://router.project-osrm.org/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        nominatim = nom.create(NominatimService.class);
        osrm = o.create(OsrmService.class);
    }

    public Call<List<NominatimPlace>> searchNoviSad(String query, String viewbox) {
        return nominatim.search(
                query,
                "jsonv2",
                0,
                8,
                "rs",
                1,
                viewbox
        );
    }

    public Call<OsrmRouteResponse> route(double lonA, double latA, double lonB, double latB) {
        String coords = lonA + "," + latA + ";" + lonB + "," + latB;
        return osrm.route(coords, "full", "geojson");
    }

    public Call<OsrmRouteResponse> routeMulti(List<GeoPoint> pts) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pts.size(); i++) {
            GeoPoint p = pts.get(i);
            if (i > 0) sb.append(";");
            sb.append(p.getLongitude()).append(",").append(p.getLatitude());
        }
        String coords = sb.toString();

        return osrm.routeMulti(coords, "full", "geojson", false);
    }
}
