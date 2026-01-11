package com.komsiluk.taxi.ui.ride;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.komsiluk.taxi.R;
import com.komsiluk.taxi.ui.menu.BaseNavDrawerActivity;

import java.util.ArrayList;

public class FavoritesActivity extends BaseNavDrawerActivity implements FavoritesAdapter.Listener {

    public static final String EXTRA_BOOK_FAVORITE = "extra_book_favorite";

    private RecyclerView rv;
    private TextView tvEmpty;

    private final ArrayList<FavoriteRide> mock = new ArrayList<>();

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
        seedMock();

        FavoritesAdapter adapter = new FavoritesAdapter(mock, this);
        rv.setAdapter(adapter);

        updateEmpty();
    }

    private void seedMock() {
        // GUI-only mock
        ArrayList<String> stations = new ArrayList<>();
        stations.add("Station address 1");
        stations.add("Station address 2");

        ArrayList<String> users = new ArrayList<>();
        users.add("user1@gmail.com");

        mock.add(new FavoriteRide(
                "Favorite card 1",
                "Start location 123",
                "Finish location 123",
                stations,
                users,
                "Luxury",
                true,
                true
        ));

        mock.add(new FavoriteRide(
                "Favorite card 2",
                "Start location 999",
                "Finish location 999",
                new ArrayList<>(),
                new ArrayList<>(),
                "Standard",
                false,
                false
        ));
    }

    private void updateEmpty() {
        boolean empty = mock.isEmpty();
        tvEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        rv.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onCardClicked(FavoriteRide ride) {
        FavoriteDialogs.showFavoriteDetails(this, ride,
                () -> {
                    Intent i = new Intent(this, com.komsiluk.taxi.UserActivity.class);
                    i.putExtra(EXTRA_BOOK_FAVORITE, ride);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                },
                () -> { FavoriteDialogs.showRenameDialog(this, ride.getName(), newName -> { /* GUI-only */ }); },
                () -> { FavoriteDialogs.showDeleteDialog(this, ride.getName(), () -> { /* GUI-only */ }); });
    }
}
