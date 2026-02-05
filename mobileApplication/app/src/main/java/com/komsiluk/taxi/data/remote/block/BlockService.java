package com.komsiluk.taxi.data.remote.block;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface BlockService {

    @POST("blocks")
    Call<BlockNoteResponse> create(@Body BlockNoteCreateRequest body);

    @GET("blocks/user/{userId}")
    Call<BlockNoteResponse> getForUser(@Path("userId") long userId);
}
