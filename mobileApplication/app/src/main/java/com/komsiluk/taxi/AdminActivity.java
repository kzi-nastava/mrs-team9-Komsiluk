package com.komsiluk.taxi;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.komsiluk.taxi.data.remote.location.DriverLocationResponse;
import com.komsiluk.taxi.data.remote.location.LocationService;
import com.komsiluk.taxi.ui.menu.BaseNavDrawerActivity;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.Collection;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class AdminActivity extends BaseNavDrawerActivity {

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_admin;
    }

    @Override
    protected int getDrawerMenuResId() {
        return R.menu.menu_admin_drawer;
    }


    @Inject
    LocationService locationService;
    private MapView map;
    private Handler locationHandler = new Handler(Looper.getMainLooper());
    private Runnable locationRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

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

            }
        });
    }

    private void updateMapMarkers(Collection<DriverLocationResponse> locations) {
        if (map == null) return;

        map.getOverlays().clear();

        for (DriverLocationResponse loc : locations) {
            Marker marker = new Marker(map);
            marker.setPosition(new GeoPoint(loc.getLat(), loc.getLng()));
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

            Drawable icon;
            if (loc.isPanic()) {
                icon = ContextCompat.getDrawable(this, R.drawable.taxi_panic);
                marker.setTitle("PANIC!");
                marker.setSubDescription("Vozač u opasnosti!");
            } else {
                icon = loc.isBusy() ?
                        ContextCompat.getDrawable(this, R.drawable.taxi_busy) :
                        ContextCompat.getDrawable(this, R.drawable.taxi_free);
                marker.setTitle(loc.isBusy() ? "Zauzeto" : "Slobodno");
            }

            marker.setIcon(icon);
            map.getOverlays().add(marker);
        }
        map.invalidate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationHandler != null && locationRunnable != null) {
            locationHandler.removeCallbacks(locationRunnable);
        }
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

}