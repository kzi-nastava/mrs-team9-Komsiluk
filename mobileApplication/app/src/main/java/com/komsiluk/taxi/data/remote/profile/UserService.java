package com.komsiluk.taxi.data.remote.profile;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface UserService {

    @Headers({
            "User-Agent: Mobile-Android",
            "Content-Type:application/json"
    })
    @GET("users/{id}/profile")
    Call<UserProfileResponse> getProfile(@Path("id") Long id);
}
