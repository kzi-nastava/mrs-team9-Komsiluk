package com.komsiluk.taxi.data.remote.ride;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RideService {
    @POST("rides")
    Call<RideResponse> orderRide(@Body RideCreateRequest body);
}
