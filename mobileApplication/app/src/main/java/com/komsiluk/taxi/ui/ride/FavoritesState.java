package com.komsiluk.taxi.ui.ride;

import com.komsiluk.taxi.data.remote.favorite.FavoriteRouteResponse;

import java.util.List;

public class FavoritesState {
    public boolean loading;
    public String error;
    public List<FavoriteRouteResponse> data;

    public static FavoritesState loading() {
        FavoritesState s = new FavoritesState();
        s.loading = true;
        return s;
    }

    public static FavoritesState error(String msg) {
        FavoritesState s = new FavoritesState();
        s.error = msg;
        return s;
    }

    public static FavoritesState data(List<FavoriteRouteResponse> d) {
        FavoritesState s = new FavoritesState();
        s.data = d;
        return s;
    }
}
