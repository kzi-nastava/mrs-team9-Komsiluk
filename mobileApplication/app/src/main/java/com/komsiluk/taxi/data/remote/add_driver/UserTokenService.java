package com.komsiluk.taxi.data.remote.add_driver;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserTokenService {

    @POST("tokens/activation")
    Call<Void> activate(@Body UserTokenActivationRequest request);
}