package com.komsiluk.taxi.ui.ride.map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OsrmService {
    @GET("route/v1/driving/{coords}")
    Call<OsrmRouteResponse> route(
            @Path("coords") String coords,
            @Query("overview") String overview,
            @Query("geometries") String geometries
    );

    @GET("route/v1/driving/{coords}")
    Call<OsrmRouteResponse> routeMulti(
            @Path(value = "coords", encoded = true) String coords,
            @Query("overview") String overview,
            @Query("geometries") String geometries,
            @Query("steps") boolean steps
    );
}
