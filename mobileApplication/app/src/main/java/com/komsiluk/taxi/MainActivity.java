package com.komsiluk.taxi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.komsiluk.taxi.auth.AuthManager;
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

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends BaseNavDrawerActivity {

    @Inject AuthManager authManager;

    private ActivityMainBinding mainBinding;

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
}
