package com.komsiluk.taxi;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.komsiluk.taxi.auth.AuthManager;
import com.komsiluk.taxi.data.remote.location.DriverLocationResponse;
import com.komsiluk.taxi.data.remote.location.LocationService;
import com.komsiluk.taxi.databinding.ActivityMainBinding;
import com.komsiluk.taxi.ui.about.AboutUsActivity;
import com.komsiluk.taxi.ui.auth.AuthActivity;
import com.komsiluk.taxi.ui.menu.BaseNavDrawerActivity;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class MainActivity extends BaseNavDrawerActivity {

    @Inject AuthManager authManager;
    @Inject LocationService locationService;

    private ActivityMainBinding mainBinding;
    private Timer refreshTimer;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected int getDrawerMenuResId() {
        return R.menu.menu_guest_drawer;
    }

    @Override
    protected boolean shouldShowBottomNav() {
        return false;
    }

    private MapView map;

    private Handler locationHandler = new Handler(Looper.getMainLooper());
    private Runnable locationRunnable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View contentRoot = findViewById(R.id.main);
        mainBinding = ActivityMainBinding.bind(contentRoot);

        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE));

        map = findViewById(R.id.map);

        map.setTileSource(TileSourceFactory.MAPNIK);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        map.setMultiTouchControls(true);
        map.getController().setZoom(13.5);
        map.getController().setCenter(new GeoPoint(45.2671, 19.8335));

        BoundingBox nsBox = new BoundingBox(
                45.35, 19.95,
                45.20, 19.75
        );

        map.setScrollableAreaLimitDouble(nsBox);
        map.setMinZoomLevel(12.0);
        map.setMaxZoomLevel(20.0);
        startLocationRefresh();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (map != null) map.onResume();
    }

    @Override
    protected void onPause() {
        if (map != null) map.onPause();
        super.onPause();
    }

    @Override
    protected void handleDrawerItemClick(int itemId) {
        if (itemId == R.id.nav_login) {
            Intent i = new Intent(this, AuthActivity.class);
            i.putExtra("AUTH_DESTINATION", "LOGIN");
            startActivity(i);
        } else if (itemId == R.id.nav_register) {
            Intent i = new Intent(this, AuthActivity.class);
            i.putExtra("AUTH_DESTINATION", "REGISTER");
            startActivity(i);
        } else if (itemId == R.id.nav_about) {
            startActivity(new Intent(this, AboutUsActivity.class));
        }
    }

    private void startLocationRefresh() {
        locationRunnable = new Runnable() {
            @Override
            public void run() {
                fetchDriverLocations();
                locationHandler.postDelayed(this, 1000);
            }
        };
        locationHandler.post(locationRunnable);
    }

    private void fetchDriverLocations() {
        locationService.getAllActiveDriverLocations().enqueue(new Callback<Collection<DriverLocationResponse>>() {
            @Override
            public void onResponse(Call<Collection<DriverLocationResponse>> call, Response<Collection<DriverLocationResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    runOnUiThread(() -> updateMapMarkers(response.body()));
                }
            }

            @Override
            public void onFailure(Call<Collection<DriverLocationResponse>> call, Throwable t) {
                // Tiho neuspeh da ne smeta korisniku
            }
        });
    }

    private void updateMapMarkers(Collection<DriverLocationResponse> locations) {
        if (map == null) return;

        map.getOverlays().clear();

        for (DriverLocationResponse loc : locations) {
            Marker startMarker = new Marker(map);
            startMarker.setPosition(new GeoPoint(loc.getLat(), loc.getLng()));
            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

            Drawable icon = loc.isBusy() ?
                    ContextCompat.getDrawable(this, R.drawable.taxi_busy) :
                    ContextCompat.getDrawable(this, R.drawable.taxi_free);

            startMarker.setIcon(icon);
            startMarker.setTitle(loc.isBusy() ? "Zauzeto" : "Slobodno");
            map.getOverlays().add(startMarker);
        }
        map.invalidate();
    }

    protected void onDestroy() {
        super.onDestroy();
        if (locationHandler != null && locationRunnable != null) {
            locationHandler.removeCallbacks(locationRunnable);
        }
    }
}
