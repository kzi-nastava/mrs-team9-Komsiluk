package com.komsiluk.taxi.ui.ride;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.komsiluk.taxi.data.remote.favorite.FavoriteService;
import com.komsiluk.taxi.data.remote.favorite.FavoriteRouteCreateRequest;
import com.komsiluk.taxi.data.remote.favorite.FavoriteRouteResponse;
import com.komsiluk.taxi.data.remote.favorite.FavoriteRouteUpdateRequest;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class FavoritesViewModel extends ViewModel {

    private final FavoriteService api;

    private final MutableLiveData<FavoritesState> state = new MutableLiveData<>();

    @Inject
    public FavoritesViewModel(FavoriteService api) {
        this.api = api;
    }

    public LiveData<FavoritesState> getState() { return state; }

    public void loadFavorites(Long userId) {
        state.setValue(FavoritesState.loading());

        api.getFavorites(userId).enqueue(new Callback<List<FavoriteRouteResponse>>() {
            @Override
            public void onResponse(Call<List<FavoriteRouteResponse>> call, Response<List<FavoriteRouteResponse>> resp) {
                if (!resp.isSuccessful()) {
                    state.setValue(FavoritesState.error("Load favorites failed (" + resp.code() + ")"));
                    return;
                }
                state.setValue(FavoritesState.data(resp.body()));
            }

            @Override
            public void onFailure(Call<List<FavoriteRouteResponse>> call, Throwable t) {
                state.setValue(FavoritesState.error("Network error: " + t.getMessage()));
            }
        });
    }

    public void addFavorite(Long userId, FavoriteRouteCreateRequest body, Runnable onDone) {
        state.setValue(FavoritesState.loading());

        api.addFavorite(userId, body).enqueue(new Callback<FavoriteRouteResponse>() {
            @Override
            public void onResponse(Call<FavoriteRouteResponse> call, Response<FavoriteRouteResponse> resp) {
                if (!resp.isSuccessful()) {
                    state.setValue(FavoritesState.error("Add favorite failed (" + resp.code() + ")"));
                    return;
                }
                if (onDone != null) onDone.run();
            }

            @Override
            public void onFailure(Call<FavoriteRouteResponse> call, Throwable t) {
                state.setValue(FavoritesState.error("Network error: " + t.getMessage()));
            }
        });
    }

    public void renameFavorite(Long favoriteId, String newTitle, Runnable onDone) {
        state.setValue(FavoritesState.loading());

        api.renameFavorite(favoriteId, new FavoriteRouteUpdateRequest(newTitle))
                .enqueue(new Callback<FavoriteRouteResponse>() {
                    @Override
                    public void onResponse(Call<FavoriteRouteResponse> call, Response<FavoriteRouteResponse> resp) {
                        if (!resp.isSuccessful()) {
                            state.setValue(FavoritesState.error("Rename failed (" + resp.code() + ")"));
                            return;
                        }
                        if (onDone != null) onDone.run();
                    }

                    @Override
                    public void onFailure(Call<FavoriteRouteResponse> call, Throwable t) {
                        state.setValue(FavoritesState.error("Network error: " + t.getMessage()));
                    }
                });
    }

    public void deleteFavorite(Long favoriteId, Runnable onDone) {
        state.setValue(FavoritesState.loading());

        api.deleteFavorite(favoriteId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> resp) {
                if (!resp.isSuccessful()) {
                    state.setValue(FavoritesState.error("Delete failed (" + resp.code() + ")"));
                    return;
                }
                if (onDone != null) onDone.run();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                state.setValue(FavoritesState.error("Network error: " + t.getMessage()));
            }
        });
    }
}
