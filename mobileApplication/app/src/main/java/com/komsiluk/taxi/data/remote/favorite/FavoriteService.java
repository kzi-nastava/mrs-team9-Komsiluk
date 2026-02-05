package com.komsiluk.taxi.data.remote.favorite;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface FavoriteService {

    @GET("users/{userId}/favorites")
    Call<List<FavoriteRouteResponse>> getFavorites(@Path("userId") Long userId);

    @POST("users/{userId}/favorites")
    Call<FavoriteRouteResponse> addFavorite(@Path("userId") Long userId,
                                            @Body FavoriteRouteCreateRequest body);

    @PUT("favorites/{favoriteId}")
    Call<FavoriteRouteResponse> renameFavorite(@Path("favoriteId") Long favoriteId,
                                               @Body FavoriteRouteUpdateRequest body);

    @DELETE("/api/favorites/{favoriteId}")
    Call<Void> deleteFavorite(@Path("favoriteId") Long favoriteId);
}
