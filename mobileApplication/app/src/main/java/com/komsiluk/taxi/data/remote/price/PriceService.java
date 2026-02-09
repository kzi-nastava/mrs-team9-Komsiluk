package com.komsiluk.taxi.data.remote.price;


import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PriceService {
    @GET("admin/pricing")
    Call<List<PriceResponse>> getAll();

    @PUT("admin/pricing/{type}")
    Call<PriceResponse> update(
            @Path("type") String type,
            @Body PriceUpdate update
    );
}