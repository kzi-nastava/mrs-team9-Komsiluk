package com.komsiluk.taxi.ui.ride;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.komsiluk.taxi.R;
import com.komsiluk.taxi.UserActivity;
import com.komsiluk.taxi.data.remote.favorite.FavoriteRouteResponse;
import com.komsiluk.taxi.data.remote.profile.UserProfileResponse;
import com.komsiluk.taxi.data.remote.profile.UserService;
import com.komsiluk.taxi.data.session.SessionManager;
import com.komsiluk.taxi.ui.menu.BaseNavDrawerActivity;

import java.util.ArrayList;

import dagger.hilt.android.AndroidEntryPoint;
import jakarta.inject.Inject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class FavoritesActivity extends BaseNavDrawerActivity implements FavoritesAdapter.Listener {

    public static final String EXTRA_BOOK_FAVORITE = "extra_book_favorite";

    private RecyclerView rv;
    private TextView tvEmpty;

    private FavoritesAdapter adapter;
    private final ArrayList<FavoriteRide> items = new ArrayList<>();
    private FavoritesViewModel vm;

    @Inject
    SessionManager sessionManager;

    @Inject
    UserService userApi;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_favorites;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rv = findViewById(R.id.rvFavorites);
        tvEmpty = findViewById(R.id.tvFavoritesEmpty);

        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FavoritesAdapter(items, this);
        rv.setAdapter(adapter);

        vm = new ViewModelProvider(this).get(FavoritesViewModel.class);

        vm.getState().observe(this, st -> {
            if (st == null) return;

            if (st.loading) {
                return;
            }

            if (st.error != null) {
                Toast.makeText(this, st.error, Toast.LENGTH_LONG).show();
                items.clear();
                adapter.notifyDataSetChanged();
                updateEmpty();
                return;
            }

            items.clear();
            if (st.data != null) {
                for (FavoriteRouteResponse r : st.data) {
                    items.add(mapToUi(r));
                }
            }

            adapter.notifyDataSetChanged();
            updateEmpty();
            loadEmailsForAllFavorites(st.data);
        });

        Long userId = sessionManager != null ? sessionManager.getUserId() : null;
        if (userId == null) {
            Toast.makeText(this, "Not logged in.", Toast.LENGTH_LONG).show();
            items.clear();
            updateEmpty();
            return;
        }

        vm.loadFavorites(userId);
    }

    private void updateEmpty() {
        boolean empty = items.isEmpty();
        tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        rv.setVisibility(empty ? View.GONE : View.VISIBLE);

        if (empty) {
            tvEmpty.setText("You have no favorite routes yet.");
        }
    }

    private FavoriteRide mapToUi(FavoriteRouteResponse r) {
        ArrayList<String> stations = new ArrayList<>();
        if (r.getStops() != null) stations.addAll(r.getStops());

        ArrayList<String> users = new ArrayList<>();

        String carType = prettyVehicleType(r.getVehicleType());

        String name = (r.getTitle() != null && !r.getTitle().trim().isEmpty())
                ? r.getTitle()
                : ("Favorite #" + (r.getId() != null ? r.getId() : ""));

        FavoriteRide ui = new FavoriteRide(
                r.getId(),
                name,
                r.getStartAddress(),
                r.getEndAddress(),
                stations,
                users,
                carType,
                r.isPetFriendly(),
                r.isBabyFriendly(),
                r.getDistanceKm(),
                r.getEstimatedDurationMin() != null ? r.getEstimatedDurationMin() : 0,
                r.getRouteId()
        );

        return ui;
    }

    private String prettyVehicleType(String raw) {
        if (raw == null) return "Standard";
        String s = raw.trim().toUpperCase();
        switch (s) {
            case "LUXURY": return "Luxury";
            case "VAN": return "Van";
            default: return "Standard";
        }
    }

    private void loadEmailsForAllFavorites(java.util.List<FavoriteRouteResponse> data) {
        if (data == null || data.isEmpty()) return;
        if (userApi == null) return;
        
        for (int pos = 0; pos < data.size() && pos < items.size(); pos++) {
            FavoriteRouteResponse r = data.get(pos);
            FavoriteRide ui = items.get(pos);

            java.util.List<Long> ids = r.getPassengerIds();
            if (ids == null || ids.isEmpty()) continue;

            ui.getUsers().clear();

            for (Long uid : ids) {
                if (uid == null) continue;
                loadSingleUserEmailIntoFavorite(uid, ui, pos);
            }
        }
    }

    private void loadSingleUserEmailIntoFavorite(Long uid, FavoriteRide ui, int pos) {
        userApi.getProfile(uid).enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> resp) {

                if (!resp.isSuccessful() || resp.body() == null) return;

                String email = resp.body().getEmail();
                if (email == null) return;

                String e = email.trim();
                if (e.isEmpty()) return;

                if (!ui.getUsers().contains(e)) {
                    ui.getUsers().add(e);
                    adapter.notifyItemChanged(pos);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<UserProfileResponse> call, Throwable t) {
            }
        });
    }

    @Override
    public void onCardClicked(FavoriteRide ride) {
        FavoriteDialogs.showFavoriteDetails(
                this,
                ride,
                () -> {
                    Intent i = new Intent(this, UserActivity.class);
                    i.putExtra(EXTRA_BOOK_FAVORITE, ride);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                },
                () -> FavoriteDialogs.showRenameDialog(this, ride.getName(), newName -> {
                    if (newName == null || newName.trim().length() < 2) {
                        Toast.makeText(this, "Name must be at least 2 characters.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    vm.renameFavorite(ride.getFavoriteId(), newName.trim(), () -> {
                        Long uid = sessionManager.getUserId();
                        if (uid != null) vm.loadFavorites(uid);
                    });
                }),
                () -> FavoriteDialogs.showDeleteDialog(this, ride.getName(), () -> {
                    vm.deleteFavorite(ride.getFavoriteId(), () -> {
                        Long uid = sessionManager.getUserId();
                        if (uid != null) vm.loadFavorites(uid);
                    });
                })
        );
    }
}
