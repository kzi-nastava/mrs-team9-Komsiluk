package com.komsiluk.taxi;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.komsiluk.taxi.auth.AuthManager;
import com.komsiluk.taxi.data.remote.location.DriverLocationResponse;
import com.komsiluk.taxi.data.remote.location.LocationService;
import com.komsiluk.taxi.ui.about.AboutUsActivity;
import com.komsiluk.taxi.ui.auth.AuthActivity;
import com.komsiluk.taxi.ui.menu.BaseNavDrawerActivity;
import com.komsiluk.taxi.ui.ride.map.GeoRepository;
import com.komsiluk.taxi.ui.ride.map.NominatimPlace;
import com.komsiluk.taxi.ui.ride.map.OsrmRouteResponse;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class MainActivity extends BaseNavDrawerActivity {

    @Inject AuthManager authManager;
    @Inject LocationService locationService;

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
    private EditText etPickup, etDestination;
    private ImageButton btnSearchPickup, btnSearchDestination;
    private TextView tvKm, tvDriveTime;
    private MaterialButton btnBookRide;
    private BottomSheetBehavior<View> sheetBehavior;

    private GeoRepository geoRepo;

    private Marker pickupMarker;
    private Marker destMarker;
    private Polyline routeLine;

    private GeoPoint pickupPoint;
    private GeoPoint destPoint;

    private boolean pickupSelected = false;
    private boolean destSelected = false;

    private double lastDistanceKm = 0.0;
    private int lastDurationMin = 0;

    private static final GeoPoint NOVI_SAD_CENTER = new GeoPoint(45.2671, 19.8335);
    private static final BoundingBox NS_BOX = new BoundingBox(
            45.35, 19.95,
            45.20, 19.75
    );
    private static final String NS_VIEWBOX = "19.75,45.35,19.95,45.20";

    private Handler locationHandler = new Handler(Looper.getMainLooper());
    private Runnable locationRunnable;

    public static final String EXTRA_PICKUP_ADDRESS = "EXTRA_PICKUP_ADDRESS";
    public static final String EXTRA_DEST_ADDRESS = "EXTRA_DEST_ADDRESS";
    public static final String EXTRA_PICKUP_LAT = "EXTRA_PICKUP_LAT";
    public static final String EXTRA_PICKUP_LNG = "EXTRA_PICKUP_LNG";
    public static final String EXTRA_DEST_LAT = "EXTRA_DEST_LAT";
    public static final String EXTRA_DEST_LNG = "EXTRA_DEST_LNG";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE));

        map = findViewById(R.id.map);

        if (map != null) {
            setupMap();
        } else {
            android.util.Log.e("MainActivity", "MapView is null! Check your layout.");
            Toast.makeText(this, "Map initialization failed", Toast.LENGTH_LONG).show();
            return;
        }

        setupGeoRepository();

        initializeUIElements();

        setupBottomSheet();

        setupListeners();

        startLocationRefresh();

        restoreStateIfNeeded();
    }

    private void setupMap() {
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        map.setMultiTouchControls(true);
        map.getController().setZoom(13.5);
        map.getController().setCenter(NOVI_SAD_CENTER);

        map.setScrollableAreaLimitDouble(NS_BOX);
        map.setMinZoomLevel(12.0);
        map.setMaxZoomLevel(20.0);
    }

    private void setupGeoRepository() {
        okhttp3.logging.HttpLoggingInterceptor log = new okhttp3.logging.HttpLoggingInterceptor();
        log.setLevel(okhttp3.logging.HttpLoggingInterceptor.Level.BODY);

        OkHttpClient geoClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> chain.proceed(
                        chain.request().newBuilder()
                                .header("User-Agent", "KomsilukTaxiAndroid/1.0 (contact: komsiluktim@gmail.com)")
                                .header("Accept", "application/json")
                                .build()
                ))
                .addInterceptor(log)
                .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
                .build();

        geoRepo = new GeoRepository(geoClient);
    }

    private void initializeUIElements() {
        etPickup = findViewById(R.id.etPickup);
        etDestination = findViewById(R.id.etDestination);
        btnSearchPickup = findViewById(R.id.btnSearchPickup);
        btnSearchDestination = findViewById(R.id.btnSearchDestination);
        tvKm = findViewById(R.id.tvKm);
        tvDriveTime = findViewById(R.id.tvDriveTime);
        btnBookRide = findViewById(R.id.btnBookRide);

        if (etPickup == null || etDestination == null || btnBookRide == null) {
            android.util.Log.e("MainActivity", "Critical UI elements not found!");
        }
    }

    private void setupBottomSheet() {
        View sheet = findViewById(R.id.bookRideSheet);
        if (sheet != null) {
            sheetBehavior = BottomSheetBehavior.from(sheet);
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            sheetBehavior.setHideable(false);

            setSheetDraggable(false);

            View headerRow = findViewById(R.id.headerRow);
            View sheetHandle = findViewById(R.id.sheetHandle);

            if (headerRow != null && sheetHandle != null) {
                @SuppressLint("ClickableViewAccessibility") View.OnTouchListener headerDrag = (v, event) -> {
                    switch (event.getActionMasked()) {
                        case android.view.MotionEvent.ACTION_DOWN:
                        case android.view.MotionEvent.ACTION_MOVE:
                            setSheetDraggable(true);
                            break;
                        case android.view.MotionEvent.ACTION_UP:
                        case android.view.MotionEvent.ACTION_CANCEL:
                            setSheetDraggable(false);
                            break;
                    }
                    return false;
                };

                headerRow.setOnTouchListener(headerDrag);
                sheetHandle.setOnTouchListener(headerDrag);
            }
        }
    }

    private void setSheetDraggable(boolean draggable) {
        if (sheetBehavior != null) {
            try {
                sheetBehavior.setDraggable(draggable);
            } catch (Throwable ignored) {}
        }
    }

    private void setupListeners() {
        btnSearchPickup.setOnClickListener(v -> searchAndPickLocation(true));
        btnSearchDestination.setOnClickListener(v -> searchAndPickLocation(false));

        btnBookRide.setOnClickListener(v -> handleBookRideClick());

        etPickup.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(android.text.Editable s) {
                if (pickupSelected) {
                    pickupSelected = false;
                    pickupPoint = null;
                    removeMarker(true);
                    clearRouteAndStats();
                }
            }
        });

        etDestination.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(android.text.Editable s) {
                if (destSelected) {
                    destSelected = false;
                    destPoint = null;
                    removeMarker(false);
                    clearRouteAndStats();
                }
            }
        });
    }

    private void searchAndPickLocation(boolean isPickup) {
        String q = isPickup
                ? safeTrim(etPickup.getText())
                : safeTrim(etDestination.getText());

        if (q.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_location_required), Toast.LENGTH_SHORT).show();
            return;
        }

        geoRepo.searchNoviSad(q, NS_VIEWBOX).enqueue(new Callback<List<NominatimPlace>>() {
            @Override
            public void onResponse(Call<List<NominatimPlace>> call, Response<List<NominatimPlace>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Search failed (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<NominatimPlace> raw = response.body();
                if (raw == null || raw.isEmpty()) {
                    Toast.makeText(MainActivity.this, getString(R.string.error_no_results), Toast.LENGTH_SHORT).show();
                    return;
                }

                List<NominatimPlace> filtered = filterDistinct(raw);
                if (filtered.isEmpty()) {
                    Toast.makeText(MainActivity.this, getString(R.string.error_no_results), Toast.LENGTH_SHORT).show();
                    return;
                }

                showPickDialog(filtered, isPickup);
            }

            @Override
            public void onFailure(Call<List<NominatimPlace>> call, Throwable t) {
                Toast.makeText(MainActivity.this, getString(R.string.error_geocode_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPickDialog(List<NominatimPlace> items, boolean isPickup) {
        View view = getLayoutInflater().inflate(R.layout.dialog_place_picker, null);

        TextView tvTitle = view.findViewById(R.id.tvTitle);
        tvTitle.setText(isPickup ? getString(R.string.pick_pickup_title) : getString(R.string.pick_destination_title));

        android.widget.ListView list = view.findViewById(R.id.list);
        PlaceAdapter adapter = new PlaceAdapter(this, items);
        list.setAdapter(adapter);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(true)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        list.setOnItemClickListener((parent, v, which, id) -> {
            NominatimPlace p = items.get(which);
            GeoPoint pt = new GeoPoint(parseDouble(p.lat), parseDouble(p.lon));
            String pretty = shortenDisplay(p.displayName);

            if (isPickup) {
                pickupPoint = pt;
                pickupSelected = false;
                etPickup.setText(pretty);
                pickupSelected = true;
                setMarker(true, pt);
            } else {
                destPoint = pt;
                destSelected = false;
                etDestination.setText(pretty);
                destSelected = true;
                setMarker(false, pt);
            }

            zoomToPoints();

            if (pickupPoint != null && destPoint != null) {
                drawRouteAndStats();
            }

            dialog.dismiss();
        });

        dialog.show();
    }

    private void setMarker(boolean isPickup, GeoPoint pt) {
        Marker m = isPickup ? pickupMarker : destMarker;
        if (m != null) map.getOverlays().remove(m);

        Marker marker = new Marker(map);
        marker.setPosition(pt);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setIcon(ContextCompat.getDrawable(
                this,
                isPickup ? R.drawable.car : R.drawable.flag
        ));

        map.getOverlays().add(marker);

        if (isPickup) pickupMarker = marker;
        else destMarker = marker;

        map.invalidate();
    }

    private void drawRouteAndStats() {
        if (pickupPoint == null || destPoint == null) return;

        geoRepo.route(
                pickupPoint.getLongitude(), pickupPoint.getLatitude(),
                destPoint.getLongitude(), destPoint.getLatitude()
        ).enqueue(new Callback<OsrmRouteResponse>() {
            @Override
            public void onResponse(Call<OsrmRouteResponse> call, Response<OsrmRouteResponse> response) {
                if (!response.isSuccessful() || response.body() == null ||
                        response.body().routes == null || response.body().routes.isEmpty()) {
                    Toast.makeText(MainActivity.this, getString(R.string.error_route_failed), Toast.LENGTH_SHORT).show();
                    return;
                }

                OsrmRouteResponse.Route r = response.body().routes.get(0);

                if (r.geometry != null) {
                    drawPolyline(r.geometry.coordinates);
                }

                double km = r.distance / 1000.0;
                tvKm.setText(String.format(Locale.getDefault(), "%.1f km", km));
                tvDriveTime.setText(formatDuration((long) r.duration));

                lastDistanceKm = km;
                lastDurationMin = (int) Math.round(r.duration / 60.0);
            }

            @Override
            public void onFailure(Call<OsrmRouteResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, getString(R.string.error_route_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void drawPolyline(List<List<Double>> coords) {
        if (coords == null || coords.isEmpty()) return;

        if (routeLine != null) map.getOverlays().remove(routeLine);

        List<GeoPoint> pts = new ArrayList<>();
        for (List<Double> c : coords) {
            if (c.size() < 2) continue;
            double lon = c.get(0);
            double lat = c.get(1);
            pts.add(new GeoPoint(lat, lon));
        }

        Polyline line = new Polyline();
        line.setPoints(pts);
        line.getOutlinePaint().setStrokeWidth(10f);

        map.getOverlays().add(line);
        routeLine = line;

        map.invalidate();
    }

    private void zoomToPoints() {
        if (pickupPoint == null && destPoint == null) return;

        if (pickupPoint != null && destPoint == null) {
            map.getController().animateTo(pickupPoint);
            map.getController().setZoom(16.0);
            return;
        }

        if (pickupPoint == null) {
            map.getController().animateTo(destPoint);
            map.getController().setZoom(16.0);
            return;
        }

        double north = Math.max(pickupPoint.getLatitude(), destPoint.getLatitude());
        double south = Math.min(pickupPoint.getLatitude(), destPoint.getLatitude());
        double east = Math.max(pickupPoint.getLongitude(), destPoint.getLongitude());
        double west = Math.min(pickupPoint.getLongitude(), destPoint.getLongitude());

        BoundingBox bb = new BoundingBox(north, east, south, west);
        map.zoomToBoundingBox(bb, true, 140);
    }

    private void removeMarker(boolean isPickup) {
        Marker m = isPickup ? pickupMarker : destMarker;
        if (m != null) {
            map.getOverlays().remove(m);
            if (isPickup) pickupMarker = null;
            else destMarker = null;
            map.invalidate();
        }
    }

    private void clearRouteAndStats() {
        if (routeLine != null) {
            map.getOverlays().remove(routeLine);
            routeLine = null;
            map.invalidate();
        }
        tvKm.setText(getString(R.string.placeholder_kilometers_empty));
        tvDriveTime.setText(getString(R.string.placeholder_drive_time_empty));
        lastDistanceKm = 0.0;
        lastDurationMin = 0;
    }

    private void handleBookRideClick() {
        if (!pickupSelected || pickupPoint == null) {
            Toast.makeText(this, getString(R.string.error_pickup_not_selected), Toast.LENGTH_SHORT).show();
            return;
        }
        if (!destSelected || destPoint == null) {
            Toast.makeText(this, getString(R.string.error_destination_not_selected), Toast.LENGTH_SHORT).show();
            return;
        }

        showLoginRequiredDialog();
    }

    private void showLoginRequiredDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_login_to_book, null);

        MaterialButton btnCancel = dialogView.findViewById(R.id.btnConfirmCancel);
        MaterialButton btnConfirm = dialogView.findViewById(R.id.btnConfirmOk);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            dialog.dismiss();
            navigateToLoginWithRouteData();
        });

        dialog.show();
    }

    private void navigateToLoginWithRouteData() {
        Intent intent = new Intent(this, AuthActivity.class);
        intent.putExtra("AUTH_DESTINATION", "LOGIN");

        // Pass route data as extras
        intent.putExtra(EXTRA_PICKUP_ADDRESS, safeTrim(etPickup.getText()));
        intent.putExtra(EXTRA_DEST_ADDRESS, safeTrim(etDestination.getText()));

        if (pickupPoint != null) {
            intent.putExtra(EXTRA_PICKUP_LAT, pickupPoint.getLatitude());
            intent.putExtra(EXTRA_PICKUP_LNG, pickupPoint.getLongitude());
        }

        if (destPoint != null) {
            intent.putExtra(EXTRA_DEST_LAT, destPoint.getLatitude());
            intent.putExtra(EXTRA_DEST_LNG, destPoint.getLongitude());
        }

        startActivity(intent);
    }

    private void restoreStateIfNeeded() {
        Intent intent = getIntent();
        if (intent == null) return;

        String pickupAddr = intent.getStringExtra(EXTRA_PICKUP_ADDRESS);
        String destAddr = intent.getStringExtra(EXTRA_DEST_ADDRESS);

        if (pickupAddr == null || destAddr == null) return;

        // Restore addresses
        etPickup.setText(pickupAddr);
        etDestination.setText(destAddr);

        // Restore coordinates
        double pickupLat = intent.getDoubleExtra(EXTRA_PICKUP_LAT, 0);
        double pickupLng = intent.getDoubleExtra(EXTRA_PICKUP_LNG, 0);
        double destLat = intent.getDoubleExtra(EXTRA_DEST_LAT, 0);
        double destLng = intent.getDoubleExtra(EXTRA_DEST_LNG, 0);

        if (pickupLat != 0 && pickupLng != 0) {
            pickupPoint = new GeoPoint(pickupLat, pickupLng);
            pickupSelected = true;
            setMarker(true, pickupPoint);
        }

        if (destLat != 0 && destLng != 0) {
            destPoint = new GeoPoint(destLat, destLng);
            destSelected = true;
            setMarker(false, destPoint);
        }

        if (pickupPoint != null && destPoint != null) {
            zoomToPoints();
            drawRouteAndStats();

            // Expand the bottom sheet
            if (sheetBehavior != null) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        }
    }

    private List<NominatimPlace> filterDistinct(List<NominatimPlace> raw) {
        Map<String, NominatimPlace> m = new LinkedHashMap<>();
        for (NominatimPlace p : raw) {
            String k = shortenDisplay(p.displayName).toLowerCase(Locale.ROOT);
            if (!m.containsKey(k)) m.put(k, p);
        }
        return new ArrayList<>(m.values());
    }

    private String shortenDisplay(String display) {
        if (display == null) return "";
        String s = display;

        s = s.replace(", Serbia", "")
                .replace(", Република Србија", "")
                .replace(", Republika Srbija", "")
                .replace(", South Bačka Administrative District", "")
                .replace(", Južnobački okrug", "")
                .replace(", Град Нови Сад", "")
                .replace(", Novi Sad City", "")
                .trim();

        String[] parts = s.split(",");
        if (parts.length <= 3) return s.trim();

        return (parts[0].trim() + ", " + parts[1].trim() + ", " + parts[2].trim()).trim();
    }

    private String safeTrim(CharSequence cs) {
        return cs == null ? "" : cs.toString().trim();
    }

    private double parseDouble(String s) {
        try {
            return Double.parseDouble(s);
        } catch (Exception e) {
            return 0.0;
        }
    }

    private String formatDuration(long seconds) {
        long min = seconds / 60;
        long h = min / 60;
        long m = min % 60;

        if (h <= 0) return String.format(Locale.getDefault(), "%d min", m);
        return String.format(Locale.getDefault(), "%dh %02dmin", h, m);
    }

    private static class PlaceAdapter extends android.widget.BaseAdapter {
        private final android.content.Context ctx;
        private final List<NominatimPlace> items;

        PlaceAdapter(android.content.Context ctx, List<NominatimPlace> items) {
            this.ctx = ctx;
            this.items = items;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, android.view.ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                v = LayoutInflater.from(ctx).inflate(R.layout.item_place_result, parent, false);
            }
            TextView tvMain = v.findViewById(R.id.tvMain);

            NominatimPlace p = items.get(position);
            String label = p.displayName == null ? "" : p.displayName;
            tvMain.setText(label);

            return v;
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
            }
        });
    }

    private void updateMapMarkers(Collection<DriverLocationResponse> locations) {
        if (map == null) return;

        List<Marker> toRemove = new ArrayList<>();
        for (Object overlay : map.getOverlays()) {
            if (overlay instanceof Marker) {
                Marker m = (Marker) overlay;
                if (m != pickupMarker && m != destMarker) {
                    toRemove.add(m);
                }
            }
        }
        map.getOverlays().removeAll(toRemove);

        // Add driver markers
        for (DriverLocationResponse loc : locations) {
            Marker marker = new Marker(map);
            marker.setPosition(new GeoPoint(loc.getLat(), loc.getLng()));
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

            Drawable icon = loc.isBusy() ?
                    ContextCompat.getDrawable(this, R.drawable.taxi_busy) :
                    ContextCompat.getDrawable(this, R.drawable.taxi_free);

            marker.setIcon(icon);
            marker.setTitle(loc.isBusy() ? "Zauzeto" : "Slobodno");
            map.getOverlays().add(marker);
        }
        map.invalidate();
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



    protected void onDestroy() {
        super.onDestroy();
        if (locationHandler != null && locationRunnable != null) {
            locationHandler.removeCallbacks(locationRunnable);
        }
    }
}
