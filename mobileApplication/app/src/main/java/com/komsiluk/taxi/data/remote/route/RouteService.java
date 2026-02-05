package com.komsiluk.taxi.data.remote.route;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RouteService {

    @POST("routes/find-or-create")
    Call<RouteResponse> findOrCreate(@Body RouteCreateRequest body);
}
