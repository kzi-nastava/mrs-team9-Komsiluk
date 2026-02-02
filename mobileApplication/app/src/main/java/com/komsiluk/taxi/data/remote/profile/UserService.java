package com.komsiluk.taxi.data.remote.profile;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface UserService {

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("users/{id}/profile")
    Call<UserProfileResponse> getProfile(@Path("id") Long id);

    @Multipart
    @PUT("users/{id}/profile-image")
    Call<UserProfileResponse> updateProfileImage(
            @Path("id") Long id,
            @Part MultipartBody.Part image
    );
}
