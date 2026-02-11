package com.komsiluk.taxi;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.komsiluk.taxi.data.remote.inconsistency_report.InconsistencyReportCreate;
import com.komsiluk.taxi.data.remote.inconsistency_report.InconsistencyService;
import com.komsiluk.taxi.data.remote.location.DriverLocationResponse;
import com.komsiluk.taxi.data.remote.location.DriverLocationUpdate;
import com.komsiluk.taxi.data.remote.location.LocationService;
import com.komsiluk.taxi.data.remote.profile.UserProfileResponse;
import com.komsiluk.taxi.data.remote.profile.UserService;
import com.komsiluk.taxi.data.remote.ride.PanicRequestDTO;
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

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
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

    private Handler rideCheckHandler = new Handler(Looper.getMainLooper());
    private Runnable rideCheckRunnable;
    private boolean isFetchingRide = false;

    private List<Marker> rideMarkers = new ArrayList<>();

    @Inject
    SessionManager sessionManager;

    @Inject
    RideService rideService;

    @Inject
    LocationService locationService;

    @Inject
    OkHttpClient okHttpClient;
    private View rowPassenger;
    private ImageView ivPassenger;
    private TextView tvPassengerName;
    private TextView tvPassengerEmail;

    private LinearLayout layoutActiveStops;

    @Inject
    UserService userService;

    @Inject
    InconsistencyService inconsistencyService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE));

        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        map.setMultiTouchControls(true);
        map.getController().setZoom(13.5);
        initializeMapPosition();

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

        rowPassenger = findViewById(R.id.rowPassenger);
        ivPassenger = findViewById(R.id.ivPassenger);
        tvPassengerName = findViewById(R.id.tvPassengerName);
        tvPassengerEmail = findViewById(R.id.tvPassengerEmail);

        rowStartCancel = findViewById(R.id.rowStartCancel);
        rowInRide = findViewById(R.id.rowInRide);

        btnCancel = findViewById(R.id.btnCancelRide);
        btnStart = findViewById(R.id.btnStartRide);

        btnFinish = findViewById(R.id.btnFinishRide);
        btnStop = findViewById(R.id.btnStopRide);
        btnReport = findViewById(R.id.btnReportRide);
        btnPanic = findViewById(R.id.btnPanicRide);
        layoutActiveStops = findViewById(R.id.layoutActiveStops);

        btnCancel.setOnClickListener(v -> {
            Toast.makeText(this, "Cancel not implemented yet.", Toast.LENGTH_SHORT).show();
        });

        btnStart.setOnClickListener(v -> showConfirmStartDialog());

        // U onCreate metodi
        btnFinish.setOnClickListener(v -> finishRideOnBackend());
        btnStop.setOnClickListener(v -> Toast.makeText(this, "Stop not implemented yet.", Toast.LENGTH_SHORT).show());
        btnReport.setOnClickListener(v -> showReportInconsistencyDialog());
        btnPanic.setOnClickListener(v -> showPanicDialog());

        geoRepository = new GeoRepository(okHttpClient);
        startSelfLocationTracking();
    }

    private void showPanicDialog() {
        Long rideId = getCurrentRideId();
        if (rideId == null) {
            Toast.makeText(this, "No active ride found.", Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_panic_action, null);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btnCancelPanic);
        MaterialButton btnConfirm = dialogView.findViewById(R.id.btnInitiatePanic);

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            dialog.dismiss();

            Long userId = sessionManager.getUserId();
            if (userId == null) {
                Toast.makeText(this, "User session not found. Please login again.", Toast.LENGTH_SHORT).show();
                return;
            }

            PanicRequestDTO panicDto = new PanicRequestDTO();
            panicDto.setInitiatorId(userId);

            rideService.panic(rideId, panicDto).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(
                                DriverActivity.this,
                                "Panic signal sent! Emergency services notified.",
                                Toast.LENGTH_LONG
                        ).show();

                    } else {
                        Toast.makeText(
                                DriverActivity.this,
                                "Failed to send panic signal. Code: " + response.code(),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(
                            DriverActivity.this,
                            "Network error: " + t.getMessage(),
                            Toast.LENGTH_SHORT
                    ).show();
                }
            });
        });

        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startRidePolling();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopRidePolling();
    }

    private void startRidePolling() {
        rideCheckRunnable = new Runnable() {
            @Override
            public void run() {
                fetchCurrentRideAndUpdateUi();
                rideCheckHandler.postDelayed(this, 1500);
            }
        };
        rideCheckHandler.post(rideCheckRunnable);
    }

    private void stopRidePolling() {
        if (rideCheckHandler != null && rideCheckRunnable != null) {
            rideCheckHandler.removeCallbacks(rideCheckRunnable);
        }
    }

    private void fetchCurrentRideAndUpdateUi() {
        if (isFetchingRide) return;

        Long driverId = sessionManager != null ? sessionManager.getUserId() : null;
        if (driverId == null) return;

        isFetchingRide = true;
        rideService.getDriverCurrentRide(driverId).enqueue(new Callback<RideResponse>() {
            @Override
            public void onResponse(Call<RideResponse> call, Response<RideResponse> resp) {
                isFetchingRide = false;

                if (resp.code() == 204 || !resp.isSuccessful() || resp.body() == null) {
                    // Ako nema vožnje, sakrij sheet i ugasi animacije
                    if (currentRide != null) {
                        currentRide = null;
                        sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                        cleanupAnimation();
                    }
                    return;
                }

                RideResponse newRide = resp.body();

                // KLJUČNA PROVERA: Da li je ovo nova vožnja ili promena statusa?
                if (currentRide == null || !currentRide.getId().equals(newRide.getId()) || !currentRide.getStatus().equals(newRide.getStatus())) {

                    currentRide = newRide;
                    rideStartedUi = isRideActiveStatus(currentRide.getStatus());

                    // Popuni UI
                    tvPickupValue.setText(safe(currentRide.getStartAddress()));
                    tvDestinationValue.setText(safe(currentRide.getEndAddress()));

                    if (layoutActiveStops != null) {
                        layoutActiveStops.removeAllViews();
                        List<String> stops = newRide.getStops();
                        if (stops != null) {
                            // Dodajemo brojač za oznake
                            for (int i = 0; i < stops.size(); i++) {
                                addStopToActivePanel(stops.get(i), i + 1); // Prosleđujemo redni broj
                            }
                        }
                    }
                    bindCreatorProfile(currentRide.getCreatorId());
                    applyRideButtonsUi();
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                    // POKRENI ANIMACIJU samo ako vožnja NIJE aktivna (znači ide ka putniku)
                    if (!rideStartedUi && myLocationMarker != null) {
                        cleanupAnimation(); // Ugasi staru ako postoji
                        double myLat = myLocationMarker.getPosition().getLatitude();
                        double myLng = myLocationMarker.getPosition().getLongitude();
                        getCoordinatesAndAnimate(currentRide.getStartAddress(), myLat, myLng);
                    }
                }
            }

            @Override
            public void onFailure(Call<RideResponse> call, Throwable t) {
                isFetchingRide = false;
            }
        });
    }

    private void addStopToActivePanel(String address, int stopNumber) {
        if (layoutActiveStops == null) {
            Log.e("STOPS_DEBUG", "layoutActiveStops je NULL!");
            return;
        }

        TextView label = new TextView(this);
        // Postavljamo dinamički tekst: Stop 1, Stop 2...
        label.setText("Stop " + stopNumber);
        label.setTextColor(android.graphics.Color.WHITE);
        label.setPadding(0, 10, 0, 0);

        TextView value = new TextView(this);
        value.setText(address);
        value.setBackgroundResource(R.drawable.bg_input_normal);
        value.setPadding(32, 24, 32, 24);

        value.setTextColor(getResources().getColor(R.color.text));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 20);

        layoutActiveStops.addView(label);
        layoutActiveStops.addView(value, params);
        layoutActiveStops.requestLayout();
    }
    private void initializeMapPosition() {
        Long myId = sessionManager.getUserId();
        if (myId == null) return;

        locationService.getSpecificDriverLocation(myId).enqueue(new Callback<DriverLocationResponse>() {
            @Override
            public void onResponse(Call<DriverLocationResponse> call, Response<DriverLocationResponse> response) {
                GeoPoint finalPoint;

                if (response.isSuccessful() && response.body() != null) {
                    DriverLocationResponse loc = response.body();
                    finalPoint = new GeoPoint(loc.getLat(), loc.getLng());
                } else {
                    finalPoint = new GeoPoint(45.2671, 19.8335);
                }

                setMapCamera(finalPoint);
            }

            @Override
            public void onFailure(Call<DriverLocationResponse> call, Throwable t) {
                GeoPoint defaultPoint = new GeoPoint(45.2671, 19.8335);
                setMapCamera(defaultPoint);
            }
        });
    }

    private void setMapCamera(GeoPoint point) {
        firstTimeZoom = false;
        map.getController().setZoom(15.5); // Postavi inicijalni zoom
        map.getController().setCenter(point);
        updateDriverMarkerOnMap(point.getLatitude(), point.getLongitude());
    }
    private void bindCreatorProfile(Long creatorId) {
        if (rowPassenger != null) rowPassenger.setVisibility(View.GONE);

        if (creatorId == null || userService == null) return;

        userService.getProfile(creatorId).enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> resp) {
                if (!resp.isSuccessful() || resp.body() == null) return;

                UserProfileResponse p = resp.body();

                String first = safe(p.getFirstName());
                String last  = safe(p.getLastName());
                String fullName = (first + " " + last).trim();
                if (fullName.isEmpty()) fullName = "(Unknown passenger)";

                if (tvPassengerName != null) tvPassengerName.setText(fullName);
                if (tvPassengerEmail != null) tvPassengerEmail.setText(safe(p.getEmail()));

                String rel = safe(p.getProfileImageUrl());
                if (!rel.isEmpty() && ivPassenger != null) {
                    String url = buildAbsoluteImageUrl(rel);

                    Glide.with(DriverActivity.this)
                            .load(url)
                            .placeholder(R.drawable.ic_launcher_foreground)
                            .error(R.drawable.ic_launcher_foreground)
                            .circleCrop()
                            .into(ivPassenger);
                } else if (ivPassenger != null) {
                    ivPassenger.setImageResource(R.drawable.ic_launcher_foreground);
                }

                if (rowPassenger != null) rowPassenger.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
            }
        });
    }

    private String buildAbsoluteImageUrl(String relativePath) {
        String rel = relativePath.startsWith("/") ? relativePath.substring(1) : relativePath;
        String base = "http://"+ BuildConfig.IP_ADDR +":8081/";
        return base + rel;
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
                        Toast.makeText(this, "Ride started successfully!", Toast.LENGTH_SHORT).show();
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

                    if (isDriverCloseEnough(lat, lon, destLat, destLng)) {
                        btnStart.setEnabled(true);
                        btnStart.setAlpha(1.0f);
                    }

                    if (rideStartedUi && isDriverCloseEnough(lat, lon, destLat, destLng)) {
                        btnFinish.setEnabled(true);
                        btnFinish.setAlpha(1.0f);
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

        routePolyline.getOutlinePaint().setColor(android.graphics.Color.BLACK);
        routePolyline.getOutlinePaint().setStrokeWidth(12f);

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

    private void addRideMarker(GeoPoint point, int iconRes) {
        Marker m = new Marker(map);
        m.setPosition(point);
        m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        m.setIcon(getDrawable(iconRes));
        map.getOverlays().add(m);
        rideMarkers.add(m);
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

        for (Marker m : rideMarkers) {
            map.getOverlays().remove(m);
        }

        if (layoutActiveStops != null) {
            layoutActiveStops.removeAllViews();
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
                        GeoPoint pt = new GeoPoint(Double.parseDouble(resp.body().get(0).lat), Double.parseDouble(resp.body().get(0).lon));
                        waypoints.add(pt);
                        addRideMarker(pt, R.drawable.station);
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
                        GeoPoint pt = new GeoPoint(Double.parseDouble(resp.body().get(0).lat), Double.parseDouble(resp.body().get(0).lon));
                        waypoints.add(pt);
                        addRideMarker(pt, R.drawable.flag);

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
                    Toast.makeText(DriverActivity.this, "Ride finished successfully!", Toast.LENGTH_SHORT).show();
                    rideStartedUi = false;
                    currentRide = null;
                    cleanupAnimation();
                    sheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    if (myLocationMarker != null) myLocationMarker.setIcon(iconFree);
                } else {
                    Toast.makeText(DriverActivity.this, "Error.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(Call<RideResponse> call, Throwable t) {
                Log.e("FINISH_RIDE", "Error: " + t.getMessage());
            }
        });
    }

    private void showReportInconsistencyDialog() {
        // Provera ID-a vožnje pomoću tvoje postojeće pomoćne metode
        Long rideId = getCurrentRideId();
        if (rideId == null) {
            Toast.makeText(this, "No active ride found.", Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_report_inconsistency, null);
        EditText etMessage = dialogView.findViewById(R.id.etReportMessage);
        MaterialButton btnCancel = dialogView.findViewById(R.id.btnCancelReport);
        MaterialButton btnSend = dialogView.findViewById(R.id.btnSendReport);

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSend.setOnClickListener(v -> {
            String message = etMessage.getText().toString().trim();

            if (message.isEmpty()) {
                Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            if (message.length() > 200) {
                Toast.makeText(this, "Message too long (max 200 chars)", Toast.LENGTH_SHORT).show();
                return;
            }

            sendInconsistencyReport(message, dialog, rideId);
        });

        dialog.show();
    }
    private void sendInconsistencyReport(String message, AlertDialog dialog, Long rideId) {
        InconsistencyReportCreate dto = new InconsistencyReportCreate();
        dto.setMessage(message);

        inconsistencyService.reportInconsistency(rideId, dto).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(DriverActivity.this, "Report sent successfully", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(DriverActivity.this, "Failed to send report: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(DriverActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
