package com.komsiluk.taxi.data.remote.favorite;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FavoriteRouteUpdateRequest {

    @Expose @SerializedName("title")
    private String title;

    public FavoriteRouteUpdateRequest() {}
    public FavoriteRouteUpdateRequest(String title) { this.title = title; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}
