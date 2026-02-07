package com.komsiluk.taxi.ui.passenger.ride_history;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.komsiluk.taxi.R;
import com.komsiluk.taxi.UserActivity;
import com.komsiluk.taxi.data.remote.favorite.FavoriteRouteCreateRequest;
import com.komsiluk.taxi.data.remote.favorite.FavoriteRouteResponse;
import com.komsiluk.taxi.data.remote.favorite.FavoriteService;
import com.komsiluk.taxi.data.remote.passenger_ride_history.DriverResponseDTO;
import com.komsiluk.taxi.data.remote.passenger_ride_history.InconsistencyReportResponseDTO;
import com.komsiluk.taxi.data.remote.passenger_ride_history.PassengerRideDetailsDTO;
import com.komsiluk.taxi.data.remote.passenger_ride_history.RatingResponseDTO;
import com.komsiluk.taxi.data.remote.passenger_ride_history.RouteResponseDTO;
import com.komsiluk.taxi.data.remote.passenger_ride_history.VehicleType;
import com.komsiluk.taxi.data.session.SessionManager;
import com.komsiluk.taxi.databinding.DialogPassengerRideDetailsBinding;
import com.komsiluk.taxi.ui.ride.map.GeoRepository;
import com.komsiluk.taxi.ui.ride.map.NominatimPlace;
import com.komsiluk.taxi.ui.ride.map.OsrmRouteResponse;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class PassengerRideDetailsDialogFragment extends DialogFragment {

    private DialogPassengerRideDetailsBinding binding;
    private PassengerRideDetailsDTO rideDetails;

    private MapView map;
    private Marker pickupMarker;
    private Marker destMarker;
    private Polyline routeLine;

    private List<Long> currentMatchingFavoriteIds = null;
    @Inject
    FavoriteService favoriteApi;

    GeoRepository geoRepo;

    @Inject
    SessionManager sessionManager;


    public static PassengerRideDetailsDialogFragment newInstance(PassengerRideDetailsDTO details) {
        PassengerRideDetailsDialogFragment fragment = new PassengerRideDetailsDialogFragment();
        Bundle args = new Bundle();
        
        Gson gson = new Gson();
        args.putString("details", gson.toJson(details));
        
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            int w = (int) (requireContext().getResources().getDisplayMetrics().widthPixels * 0.92f);
            int h = (int) (requireContext().getResources().getDisplayMetrics().heightPixels * 0.85f);

            getDialog().getWindow().setLayout(w, h);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DialogPassengerRideDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args == null) return;

        Gson gson = new Gson();
        String detailsJson = args.getString("details");
        rideDetails = gson.fromJson(detailsJson, PassengerRideDetailsDTO.class);

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

        map = view.findViewById(R.id.mapDetails);
        setupMap();

        populateUI();
        setupButtons();

        checkFavoriteStatus();

        if (rideDetails != null && rideDetails.getRoute() != null) {
            drawRideHistoryRoute();
        }
    }


    private void checkFavoriteStatus() {
        Long userId = sessionManager != null ? sessionManager.getUserId() : null;
        favoriteApi.getFavorites(userId).enqueue(new Callback<List<FavoriteRouteResponse>>() {
            @Override
            public void onResponse(Call<List<FavoriteRouteResponse>> call, Response<List<FavoriteRouteResponse>> response) {
                if (!isAdded() || getContext() == null) return;

                if (response.isSuccessful() && response.body() != null) {
                    List<Long> matchingIds = new ArrayList<>();
                    for (FavoriteRouteResponse fav : response.body()) {
                        if (isFavoriteMatch(fav)) {
                            matchingIds.add(fav.getId());
                        }
                    }

                    currentMatchingFavoriteIds = matchingIds.isEmpty() ? null : matchingIds;
                    updateFavoriteIcon(!matchingIds.isEmpty());
                }
            }

            @Override
            public void onFailure(Call<List<FavoriteRouteResponse>> call, Throwable t) {
            }
        });
    }



    private boolean isFavoriteMatch(FavoriteRouteResponse fav) {
        if (!fav.getStartAddress().equals(rideDetails.getRoute().getStartAddress())) return false;
        if (!fav.getEndAddress().equals(rideDetails.getRoute().getEndAddress())) return false;

        String rideVehicleType = mapVehicleType(rideDetails.getVehicleType());
        if (!rideVehicleType.equals(fav.getVehicleType())) return false;

        if (fav.isPetFriendly() != rideDetails.isPetFriendly()) return false;
        if (fav.isBabyFriendly() != rideDetails.isBabyFriendly()) return false;

        String rideStops = rideDetails.getRoute().getStops() != null ? rideDetails.getRoute().getStops() : "";
        List<String> favStops = fav.getStops() != null ? fav.getStops() : new ArrayList<>();
        String favStopsStr = String.join("|", favStops);
        if (!rideStops.equals(favStopsStr)) return false;

        return true;
    }
    private void updateFavoriteIcon(boolean isFavorite) {
        if (isFavorite) {
            binding.btnFavorite.setImageResource(R.drawable.favorite_filled);
        } else {
            binding.btnFavorite.setImageResource(R.drawable.favorite);
        }
    }

    private void setupMap() {
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);

        map.getController().setZoom(13.0);
        map.getController().setCenter(new GeoPoint(45.2671, 19.8335));
    }

    private void drawRideHistoryRoute() {
        String pickup = extractStreetAddress(rideDetails.getRoute().getStartAddress());
        String destination = extractStreetAddress(rideDetails.getRoute().getEndAddress());

        List<String> stops = new ArrayList<>();
        String stopsData = rideDetails.getRoute().getStops();
        if (stopsData != null && !stopsData.isEmpty()) {
            String[] parts = stopsData.split("\\|");
            for (String s : parts) stops.add(extractStreetAddress(s));
        }

        geocodeAndDrawRoute(pickup, destination, stops);
    }

    private void geocodeAndDrawRoute(String pickupAddr, String destAddr, List<String> stopsAddr) {
        List<GeoPoint> allRoutePoints = new ArrayList<>();


        geoRepo.searchNoviSad(pickupAddr, "19.7,45.3,19.9,45.2").enqueue(new Callback<List<NominatimPlace>>() {
            @Override
            public void onResponse(Call<List<NominatimPlace>> call, Response<List<NominatimPlace>> response) {
                if (!isAdded() || getContext() == null) return;

                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {

                    GeoPoint startPt = new GeoPoint(
                            Double.parseDouble(response.body().get(0).lat),
                            Double.parseDouble(response.body().get(0).lon)
                    );
                    allRoutePoints.add(startPt);
                    setMarker(true, startPt, "Start: " + pickupAddr);

                    geocodeStopsRecursive(stopsAddr, 0, allRoutePoints, () -> {

                        geoRepo.searchNoviSad(destAddr, "19.7,45.3,19.9,45.2").enqueue(new Callback<List<NominatimPlace>>() {
                            @Override
                            public void onResponse(Call<List<NominatimPlace>> call, Response<List<NominatimPlace>> response) {
                                if (!isAdded() || getContext() == null) return;

                                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                                    GeoPoint endPt = new GeoPoint(
                                            Double.parseDouble(response.body().get(0).lat),
                                            Double.parseDouble(response.body().get(0).lon)
                                    );
                                    allRoutePoints.add(endPt);
                                    setMarker(false, endPt, "End: " + destAddr);
                                    drawComplexRoute(allRoutePoints);
                                }
                            }
                            @Override
                            public void onFailure(Call<List<NominatimPlace>> call, Throwable t) {
                                Log.e("GEO", "End fail", t);
                            }
                        });
                    });
                } else {
                    if (isAdded()) {
                        Toast.makeText(requireContext(), "Could not locate pickup location", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<NominatimPlace>> call, Throwable t) {
                Log.e("GEO", "Start fail", t);
                if (isAdded()) {
                    Toast.makeText(requireContext(), "Network error loading route", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void geocodeStopsRecursive(List<String> stops, int index, List<GeoPoint> points, Runnable onComplete) {
        if (index >= stops.size()) {
            onComplete.run();
            return;
        }

        String currentStop = stops.get(index);

        geoRepo.searchNoviSad(currentStop, "19.7,45.3,19.9,45.2").enqueue(new Callback<List<NominatimPlace>>() {
            @Override
            public void onResponse(Call<List<NominatimPlace>> call, Response<List<NominatimPlace>> response) {

                if (!isAdded() || getContext() == null) return;
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    GeoPoint stopPt = new GeoPoint(
                            Double.parseDouble(response.body().get(0).lat),
                            Double.parseDouble(response.body().get(0).lon)
                    );
                    points.add(stopPt);
                    addStationMarker(stopPt, "Stop: " + currentStop);
                }
                geocodeStopsRecursive(stops, index + 1, points, onComplete);
            }

            @Override
            public void onFailure(Call<List<NominatimPlace>> call, Throwable t) {
                Log.e("GEO", "Stop fail", t);
                geocodeStopsRecursive(stops, index + 1, points, onComplete);
            }
        });
    }



    private void setMarker(boolean isPickup, GeoPoint pt, String title) {
        if (!isAdded() || getContext() == null || map == null) return;

        Marker marker = new Marker(map);
        marker.setPosition(pt);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle(title);

        marker.setIcon(androidx.core.content.ContextCompat.getDrawable(
                requireContext(),
                isPickup ? R.drawable.car : R.drawable.flag
        ));

        map.getOverlays().add(marker);
        if (isPickup) pickupMarker = marker; else destMarker = marker;
    }

    private void drawPolyline(List<List<Double>> coords) {

        if (coords == null || coords.isEmpty()) {
            return;
        }


        if(!isAdded() || map == null) return;

        if (routeLine != null) {
            map.getOverlays().remove(routeLine);
        }

        List<GeoPoint> pts = new ArrayList<>();
        for (List<Double> c : coords) {
            pts.add(new GeoPoint(c.get(1), c.get(0)));
        }


        routeLine = new Polyline();
        routeLine.setPoints(pts);
        routeLine.getOutlinePaint().setColor(android.graphics.Color.BLUE);
        routeLine.getOutlinePaint().setStrokeWidth(10f);


        map.getOverlays().add(routeLine);


        map.invalidate();
    }

    private void addStationMarker(GeoPoint pt, String title) {
        if (!isAdded() || getContext() == null || map == null) return;

        Marker marker = new Marker(map);
        marker.setPosition(pt);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        marker.setTitle(title);
        marker.setIcon(androidx.core.content.ContextCompat.getDrawable(requireContext(), R.drawable.station));
        map.getOverlays().add(marker);
    }

    private void drawComplexRoute(List<GeoPoint> points) {

        geoRepo.routeMulti(points).enqueue(new Callback<OsrmRouteResponse>() {
            @Override
            public void onResponse(Call<OsrmRouteResponse> call, Response<OsrmRouteResponse> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().routes.isEmpty()) {

                    List<List<Double>> coords = response.body().routes.get(0).geometry.coordinates;
                    drawPolyline(coords);


                    if (map != null) {
                        List<Marker> markers = new ArrayList<>();
                        for (org.osmdroid.views.overlay.Overlay overlay :
                                map.getOverlays()) {
                            if (overlay instanceof Marker) {
                                markers.add((Marker) overlay);
                            }
                        }

                        for (Marker m : markers) {
                            map.getOverlays().remove(m);
                        }

                        for (Marker m : markers) {
                            map.getOverlays().add(m);
                        }
                    }

                    zoomToPoints(points);

                    if (map != null && isAdded()) {
                        map.invalidate();
                    }
                }
            }
            @Override public void onFailure(Call<OsrmRouteResponse> call, Throwable t) {
                if (isAdded()) {
                    Toast.makeText(requireContext(),
                            "Failed to load route",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void zoomToPoints(List<GeoPoint> points) {
        if (points.isEmpty()) return;

        double minLat = Double.MAX_VALUE, maxLat = -Double.MAX_VALUE;
        double minLon = Double.MAX_VALUE, maxLon = -Double.MAX_VALUE;

        for (GeoPoint p : points) {
            minLat = Math.min(minLat, p.getLatitude());
            maxLat = Math.max(maxLat, p.getLatitude());
            minLon = Math.min(minLon, p.getLongitude());
            maxLon = Math.max(maxLon, p.getLongitude());
        }

        BoundingBox bb = new BoundingBox(maxLat + 0.005, maxLon + 0.005, minLat - 0.005, minLon - 0.005);
        map.zoomToBoundingBox(bb, true, 100);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (map != null) map.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        if (map != null) map.onPause();
    }

    private void populateUI() {
        int labelWhite = android.R.color.white;
        int valueYellow = R.color.secondary;

        RouteResponseDTO route = rideDetails.getRoute();
        if (route != null) {
            binding.tvPickup.setText(labelValue("Pickup: ", extractStreetAddress(route.getStartAddress()), labelWhite, valueYellow));
            binding.tvDestination.setText(labelValue("Destination: ", extractStreetAddress(route.getEndAddress()), labelWhite, valueYellow));

            if (route.getStops() != null && !route.getStops().isEmpty()) {
                populateStops(route.getStops());
            }
        }

        String status = rideDetails.getStatus() != null ? rideDetails.getStatus().name() : "N/A";
        binding.tvStatus.setText(labelValue("Status: ", status, labelWhite, valueYellow));

        binding.tvStartTime.setText(labelValue("Start time: ", formatDateTime(rideDetails.getStartTime()), labelWhite, valueYellow));
        binding.tvEndTime.setText(labelValue("End time: ", formatDateTime(rideDetails.getEndTime()), labelWhite, valueYellow));

        if (rideDetails.getScheduledAt() != null) {
            binding.tvScheduledTime.setVisibility(View.VISIBLE);
            binding.tvScheduledTime.setText(labelValue("Scheduled: ", formatDateTime(rideDetails.getScheduledAt()), labelWhite, valueYellow));
        }

        binding.tvDistance.setText(labelValue("Distance: ", String.format(Locale.getDefault(), "%.2f km", rideDetails.getDistanceKm()), labelWhite, valueYellow));
        binding.tvDuration.setText(labelValue("Estimated duration: ", rideDetails.getEstimatedDurationMin() + " min", labelWhite, valueYellow));
        
        String price = rideDetails.getPrice() != null ? rideDetails.getPrice().toString() + " $" : "N/A";
        binding.tvPrice.setText(labelValue("Price: ", price, labelWhite, valueYellow));

        String vehicleType = rideDetails.getVehicleType() != null ? rideDetails.getVehicleType().name() : "N/A";
        binding.tvVehicleType.setText(labelValue("Vehicle type: ", vehicleType, labelWhite, valueYellow));

        String arePetsAllowed = "";
        arePetsAllowed = rideDetails.isPetFriendly() ? " Yes" : "No";
        binding.tvPetFriendly.setText(labelValue("Pets allowed: ", arePetsAllowed, labelWhite, valueYellow));

        String areBabiesAllowed = "";
        areBabiesAllowed = rideDetails.isBabyFriendly() ? " Yes" : "No";
        binding.tvBabyFriendly.setText(labelValue("Babies allowed: ", areBabiesAllowed, labelWhite, valueYellow));

        populateDriverInfo();

        populateRatings();

        populateInconsistencyReports();

        String panicText = rideDetails.isPanicTriggered() ? "Yes" : "No";
        binding.tvPanicFlag.setText(labelValue("Panic button: ", panicText, labelWhite, valueYellow));

        String cancelText = rideDetails.isCanceled() ? "Yes" : "No";
        if (rideDetails.isCanceled() && rideDetails.getCancellationSource() != null) {
            cancelText += " (by " + rideDetails.getCancellationSource().name() + ")";
            if (rideDetails.getCancellationReason() != null) {
                cancelText += " - " + rideDetails.getCancellationReason();
            }
        }
        binding.tvCancelFlag.setText(labelValue("Canceled: ", cancelText, labelWhite, valueYellow));
    }

    private void populateStops(String stopsString) {
        
        binding.layoutStops.removeAllViews();

        String[] stops = stopsString.split("\\|");

        for (int i = 0; i < stops.length; i++) {
            TextView tvStop = new TextView(requireContext());
            tvStop.setText(labelValue("Stop " + (i + 1) + ": ", extractStreetAddress(stops[i]),
                    android.R.color.white, R.color.secondary));
            tvStop.setTextColor(ContextCompat.getColor(requireContext(), R.color.secondary));
            tvStop.setTypeface(tvStop.getTypeface(), android.graphics.Typeface.NORMAL);
            
            binding.layoutStops.addView(tvStop);
        }
    }

    private String extractStreetAddress(String fullAddress) {
        if (fullAddress == null || fullAddress.trim().isEmpty()) {
            return "";
        }
        return fullAddress.split(",")[0].trim();
    }

    private void populateDriverInfo() {
        DriverResponseDTO driver = rideDetails.getDriver();
        if (driver != null) {
            binding.tvDriverName.setText(labelValue("Name: ", driver.getFullName(), 
                    android.R.color.white, R.color.secondary));
            binding.tvDriverEmail.setText(labelValue("Email: ", driver.getEmail(), 
                    android.R.color.white, R.color.secondary));
            binding.tvDriverPhone.setText(labelValue("Phone: ", driver.getPhoneNumber(), 
                    android.R.color.white, R.color.secondary));

            if (driver.getVehicle() != null) {
                String vehicleInfo = driver.getVehicle().getMake() + " - " +
                                    driver.getVehicle().getModel() + " (" + 
                                    driver.getVehicle().getLicensePlate() + ")";
                binding.tvVehicleInfo.setText(labelValue("Vehicle: ", vehicleInfo, 
                        android.R.color.white, R.color.secondary));
            }
        } else {
            binding.tvDriverName.setText("Driver information not available");
        }
    }

    private void populateRatings() {
        List<RatingResponseDTO> ratings = rideDetails.getRatings();
        
        if (ratings == null || ratings.isEmpty()) {
            binding.tvRatings.setText("No ratings yet");
            return;
        }

        StringBuilder ratingsText = new StringBuilder();
        
        for (int i = 0; i < ratings.size(); i++) {
            RatingResponseDTO rating = ratings.get(i);
            
            ratingsText.append(rating.getRaterMail()).append("\n");
            
            if (rating.getDriverGrade() != null) {
                ratingsText.append("Driver rating: ").append(rating.getDriverGrade()).append("\n");
            } else {
                ratingsText.append("Driver rating: N/A\n");
            }
            
            if (rating.getVehicleGrade() != null) {
                ratingsText.append("Vehicle rating: ").append(rating.getVehicleGrade()).append("\n");
            } else {
                ratingsText.append("Vehicle rating: N/A\n");
            }
            
            String comment = rating.getComment() != null && !rating.getComment().isEmpty() 
                    ? rating.getComment() : "N/A";
            ratingsText.append("Comment: ").append(comment);
            
            if (i < ratings.size() - 1) {
                ratingsText.append("\n\n");
            }
        }
        
        binding.tvRatings.setText(colorizeLabelsUntilColon(ratingsText.toString(), 
                android.R.color.white, R.color.secondary));
    }

    private void populateInconsistencyReports() {
        List<InconsistencyReportResponseDTO> reports = rideDetails.getInconsistencyReports();
        
        if (reports == null || reports.isEmpty()) {
            binding.tvInconsistencyReports.setText("No inconsistency reports");
            return;
        }

        StringBuilder reportsText = new StringBuilder();
        
        for (int i = 0; i < reports.size(); i++) {
            InconsistencyReportResponseDTO report = reports.get(i);
            
            reportsText.append("Reporter: ").append(report.getReporterEmail()).append("\n");
            reportsText.append("Role: ").append(report.getReporterRole()).append("\n");
            reportsText.append("Message: ").append(report.getMessage()).append("\n");
            reportsText.append("Date: ").append(formatDateTime(report.getCreatedAt()));
            
            if (i < reports.size() - 1) {
                reportsText.append("\n\n");
            }
        }
        
        binding.tvInconsistencyReports.setText(colorizeLabelsUntilColon(reportsText.toString(), 
                android.R.color.white, R.color.secondary));
    }

    private void setupButtons() {
        binding.btnClose.setOnClickListener(v -> dismiss());

        binding.btnBookAgain.setOnClickListener(v -> {
            if (rideDetails != null) {
                Intent intent = new Intent(requireContext(), UserActivity.class);

                Gson gson = new Gson();
                intent.putExtra("ORDER_AGAIN_DTO", gson.toJson(rideDetails));

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                startActivity(intent);
                dismiss();

                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        });


        binding.btnFavorite.setOnClickListener(v -> {
            if (currentMatchingFavoriteIds != null && !currentMatchingFavoriteIds.isEmpty()) {
                deleteAllMatchingFavorites(currentMatchingFavoriteIds);
            } else {
                showAddFavoriteDialog();
            }
        });

        binding.btnAddRating.setOnClickListener(v -> {
            // TODO: Implementiraj rating dialog
            Toast.makeText(getContext(), "Rate ride - Coming soon!", Toast.LENGTH_SHORT).show();
        });
    }

    private void deleteAllMatchingFavorites(List<Long> favoriteIds) {
        int totalCount = favoriteIds.size();
        final int[] deletedCount = {0};
        final boolean[] hasError = {false};

        for (Long id : favoriteIds) {
            favoriteApi.deleteFavorite(id).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (!isAdded() || getContext() == null) return;

                    deletedCount[0]++;
                    if (deletedCount[0] == totalCount && !hasError[0]) {
                        currentMatchingFavoriteIds = null;
                        updateFavoriteIcon(false);

                        String message = totalCount > 1
                                ? "Removed " + totalCount + " matching favorites"
                                : "Removed from favorites";
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    hasError[0] = true;
                    if (isAdded() && getContext() != null) {
                        Toast.makeText(getContext(), "Error removing favorites", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void showAddFavoriteDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_add_to_favorites, null);

        MaterialButton btnCancel = view.findViewById(R.id.btnFavCancel);
        MaterialButton btnConfirm = view.findViewById(R.id.btnFavConfirm);

        AlertDialog dialog =
                new AlertDialog.Builder(getContext())
                        .setView(view)
                        .setCancelable(true)
                        .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnConfirm.setOnClickListener(x -> {
            android.widget.EditText etName = view.findViewById(R.id.etFavName);
            String title = etName != null && etName.getText() != null ? etName.getText().toString().trim() : "";

            if (title.length() < 2) {
                Toast.makeText(getContext(), "Name must be at least 2 characters.", Toast.LENGTH_LONG).show();
                return;
            }

            Long userId = sessionManager != null ? sessionManager.getUserId() : null;
            if (userId == null) {
                Toast.makeText(getContext(), "Not logged in.", Toast.LENGTH_LONG).show();
                return;
            }

            FavoriteRouteCreateRequest freq = new FavoriteRouteCreateRequest();
            freq.setTitle(title);
            freq.setRouteId(rideDetails.getRoute().getId());
            freq.setVehicleType(mapVehicleType(rideDetails.getVehicleType()));
            freq.setPetFriendly(rideDetails.isPetFriendly());
            freq.setBabyFriendly(rideDetails.isBabyFriendly());
            freq.setPassengersEmails(rideDetails.getPassengerEmails());

            favoriteApi.addFavorite(userId, freq).enqueue(new retrofit2.Callback<FavoriteRouteResponse>() {
                @Override
                public void onResponse(retrofit2.Call<FavoriteRouteResponse> call,
                                       retrofit2.Response<FavoriteRouteResponse> resp2) {
                    if (!isAdded() || getContext() == null) return;

                    if (!resp2.isSuccessful()) {
                        Toast.makeText(getContext(), "Add favorite failed (" + resp2.code() + ")", Toast.LENGTH_LONG).show();
                        return;
                    }
                    Toast.makeText(getContext(), "Added to favorites!", Toast.LENGTH_LONG).show();

                    // Dodaj novi ID u listu
                    if (currentMatchingFavoriteIds == null) {
                        currentMatchingFavoriteIds = new ArrayList<>();
                    }
                    currentMatchingFavoriteIds.add(resp2.body().getId());
                    updateFavoriteIcon(true);
                    dialog.dismiss();
                }

                @Override
                public void onFailure(retrofit2.Call<FavoriteRouteResponse> call, Throwable t) {
                    if (isAdded() && getContext() != null) {
                        Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        });

        dialog.show();
    }

    private String mapVehicleType(VehicleType type) {
        if (type.equals(VehicleType.STANDARD)) return "STANDARD";
        if (type.equals(VehicleType.LUXURY)) return "LUXURY";
        if (type.equals(VehicleType.VAN)) return "VAN";
        return "STANDARD";
    }

    private String safeTrim(CharSequence cs) {
        return cs == null ? "" : cs.toString().trim();
    }



    private String formatDateTime(String isoDateTime) {
        if (isoDateTime == null || isoDateTime.isEmpty()) {
            return "N/A";
        }

        try {
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat displayFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
            Date date = isoFormat.parse(isoDateTime);
            return date != null ? displayFormat.format(date) : isoDateTime;
        } catch (ParseException e) {
            return isoDateTime;
        }
    }

    private CharSequence labelValue(String label, String value, int labelColorRes, int valueColorRes) {
        String full = label + value;
        SpannableString ss = new SpannableString(full);

        int labelColor = ContextCompat.getColor(requireContext(), labelColorRes);
        int valueColor = ContextCompat.getColor(requireContext(), valueColorRes);

        ss.setSpan(new ForegroundColorSpan(labelColor), 0, label.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new ForegroundColorSpan(valueColor), label.length(), full.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }

    private CharSequence colorizeLabelsUntilColon(String text, int labelColorRes, int valueColorRes) {
        SpannableString ss = new SpannableString(text);

        int labelColor = ContextCompat.getColor(requireContext(), labelColorRes);
        int valueColor = ContextCompat.getColor(requireContext(), valueColorRes);

        int start = 0;
        while (start < text.length()) {
            int endLine = text.indexOf('\n', start);
            if (endLine == -1) endLine = text.length();

            int colon = text.indexOf(':', start);

            if (colon != -1 && colon < endLine) {
                ss.setSpan(new ForegroundColorSpan(labelColor),
                        start, colon + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                ss.setSpan(new ForegroundColorSpan(valueColor),
                        colon + 1, endLine, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                ss.setSpan(new ForegroundColorSpan(labelColor),
                        start, endLine, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            start = endLine + 1;
        }

        return ss;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (map != null) {
            map.getOverlays().clear();
            map = null;
        }

        pickupMarker = null;
        destMarker = null;
        routeLine = null;

        binding = null;

        binding = null;
    }
}
