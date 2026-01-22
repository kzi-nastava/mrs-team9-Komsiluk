package com.komsiluk.taxi.data.remote.auth;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface AuthService {


    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @POST("auth/forgot-password")
    Call<Void> forgotPassword(@Body ForgotPasswordRequest request);
}
