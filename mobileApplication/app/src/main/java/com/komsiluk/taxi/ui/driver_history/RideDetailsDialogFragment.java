package com.komsiluk.taxi.ui.driver_history;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.komsiluk.taxi.R;
import com.komsiluk.taxi.data.remote.driver_history.DriverService;
import com.komsiluk.taxi.data.remote.inconsistency_report.InconsistencyReportResponse;
import com.komsiluk.taxi.data.remote.inconsistency_report.InconsistencyService;
import com.komsiluk.taxi.data.remote.rating.RatingResponse;
import com.komsiluk.taxi.data.remote.rating.RatingService;
import com.komsiluk.taxi.databinding.DialogRideDetailsBinding;
import com.komsiluk.taxi.ui.ride.map.GeoRepository;
import com.komsiluk.taxi.ui.ride.map.NominatimPlace;
import com.komsiluk.taxi.ui.ride.map.OsrmRouteResponse;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class RideDetailsDialogFragment extends DialogFragment {

    private DialogRideDetailsBinding b;

    private static GeoRepository staticGeoRepo;

    @Inject DriverService driverService;
    @Inject RatingService ratingService;
    @Inject InconsistencyService inconsistencyService;

    public static RideDetailsDialogFragment newInstance(DriverRide ride, GeoRepository geoRepository) {
        RideDetailsDialogFragment f = new RideDetailsDialogFragment();
        staticGeoRepo = geoRepository;
        Bundle args = new Bundle();
        args.putLong("id", ride.id);
        args.putString("date", ride.date);
        args.putString("start", ride.startTime);
        args.putString("end", ride.endTime);
        args.putString("pickup", ride.pickup);
        args.putStringArrayList("stops", ride.stops != null ? new ArrayList<>(ride.stops) : new ArrayList<>());
        args.putStringArrayList("passengerEmails", ride.passengerEmails != null ? new ArrayList<>(ride.passengerEmails) : new ArrayList<>());
        args.putString("dest", ride.destination);
        args.putDouble("km", ride.kilometers);
        args.putString("duration", ride.duration);
        args.putString("price", ride.price);
        args.putBoolean("panic", ride.isPanicPressed);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            int w = (int) (requireContext().getResources().getDisplayMetrics().widthPixels * 0.95f);
            int h = (int) (requireContext().getResources().getDisplayMetrics().heightPixels * 0.90f);
            getDialog().getWindow().setLayout(w, h);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        b = DialogRideDetailsBinding.inflate(inflater, container, false);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args == null) return;

        long rideId = args.getLong("id");

        // --- INICIJALIZACIJA MAPE ---
        b.mapDetails.setTileSource(TileSourceFactory.MAPNIK);
        b.mapDetails.setMultiTouchControls(true);

        // --- POPUNJAVANJE PODATAKA (Tvoj originalni kod) ---
        List<String> emails = args.getStringArrayList("passengerEmails");
        b.tvPassengers.setText(emails != null && !emails.isEmpty() ? String.join("\n", emails) : "No passengers");

        b.containerStops.removeAllViews();
        List<String> stops = args.getStringArrayList("stops");
        if (stops != null) {
            for (int i = 0; i < stops.size(); i++) {
                TextView tv = new TextView(requireContext());
                tv.setText(labelValue("Station " + (i + 1) + ": ", stops.get(i), android.R.color.white, R.color.secondary));
                tv.setPadding(0, 8, 0, 8);
                b.containerStops.addView(tv);
            }
        }

        b.tvPickup.setText(labelValue("Pickup location: ", args.getString("pickup"), android.R.color.white, R.color.secondary));
        b.tvDestination.setText(labelValue("Destination: ", args.getString("dest"), android.R.color.white, R.color.secondary));
        b.tvStart.setText(labelValue("Start time: ", args.getString("start"), android.R.color.white, R.color.secondary));
        b.tvEnd.setText(labelValue("End time: ", args.getString("end"), android.R.color.white, R.color.secondary));
        b.tvKm.setText(labelValue("Kilometers: ", String.valueOf(args.getDouble("km")), android.R.color.white, R.color.secondary));
        b.tvDuration.setText(labelValue("Time: ", args.getString("duration"), android.R.color.white, R.color.secondary));
        b.tvPrice.setText(labelValue("Price: ", args.getString("price"), android.R.color.white, R.color.secondary));
        b.tvPanicFlag.setText(labelValue("Panic button pressed: ", args.getBoolean("panic") ? "True" : "False", android.R.color.white, R.color.secondary));

        // --- LOGIKA ZA RUTU ---
        prepareAndDrawRoute(args.getString("pickup"), stops, args.getString("dest"));

        loadRatings(rideId, android.R.color.white, R.color.secondary);
        loadInconsistencies(rideId, android.R.color.white, R.color.secondary);
        b.btnClose.setOnClickListener(v -> dismiss());
    }

    private void prepareAndDrawRoute(String start, List<String> stops, String end) {
        List<String> allAddresses = new ArrayList<>();
        allAddresses.add(start);
        if (stops != null) allAddresses.addAll(stops);
        allAddresses.add(end);

        List<GeoPoint> routePoints = new ArrayList<>();
        fetchPointsSequential(allAddresses, 0, routePoints);
    }

    private void fetchPointsSequential(List<String> addresses, int index, List<GeoPoint> results) {
        if (index >= addresses.size()) {
            drawMapData(results);
            return;
        }
        staticGeoRepo.searchNoviSad(addresses.get(index), "19.75,45.35,19.95,45.20").enqueue(new Callback<List<NominatimPlace>>() {
            @Override
            public void onResponse(Call<List<NominatimPlace>> call, Response<List<NominatimPlace>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    NominatimPlace p = response.body().get(0);
                    results.add(new GeoPoint(Double.parseDouble(p.lat), Double.parseDouble(p.lon)));
                }
                fetchPointsSequential(addresses, index + 1, results);
            }
            @Override
            public void onFailure(Call<List<NominatimPlace>> call, Throwable t) {
                fetchPointsSequential(addresses, index + 1, results);
            }
        });
    }

    private void drawMapData(List<GeoPoint> points) {
        if (points.size() < 2 || !isAdded()) return;

        for (int i = 0; i < points.size(); i++) {
            Marker m = new Marker(b.mapDetails);
            m.setPosition(points.get(i));
            m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            if (i == 0) m.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.home));
            else if (i == points.size() - 1) m.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.flag));
            else m.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.station));
            b.mapDetails.getOverlays().add(m);
        }

        staticGeoRepo.routeMulti(points).enqueue(new Callback<OsrmRouteResponse>() {
            @Override
            public void onResponse(Call<OsrmRouteResponse> call, Response<OsrmRouteResponse> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().routes.isEmpty()) {
                    List<List<Double>> coords = response.body().routes.get(0).geometry.coordinates;
                    Polyline line = new Polyline();
                    for (List<Double> c : coords) line.addPoint(new GeoPoint(c.get(1), c.get(0)));
                    line.getOutlinePaint().setColor(Color.BLUE);
                    line.getOutlinePaint().setStrokeWidth(10f);
                    b.mapDetails.getOverlays().add(line);
                    b.mapDetails.zoomToBoundingBox(BoundingBox.fromGeoPoints(points), true);
                    b.mapDetails.invalidate();
                }
            }
            @Override public void onFailure(Call<OsrmRouteResponse> call, Throwable t) {}
        });
    }

    // --- Tvoje postojeće metode za loadRatings, loadInconsistencies, labelValue, colorize ---
    // (Zadrži ih točno onakvima kakve si ih poslao)

    private void loadRatings(long rideId, int labelWhite, int valueYellow) {
        ratingService.getRideRatings(rideId).enqueue(new Callback<List<RatingResponse>>() {
            @Override
            public void onResponse(Call<List<RatingResponse>> call, Response<List<RatingResponse>> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null) {
                    StringBuilder sb = new StringBuilder();
                    for (RatingResponse r : response.body()) {
                        sb.append(r.getRaterMail()).append("\n")
                                .append("Driver rating: ").append(r.getDriverGrade()).append(" stars\n")
                                .append("Vehicle rating: ").append(r.getVehicleGrade()).append(" stars\n")
                                .append("Comment: ").append(r.getComment() != null ? r.getComment() : "N/A").append("\n\n");
                    }
                    if (response.body().isEmpty()) b.tvRatings.setText("No ratings yet.");
                    else b.tvRatings.setText(colorizeLabelsUntilColon(sb.toString().trim(), labelWhite, valueYellow));
                }
            }
            @Override public void onFailure(Call<List<RatingResponse>> call, Throwable t) {
                if (isAdded()) b.tvRatings.setText("Failed to load ratings.");
            }
        });
    }

    private void loadInconsistencies(long rideId, int labelWhite, int valueYellow) {
        inconsistencyService.getRideInconsistencies(rideId).enqueue(new Callback<List<InconsistencyReportResponse>>() {
            @Override
            public void onResponse(Call<List<InconsistencyReportResponse>> call, Response<List<InconsistencyReportResponse>> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null) {
                    if (response.body().isEmpty()) {
                        b.tvInconsistencyDetails.setText("No inconsistencies reported.");
                    } else {
                        StringBuilder sb = new StringBuilder();
                        for (InconsistencyReportResponse report : response.body()) {
                            sb.append("Reporter: ").append(report.getReporterEmail()).append("\n")
                                    .append("Reason: ").append(report.getMessage()).append("\n\n");
                        }
                        b.tvInconsistencyDetails.setText(colorizeLabelsUntilColon(sb.toString().trim(), labelWhite, valueYellow));
                    }
                }
            }
            @Override public void onFailure(Call<List<InconsistencyReportResponse>> call, Throwable t) {
                if (isAdded()) b.tvInconsistencyDetails.setText("Failed to load reports.");
            }
        });
    }

    private CharSequence labelValue(String label, String value, int labelColorRes, int valueColorRes) {
        String full = label + (value != null ? value : "N/A");
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
                ss.setSpan(new ForegroundColorSpan(labelColor), start, colon + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ss.setSpan(new ForegroundColorSpan(valueColor), colon + 1, endLine, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                ss.setSpan(new ForegroundColorSpan(labelColor), start, endLine, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            start = endLine + 1;
        }
        return ss;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        b = null;
    }
}