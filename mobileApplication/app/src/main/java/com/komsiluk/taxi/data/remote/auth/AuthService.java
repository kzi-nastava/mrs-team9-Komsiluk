package com.komsiluk.taxi.data.remote.auth;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

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

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @POST("tokens/reset-password")
    Call<Void> resetPassword(@Body ResetPasswordRequest request);

    @Multipart
    @POST("auth/registration/passenger")
    Call<PassengerRegistrationResponse> registerPassenger(
            @Part("data") RequestBody data,
            @Part MultipartBody.Part profileImage
    );

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @POST("tokens/activation/passenger")
    Call<Void> activatePassenger(@Body ActivatePassengerRequest request);

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @POST("auth/registration/resend")
    Call<Void> resendEmail(@Body ResendEmailRequest request);
}
