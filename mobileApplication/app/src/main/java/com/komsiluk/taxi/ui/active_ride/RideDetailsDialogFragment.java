package com.komsiluk.taxi.ui.active_ride;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;

import com.komsiluk.taxi.R;
import com.komsiluk.taxi.data.remote.location.DriverLocationResponse;
import com.komsiluk.taxi.data.remote.location.LocationService;
import com.komsiluk.taxi.data.remote.ride.AdminRideDetails;
import com.komsiluk.taxi.databinding.DialogActiveRideDetailsBinding;
import com.komsiluk.taxi.ui.ride.map.GeoRepository;
import com.komsiluk.taxi.ui.ride.map.NominatimPlace;
import com.komsiluk.taxi.ui.ride.map.OsrmRouteResponse;

import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class RideDetailsDialogFragment extends DialogFragment {

    @Inject LocationService locationService;
    private static GeoRepository staticGeoRepo;
    private static AdminRideDetails staticDetails; // Direktno koristimo bogatiji DTO

    private DialogActiveRideDetailsBinding b;
    private Marker driverMarker;
    private final Handler liveTrackingHandler = new Handler(Looper.getMainLooper());
    private static final int REFRESH_INTERVAL = 3000;

    public static RideDetailsDialogFragment newInstance(AdminRideDetails details, GeoRepository geoRepository) {
        RideDetailsDialogFragment fragment = new RideDetailsDialogFragment();
        staticGeoRepo = geoRepository;
        staticDetails = details;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_DeviceDefault_Dialog_MinWidth);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        b = DialogActiveRideDetailsBinding.inflate(inflater, container, false);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (staticDetails == null) { dismiss(); return; }

        setupUI();
        initMap();
        startLiveTracking();
        b.btnActiveDetailsClose.setOnClickListener(v -> dismiss());
    }

    private void setupUI() {
        // 1. OSNOVNI PODACI (Driver, Pickup, Destination)
        if (staticDetails.getDriver() != null) {
            b.tvActiveDetailsDriverValue.setText(staticDetails.getDriver().getFirstName() + " " + staticDetails.getDriver().getLastName());
        }

        b.tvActiveDetailsPickupValue.setText(staticDetails.getRoute().getStartAddress());
        b.tvActiveDetailsDestinationValue.setText(staticDetails.getRoute().getEndAddress());

        // 2. FORMATIRANJE VREMENA
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        SimpleDateFormat prettyFormat = new SimpleDateFormat("dd.MM. HH:mm", Locale.getDefault());

        try {
            if (staticDetails.getStartTime() != null) {
                Date dStart = isoFormat.parse(staticDetails.getStartTime());
                b.tvActiveStartTime.setText("Start: " + prettyFormat.format(dStart));
            }
            if (staticDetails.getEndTime() == null) {
                b.tvActiveEndTime.setText("End: Pending");
            } else {
                Date dEnd = isoFormat.parse(staticDetails.getEndTime());
                b.tvActiveEndTime.setText("End: " + prettyFormat.format(dEnd));
            }
        } catch (Exception e) { e.printStackTrace(); }

        // 3. DINAMIČKE STANICE (STOPS)
        b.containerActiveStops.removeAllViews();
        List<String> stops = Collections.singletonList(staticDetails.getRoute().getStops());
        if (stops != null && !stops.isEmpty()) {
            for (String stop : stops) {
                addStopToUI(stop);
            }
        }

        // 4. DINAMIČKI PUTNICI (Svi u jednoj listi)
        b.containerPassengers.removeAllViews();

        // Pravimo listu u koju ubacujemo prvo kreatora, pa ostale
        List<String> allEmails = new ArrayList<>();
        if (staticDetails.getCreatorEmail() != null) {
            allEmails.add(staticDetails.getCreatorEmail());
        }
        if (staticDetails.getPassengerEmails() != null) {
            allEmails.addAll(staticDetails.getPassengerEmails());
        }

        if (allEmails.isEmpty()) {
            addSimplePassengerText("No passengers");
        } else {
            for (String email : allEmails) {
                addSimplePassengerText(email);
            }
        }

        // 5. METRIKA
        b.tvActiveKm.setText(String.format(Locale.US, "%.1f km", staticDetails.getDistanceKm()));
        b.tvActiveTime.setText(staticDetails.getEstimatedDurationMin() + " min");
        b.tvActivePrice.setText(staticDetails.getPrice() + " $"); // Dodaj ako DTO ima cenu

        // 6. PANIC STATUS
        boolean isPanic = staticDetails.isPanicTriggered();
        b.tvActivePanicValue.setText(isPanic ? "Yes" : "No");
        b.tvActivePanicValue.setTextColor(isPanic ? Color.RED : ContextCompat.getColor(requireContext(), R.color.secondary));
    }

    /**
     * Samo običan TextView za svakog putnika, bez ikonica i labela.
     */
    private void addSimplePassengerText(String text) {
        TextView tv = new TextView(requireContext());
        tv.setText(text);
        tv.setTextColor(Color.YELLOW);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.fs_sm));
        tv.setPadding(0, convertDpToPx(4), 0, convertDpToPx(4));
        tv.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.roboto_serif_medium));
        b.containerPassengers.addView(tv);
    }

    private void addStopToUI(String stop) {
        ImageView arrow = new ImageView(requireContext());
        arrow.setImageResource(R.drawable.ic_arrow_down);
        arrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        b.containerActiveStops.addView(arrow, new LinearLayout.LayoutParams(convertDpToPx(16), convertDpToPx(16)));

        TextView tv = new TextView(requireContext());
        tv.setText(stop);
        tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.secondary));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.fs_md));
        tv.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.roboto_serif_semibold));
        b.containerActiveStops.addView(tv);
    }

    private void initMap() {
        if (staticGeoRepo == null) return;
        b.mapActiveDetail.setMultiTouchControls(true);

        List<String> addresses = new ArrayList<>();
        addresses.add(staticDetails.getRoute().getStartAddress());
        if (staticDetails.getRoute().getStops() != null) addresses.addAll(Collections.singleton(staticDetails.getRoute().getStops()));
        addresses.add(staticDetails.getRoute().getEndAddress());

        List<GeoPoint> points = new ArrayList<>();
        fetchNextPoint(addresses, 0, points);
    }

    private void fetchNextPoint(List<String> addresses, int index, List<GeoPoint> points) {
        if (index >= addresses.size()) {
            drawMap(points);
            return;
        }
        staticGeoRepo.searchNoviSad(addresses.get(index), "19.75,45.35,19.95,45.20").enqueue(new Callback<List<NominatimPlace>>() {
            @Override
            public void onResponse(Call<List<NominatimPlace>> call, Response<List<NominatimPlace>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    points.add(new GeoPoint(Double.parseDouble(response.body().get(0).lat), Double.parseDouble(response.body().get(0).lon)));
                }
                fetchNextPoint(addresses, index + 1, points);
            }
            @Override public void onFailure(Call<List<NominatimPlace>> call, Throwable t) { fetchNextPoint(addresses, index + 1, points); }
        });
    }

    private void drawMap(List<GeoPoint> points) {
        if (points.isEmpty() || !isAdded()) return;

        for (int i = 0; i < points.size(); i++) {
            addMarker(points.get(i), i == 0 ? R.drawable.home : (i == points.size() - 1 ? R.drawable.flag : R.drawable.station));
        }

        staticGeoRepo.routeMulti(points).enqueue(new Callback<OsrmRouteResponse>() {
            @Override
            public void onResponse(Call<OsrmRouteResponse> call, Response<OsrmRouteResponse> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().routes.isEmpty()) {
                    Polyline line = new Polyline();
                    for (List<Double> c : response.body().routes.get(0).geometry.coordinates) {
                        line.addPoint(new GeoPoint(c.get(1), c.get(0)));
                    }
                    line.getOutlinePaint().setColor(Color.parseColor("#3498db"));
                    line.getOutlinePaint().setStrokeWidth(10f);
                    b.mapActiveDetail.getOverlays().add(line);
                    b.mapActiveDetail.zoomToBoundingBox(BoundingBox.fromGeoPoints(points), true);
                    b.mapActiveDetail.invalidate();
                }
            }
            @Override public void onFailure(Call<OsrmRouteResponse> call, Throwable t) {}
        });
    }

    private void startLiveTracking() {
        if (staticDetails.getDriver() != null) liveTrackingHandler.post(liveTrackingRunnable);
    }

    private final Runnable liveTrackingRunnable = new Runnable() {
        @Override
        public void run() {
            locationService.getSpecificDriverLocation(staticDetails.getDriver().getId()).enqueue(new Callback<DriverLocationResponse>() {
                @Override
                public void onResponse(Call<DriverLocationResponse> call, Response<DriverLocationResponse> response) {
                    if (isAdded() && response.isSuccessful() && response.body() != null) {
                        updateDriverMarker(new GeoPoint(response.body().getLat(), response.body().getLng()));
                    }
                }
                @Override public void onFailure(Call<DriverLocationResponse> call, Throwable t) {}
            });
            liveTrackingHandler.postDelayed(this, REFRESH_INTERVAL);
        }
    };

    private void updateDriverMarker(GeoPoint point) {
        if (driverMarker == null) {
            driverMarker = new Marker(b.mapActiveDetail);
            driverMarker.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.taxi_busy));
            b.mapActiveDetail.getOverlays().add(driverMarker);
        }
        driverMarker.setPosition(point);
        b.mapActiveDetail.invalidate();
    }

    private void addMarker(GeoPoint p, int icon) {
        Marker m = new Marker(b.mapActiveDetail);
        m.setPosition(p);
        m.setIcon(ContextCompat.getDrawable(requireContext(), icon));
        m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        b.mapActiveDetail.getOverlays().add(m);
    }

    private int convertDpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        liveTrackingHandler.removeCallbacks(liveTrackingRunnable);
        b = null;
    }
}