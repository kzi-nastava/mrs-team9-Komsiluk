package com.komsiluk.taxi.data.remote.location;

import java.util.Collection;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface LocationService {
    @GET("drivers/locations")
    Call<Collection<DriverLocationResponse>> getAllActiveDriverLocations();

    @PUT("drivers/{id}/location")
    Call<Void> updateLocation(@Path("id") Long id, @Body DriverLocationUpdate dto);
}