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

    @Inject
    DriverService driverService;

    @Inject
    RatingService ratingService;

    @Inject
    InconsistencyService inconsistencyService;

    public static RideDetailsDialogFragment newInstance(DriverRide ride) {
        RideDetailsDialogFragment f = new RideDetailsDialogFragment();
        Bundle args = new Bundle();

        args.putLong("id", ride.id);
        args.putString("date", ride.date);
        args.putString("start", ride.startTime);
        args.putString("end", ride.endTime);
        args.putString("pickup", ride.pickup);
        args.putStringArrayList("stops", (ArrayList<String>) ride.stops);
        args.putStringArrayList("passengerEmails", ride.passengerEmails != null ? new ArrayList<>(ride.passengerEmails) : new ArrayList<>());
        args.putString("dest", ride.destination);
        args.putString("status", ride.status);
        args.putInt("passengers", ride.passengers);
        args.putDouble("km", ride.kilometers);
        args.putString("duration", ride.duration);
        args.putString("price", ride.price);

        f.setArguments(args);
        return f;
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        b = DialogRideDetailsBinding.inflate(inflater, container, false);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args == null) return;

        int labelWhite = ContextCompat.getColor(requireContext(), android.R.color.white);
        int valueYellow = ContextCompat.getColor(requireContext(), R.color.secondary);
        long rideId = args.getLong("id");

        // Unutar onViewCreated u Dijalogu
        List<String> emails = args.getStringArrayList("passengerEmails");
        if (emails != null && !emails.isEmpty()) {
            b.tvPassengers.setText(String.join("\n", emails));
        } else {
            b.tvPassengers.setText("No passengers");
        }

        // 2. STANICE - Dinamičko dodavanje
        b.containerStops.removeAllViews();
        List<String> stops = args.getStringArrayList("stops");
        if (stops != null && !stops.isEmpty()) {
            for (int i = 0; i < stops.size(); i++) {
                TextView tv = new TextView(requireContext());
                // Ključno: Eksplicitno postavljamo boju i parametre da budu vidljivi
                tv.setText(labelValue("Station " + (i + 1) + ": ", stops.get(i), android.R.color.white, R.color.secondary));
                tv.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                tv.setPadding(0, 8, 0, 8);
                b.containerStops.addView(tv);
            }
        }

        // 3. OSTALI PODACI
        b.tvPickup.setText(labelValue("Pickup location: ", args.getString("pickup"), android.R.color.white, R.color.secondary));
        b.tvDestination.setText(labelValue("Destination: ", args.getString("dest"), android.R.color.white, R.color.secondary));
        b.tvStart.setText(labelValue("Start time: ", args.getString("start"), android.R.color.white, R.color.secondary));
        b.tvEnd.setText(labelValue("End time: ", args.getString("end"), android.R.color.white, R.color.secondary));
        b.tvKm.setText(labelValue("Kilometers: ", String.valueOf(args.getDouble("km")), android.R.color.white, R.color.secondary));
        b.tvDuration.setText(labelValue("Time: ", args.getString("duration"), android.R.color.white, R.color.secondary));
        b.tvPrice.setText(labelValue("Price: ", args.getString("price"), android.R.color.white, R.color.secondary));

        // Panic flag bez broja putnika
        b.tvPanicFlag.setText(labelValue("Panic button pressed: ", "False", android.R.color.white, R.color.secondary));

        loadRatings(rideId, android.R.color.white, R.color.secondary);
        loadInconsistencies(rideId, android.R.color.white, R.color.secondary);

        b.btnClose.setOnClickListener(v -> dismiss());
    }
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

                    if (response.body().isEmpty()) {
                        b.tvRatings.setText("No ratings yet.");
                    } else {
                        b.tvRatings.setText(colorizeLabelsUntilColon(sb.toString().trim(), labelWhite, valueYellow));
                    }
                }
            }

            @Override
            public void onFailure(Call<List<RatingResponse>> call, Throwable t) {
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
                        // Popunjavamo veliku sekciju sa detaljima
                        b.tvInconsistencyDetails.setText(colorizeLabelsUntilColon(sb.toString().trim(), labelWhite, valueYellow));
                    }
                }
            }

            @Override
            public void onFailure(Call<List<InconsistencyReportResponse>> call, Throwable t) {
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