package com.komsiluk.taxi;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.komsiluk.taxi.data.remote.location.DriverLocationResponse;
import com.komsiluk.taxi.data.remote.location.DriverLocationUpdate;
import com.komsiluk.taxi.data.remote.location.LocationService;
import com.komsiluk.taxi.data.remote.ride.RideResponse;
import com.komsiluk.taxi.data.remote.ride.RideService;
import com.komsiluk.taxi.data.session.SessionManager;
import com.komsiluk.taxi.ui.menu.BaseNavDrawerActivity;
import com.komsiluk.taxi.ui.ride.map.GeoRepository;
import com.komsiluk.taxi.ui.ride.map.OsrmRouteResponse;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@AndroidEntryPoint
public class DriverActivity extends BaseNavDrawerActivity {

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_driver;
    }

    @Override
    protected int getDrawerMenuResId() {
        return R.menu.menu_driver_drawer;
    }

    private MapView map;

    private org.osmdroid.views.overlay.Polyline routePolyline;

    // Bottom sheet
    private BottomSheetBehavior<View> sheetBehavior;
    private View currentRideSheet;

    private TextView tvPickupValue;
    private TextView tvDestinationValue;

    private View rowStartCancel;
    private View rowInRide;

    private MaterialButton btnCancel;
    private MaterialButton btnStart;

    private MaterialButton btnFinish;
    private MaterialButton btnStop;
    private MaterialButton btnReport;
    private MaterialButton btnPanic;

    private RideResponse currentRide;
    private boolean rideStartedUi = false;

    private boolean startInFlight= false;

    private Marker myLocationMarker;
    private Drawable iconFree;
    private Drawable iconBusy;

    private Handler selfLocationHandler = new Handler(Looper.getMainLooper());
    private Runnable selfLocationRunnable;

    private boolean firstTimeZoom = true;

    private GeoRepository geoRepository;

    private Handler animationHandler = new Handler(Looper.getMainLooper());
    private Runnable animationRunnable;

    @Inject
    SessionManager sessionManager;

    @Inject
    RideService rideService;

    @Inject
    LocationService locationService;

    @Inject
    OkHttpClient okHttpClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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

        currentRideSheet = findViewById(R.id.currentRideSheet);
        sheetBehavior = BottomSheetBehavior.from(currentRideSheet);

        sheetBehavior.setHideable(true);
        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        tvPickupValue = findViewById(R.id.tvPickupValue);
        tvDestinationValue = findViewById(R.id.tvDestinationValue);

        rowStartCancel = findViewById(R.id.rowStartCancel);
        rowInRide = findViewById(R.id.rowInRide);

        btnCancel = findViewById(R.id.btnCancelRide);
        btnStart = findViewById(R.id.btnStartRide);

        btnFinish = findViewById(R.id.btnFinishRide);
        btnStop = findViewById(R.id.btnStopRide);
        btnReport = findViewById(R.id.btnReportRide);
        btnPanic = findViewById(R.id.btnPanicRide);

        btnCancel.setOnClickListener(v -> {
            Toast.makeText(this, "Cancel not implemented yet.", Toast.LENGTH_SHORT).show();
        });

        btnStart.setOnClickListener(v -> showConfirmStartDialog());

        // U onCreate metodi
        btnFinish.setOnClickListener(v -> finishRideOnBackend());
        btnStop.setOnClickListener(v -> Toast.makeText(this, "Stop not implemented yet.", Toast.LENGTH_SHORT).show());
        btnReport.setOnClickListener(v -> Toast.makeText(this, "Report not implemented yet.", Toast.LENGTH_SHORT).show());
        btnPanic.setOnClickListener(v -> Toast.makeText(this, "Panic not implemented yet.", Toast.LENGTH_SHORT).show());

        geoRepository = new GeoRepository(okHttpClient);
        startSelfLocationTracking();
    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchCurrentRideAndUpdateUi();
    }

    private void fetchCurrentRideAndUpdateUi() {
        Long driverId = sessionManager != null ? sessionManager.getUserId() : null;
        if (driverId == null) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            return;
        }

        if (rideService == null) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            return;
        }

        rideService.getDriverCurrentRide(driverId).enqueue(new Callback<RideResponse>() {
            @Override
            public void onResponse(Call<RideResponse> call, Response<RideResponse> resp) {

                currentRide = resp.body();

                if (resp.code() == 204) {
                    currentRide = null;
                    rideStartedUi = false;
                    sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    return;
                }

                if (!resp.isSuccessful() || resp.body() == null) {
                    currentRide = null;
                    rideStartedUi = false;
                    sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    return;
                }

                if (currentRide != null && !isRideActiveStatus(currentRide.getStatus())) {
                    if (myLocationMarker != null) {
                        Log.d("SIMULATION", "Pokrećem animaciju ka pickup-u");
                        double myLat = myLocationMarker.getPosition().getLatitude();
                        double myLng = myLocationMarker.getPosition().getLongitude();
                        String pickupAddress = currentRide.getStartAddress();

                        getCoordinatesAndAnimate(pickupAddress, myLat, myLng);
                    } else {
                        Log.e("SIMULATION", "Marker lokacije je NULL, odlažem animaciju");
                        new Handler(Looper.getMainLooper()).postDelayed(DriverActivity.this::fetchCurrentRideAndUpdateUi, 1000);
                    }
                }

                currentRide = resp.body();

                String pickup = safe(currentRide.getStartAddress());
                String dest = safe(currentRide.getEndAddress());

                tvPickupValue.setText(pickup);
                tvDestinationValue.setText(dest);

                rideStartedUi = isRideActiveStatus(currentRide.getStatus());
                applyRideButtonsUi();

                applyRideButtonsUi();

                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }

            @Override
            public void onFailure(Call<RideResponse> call, Throwable t) {
                currentRide = null;
                rideStartedUi = false;
                sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
    }

    private boolean isRideActiveStatus(String statusRaw) {
        if (statusRaw == null) return false;

        String s = statusRaw.trim().toUpperCase();

        return s.equals("ACTIVE");
    }

    private void applyRideButtonsUi() {
        if (rideStartedUi) {
            rowStartCancel.setVisibility(View.GONE);
            rowInRide.setVisibility(View.VISIBLE);
            btnFinish.setEnabled(false);
            btnFinish.setAlpha(0.5f);
        } else {
            rowStartCancel.setVisibility(View.VISIBLE);
            rowInRide.setVisibility(View.GONE);
            btnStart.setEnabled(false);
            btnStart.setAlpha(0.5f); // Vizuelni feedback da je dugme zaključano
        }
    }

    private void showConfirmStartDialog() {
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_confirm_start_ride, null, false);

        ImageButton btnClose = v.findViewById(R.id.btnStartClose);
        MaterialButton btnCancel = v.findViewById(R.id.btnStartCancel);
        MaterialButton btnConfirm = v.findViewById(R.id.btnStartConfirm);

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(v)
                .setCancelable(true)
                .create();

        dialog.show();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.92f);
            dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        View.OnClickListener dismiss = x -> dialog.dismiss();
        btnClose.setOnClickListener(dismiss);
        btnCancel.setOnClickListener(dismiss);

        btnConfirm.setOnClickListener(x -> {
            if (startInFlight) return;

            Long rideId = getCurrentRideId();
            if (rideId == null) {
                Toast.makeText(this, "Ride ID missing.", Toast.LENGTH_LONG).show();
                return;
            }

            startInFlight = true;

            btnConfirm.setEnabled(false);
            btnCancel.setEnabled(false);
            btnClose.setEnabled(false);

            startRideOnBackend(
                    rideId,
                    () -> {
                        startInFlight = false;

                        rideStartedUi = true;
                        applyRideButtonsUi();

                        // 1. Prvo ugasi staru animaciju (ka pickup-u)
                        if (animationHandler != null) animationHandler.removeCallbacks(animationRunnable);

                        // 2. Pokreni novu simulaciju ka destinaciji
                        prepareAndStartMainRideSimulation();

                        // 3. OBAVEZNO zatvori dijalog ovde
                        dialog.dismiss();
                        Toast.makeText(this, "Vožnja je uspešno počela!", Toast.LENGTH_SHORT).show();
                    },
                    (errMsg) -> {
                        startInFlight = false;

                        btnConfirm.setEnabled(true);
                        btnCancel.setEnabled(true);
                        btnClose.setEnabled(true);

                        Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show();
                    }
            );
        });
    }

    private void startRideOnBackend(Long rideId, Runnable onSuccess, java.util.function.Consumer<String> onError) {
        if (rideService == null) {
            onError.accept("Ride service is not available.");
            return;
        }

        rideService.startRide(rideId).enqueue(new retrofit2.Callback<RideResponse>() {
            @Override
            public void onResponse(Call<RideResponse> call, Response<RideResponse> resp) {
                if (!resp.isSuccessful() || resp.body() == null) {
                    String msg = "Start failed (" + resp.code() + ")";
                    onError.accept(msg);
                    return;
                }

                currentRide = resp.body();
                onSuccess.run();
            }

            @Override
            public void onFailure(Call<RideResponse> call, Throwable t) {
                onError.accept("Network error: " + (t.getMessage() == null ? "unknown" : t.getMessage()));
            }
        });
    }

    private Long getCurrentRideId() {
        if (currentRide == null) return null;
        try { return currentRide.getId(); } catch (Throwable ignored) { return null; }
    }

    private void startSelfLocationTracking() {
        selfLocationRunnable = new Runnable() {
            @Override
            public void run() {
                fetchMyCurrentLocation();
                selfLocationHandler.postDelayed(this, 1000);
            }
        };
        selfLocationHandler.post(selfLocationRunnable);
    }

    private void fetchMyCurrentLocation() {
        Long myId = sessionManager.getUserId();
        if (myId == null) return;

        locationService.getSpecificDriverLocation(myId).enqueue(new Callback<DriverLocationResponse>() {
            @Override
            public void onResponse(Call<DriverLocationResponse> call, Response<DriverLocationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DriverLocationResponse loc = response.body();
                    updateDriverMarkerOnMap(loc.getLat(), loc.getLng());
                }
            }

            @Override
            public void onFailure(Call<DriverLocationResponse> call, Throwable t) {
                Log.e("SELF_TRACK", "Greška pri dohvatanju sopstvene lokacije");
            }
        });
    }

    private void updateDriverMarkerOnMap(double lat, double lng) {
        if (map == null) return;

        if (myLocationMarker == null) {
            myLocationMarker = new Marker(map);
            myLocationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            myLocationMarker.setTitle("Ja");
            map.getOverlays().add(myLocationMarker);

            iconFree = androidx.core.content.ContextCompat.getDrawable(this, R.drawable.taxi_free);
            iconBusy = androidx.core.content.ContextCompat.getDrawable(this, R.drawable.taxi_busy);
        }

        GeoPoint myPos = new GeoPoint(lat, lng);
        myLocationMarker.setPosition(myPos);

        myLocationMarker.setIcon(rideStartedUi ? iconBusy : iconFree);

        if (firstTimeZoom) {
            map.getController().animateTo(myPos);
            map.getController().setZoom(16.5);
            firstTimeZoom = false;
        }

        map.invalidate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (selfLocationHandler != null && selfLocationRunnable != null) {
            selfLocationHandler.removeCallbacks(selfLocationRunnable);
        }
        if (animationHandler != null) animationHandler.removeCallbacks(animationRunnable);
    }

    private void startAnimationToPickup(double startLat, double startLng, double targetLat, double targetLng) {
        geoRepository.route(startLng, startLat, targetLng, targetLat).enqueue(new Callback<OsrmRouteResponse>() {
            @Override
            public void onResponse(Call<OsrmRouteResponse> call, Response<OsrmRouteResponse> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().routes.isEmpty()) {
                    List<List<Double>> pathPoints = response.body().routes.get(0).geometry.coordinates;

                    // Prosleđujemo i listu tačaka i krajnji cilj
                    animateStepByStep(pathPoints, targetLat, targetLng);

                    // Crtanje plave linije (ostaje isto)
                    drawRoutePolyline(pathPoints);
                }
            }
            @Override
            public void onFailure(Call<OsrmRouteResponse> call, Throwable t) { /* Log error */ }
        });
    }
    private void animateStepByStep(List<List<Double>> points, double destLat, double destLng) {
        final int[] currentPointIndex = {0};

        animationRunnable = new Runnable() {
            @Override
            public void run() {
                if (currentPointIndex[0] < points.size()) {
                    double lon = points.get(currentPointIndex[0]).get(0);
                    double lat = points.get(currentPointIndex[0]).get(1);

                    sendLocationToBackend(lat, lon);
                    updateDriverMarkerOnMap(lat, lon);
                    updateRoutePolyline(points, currentPointIndex[0]);

                    // PROVERA: Da li je trenutna tačka animacije blizu putnika?
                    if (isDriverCloseEnough(lat, lon, destLat, destLng)) {
                        btnStart.setEnabled(true);
                        btnStart.setAlpha(1.0f);
                    }

                    if (rideStartedUi && isDriverCloseEnough(lat, lon, destLat, destLng)) {
                        btnFinish.setEnabled(true);
                        btnFinish.setAlpha(1.0f);
                        Log.d("SIMULATION", "Destinacija dostignuta. Finish otključan.");
                    }

                    currentPointIndex[0]++;
                    animationHandler.postDelayed(this, 1000);
                } else {
                    cleanupAnimation();
                }
            }
        };
        animationHandler.post(animationRunnable);
    }
    private void updateRoutePolyline(List<List<Double>> allPoints, int currentIndex) {
        if (map == null) return;

        if (routePolyline != null) {
            map.getOverlays().remove(routePolyline);
        }

        routePolyline = new org.osmdroid.views.overlay.Polyline();

        for (int i = currentIndex; i < allPoints.size(); i++) {
            routePolyline.addPoint(new GeoPoint(allPoints.get(i).get(1), allPoints.get(i).get(0)));
        }

        routePolyline.getOutlinePaint().setColor(android.graphics.Color.BLUE);
        routePolyline.getOutlinePaint().setStrokeWidth(10f);

        map.getOverlays().add(routePolyline);
        map.invalidate();
    }

    private void sendLocationToBackend(double lat, double lng) {
        Long driverId = sessionManager.getUserId();
        if (driverId == -1L) return;

        DriverLocationUpdate dto = new DriverLocationUpdate(lat, lng);
        locationService.updateLocation(driverId, dto).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
            }
            @Override public void onFailure(Call<Void> call, Throwable t) {}
        });
    }

    private void getCoordinatesAndAnimate(String address, double startLat, double startLng) {
        geoRepository.searchNoviSad(address, "19.75,45.20,19.95,45.35").enqueue(new Callback<List<com.komsiluk.taxi.ui.ride.map.NominatimPlace>>() {
            @Override
            public void onResponse(Call<List<com.komsiluk.taxi.ui.ride.map.NominatimPlace>> call, Response<List<com.komsiluk.taxi.ui.ride.map.NominatimPlace>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    com.komsiluk.taxi.ui.ride.map.NominatimPlace place = response.body().get(0);
                    double targetLat = Double.parseDouble(place.lat);
                    double targetLng = Double.parseDouble(place.lon);

                    startAnimationToPickup(startLat, startLng, targetLat, targetLng);
                }
            }

            @Override
            public void onFailure(Call<List<com.komsiluk.taxi.ui.ride.map.NominatimPlace>> call, Throwable t) {
                Log.e("GEOCODING", "Neuspešno pretvaranje adrese: " + address);
            }
        });
    }

    private boolean isDriverCloseEnough(double driverLat, double driverLng, double pickupLat, double pickupLng) {
        float[] results = new float[1];
        android.location.Location.distanceBetween(driverLat, driverLng, pickupLat, pickupLng, results);
        float distanceInMeters = results[0];

        return distanceInMeters < 100;
    }

    private void drawRoutePolyline(List<List<Double>> pathPoints) {
        if (map == null) return;

        // Uklanjamo staru liniju ako postoji
        if (routePolyline != null) {
            map.getOverlays().remove(routePolyline);
        }

        routePolyline = new org.osmdroid.views.overlay.Polyline();
        for (List<Double> coord : pathPoints) {
            // OSRM: [lon, lat] -> osmdroid: [lat, lon]
            routePolyline.addPoint(new GeoPoint(coord.get(1), coord.get(0)));
        }

        routePolyline.getOutlinePaint().setColor(android.graphics.Color.BLUE);
        routePolyline.getOutlinePaint().setStrokeWidth(10f);

        map.getOverlays().add(routePolyline);
        map.invalidate();
    }

    private void cleanupAnimation() {
        if (animationHandler != null && animationRunnable != null) {
            animationHandler.removeCallbacks(animationRunnable);
        }
        if (routePolyline != null) {
            map.getOverlays().remove(routePolyline);
            routePolyline = null;
        }
        map.invalidate();
    }

    private void prepareAndStartMainRideSimulation() {
        if (currentRide == null) return;

        String startAddr = currentRide.getStartAddress();

        String endAddr = currentRide.getEndAddress();

        List<String> stops = currentRide.getStops();

        geocodeAndSimulateFullRoute(startAddr, stops, endAddr);
    }
    private void geocodeAndSimulateFullRoute(String start, List<String> stops, String end) {
        List<GeoPoint> allWaypoints = new java.util.ArrayList<>();

        geoRepository.searchNoviSad(start, "19.75,45.20,19.95,45.35").enqueue(new Callback<List<com.komsiluk.taxi.ui.ride.map.NominatimPlace>>() {
            @Override
            public void onResponse(Call<List<com.komsiluk.taxi.ui.ride.map.NominatimPlace>> call, Response<List<com.komsiluk.taxi.ui.ride.map.NominatimPlace>> resp) {
                if (resp.isSuccessful() && resp.body() != null && !resp.body().isEmpty()) {
                    allWaypoints.add(new GeoPoint(Double.parseDouble(resp.body().get(0).lat), Double.parseDouble(resp.body().get(0).lon)));

                    geocodeStopsRecursive(allWaypoints, stops, 0, end);
                }
            }
            @Override public void onFailure(Call<List<com.komsiluk.taxi.ui.ride.map.NominatimPlace>> call, Throwable t) {}
        });
    }

    private void geocodeStopsRecursive(List<GeoPoint> waypoints, List<String> stops, int index, String end) {
        if (stops != null && index < stops.size()) {

            geoRepository.searchNoviSad(stops.get(index), "19.75,45.20,19.95,45.35").enqueue(new Callback<List<com.komsiluk.taxi.ui.ride.map.NominatimPlace>>() {
                @Override
                public void onResponse(Call<List<com.komsiluk.taxi.ui.ride.map.NominatimPlace>> call, Response<List<com.komsiluk.taxi.ui.ride.map.NominatimPlace>> resp) {
                    if (resp.isSuccessful() && resp.body() != null && !resp.body().isEmpty()) {
                        waypoints.add(new GeoPoint(Double.parseDouble(resp.body().get(0).lat), Double.parseDouble(resp.body().get(0).lon)));
                    }

                    geocodeStopsRecursive(waypoints, stops, index + 1, end);
                }
                @Override public void onFailure(Call<List<com.komsiluk.taxi.ui.ride.map.NominatimPlace>> call, Throwable t) {
                    geocodeStopsRecursive(waypoints, stops, index + 1, end); // Nastavi čak i ako jedna stanica fail-uje
                }
            });
        } else {

            geoRepository.searchNoviSad(end, "19.75,45.20,19.95,45.35").enqueue(new Callback<List<com.komsiluk.taxi.ui.ride.map.NominatimPlace>>() {
                @Override
                public void onResponse(Call<List<com.komsiluk.taxi.ui.ride.map.NominatimPlace>> call, Response<List<com.komsiluk.taxi.ui.ride.map.NominatimPlace>> resp) {
                    if (resp.isSuccessful() && resp.body() != null && !resp.body().isEmpty()) {
                        waypoints.add(new GeoPoint(Double.parseDouble(resp.body().get(0).lat), Double.parseDouble(resp.body().get(0).lon)));

                        executeMultiRoute(waypoints);
                    }
                }
                @Override public void onFailure(Call<List<com.komsiluk.taxi.ui.ride.map.NominatimPlace>> call, Throwable t) {}
            });
        }
    }
    private void executeMultiRoute(List<GeoPoint> allWaypoints) {
        geoRepository.routeMulti(allWaypoints).enqueue(new Callback<OsrmRouteResponse>() {
            @Override
            public void onResponse(Call<OsrmRouteResponse> call, Response<OsrmRouteResponse> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().routes.isEmpty()) {
                    List<List<Double>> fullPath = response.body().routes.get(0).geometry.coordinates;

                    double finalLat = allWaypoints.get(allWaypoints.size() - 1).getLatitude();
                    double finalLng = allWaypoints.get(allWaypoints.size() - 1).getLongitude();

                    if (animationHandler != null) animationHandler.removeCallbacks(animationRunnable);

                    animateStepByStep(fullPath, finalLat, finalLng);
                    drawRoutePolyline(fullPath);
                }
            }
            @Override
            public void onFailure(Call<OsrmRouteResponse> call, Throwable t) {
                Log.e("MULTI_ROUTE", "Greška pri pravljenju rute");
            }
        });
    }

    private void finishRideOnBackend() {
        Long rideId = getCurrentRideId();
        if (rideId == null) return;

        rideService.finishRide(rideId).enqueue(new Callback<RideResponse>() {
            @Override
            public void onResponse(Call<RideResponse> call, Response<RideResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(DriverActivity.this, "Vožnja uspešno završena!", Toast.LENGTH_SHORT).show();
                    rideStartedUi = false;
                    currentRide = null;
                    cleanupAnimation();
                    sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    if (myLocationMarker != null) myLocationMarker.setIcon(iconFree);
                } else {
                    Toast.makeText(DriverActivity.this, "Greška pri završavanju vožnje.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(Call<RideResponse> call, Throwable t) {
                Log.e("FINISH_RIDE", "Greška: " + t.getMessage());
            }
        });
    }

    private String safe(String s) {
        if (s == null) return "";
        String t = s.trim();
        return t.isEmpty() ? "" : t;
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
