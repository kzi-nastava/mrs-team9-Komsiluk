package com.komsiluk.taxi.ui.passenger.ride_history;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.gson.Gson;
import com.komsiluk.taxi.R;
import com.komsiluk.taxi.UserActivity;
import com.komsiluk.taxi.data.remote.passenger_ride_history.DriverResponseDTO;
import com.komsiluk.taxi.data.remote.passenger_ride_history.InconsistencyReportResponseDTO;
import com.komsiluk.taxi.data.remote.passenger_ride_history.PassengerRideDetailsDTO;
import com.komsiluk.taxi.data.remote.passenger_ride_history.RatingResponseDTO;
import com.komsiluk.taxi.data.remote.passenger_ride_history.RouteResponseDTO;
import com.komsiluk.taxi.databinding.DialogPassengerRideDetailsBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PassengerRideDetailsDialogFragment extends DialogFragment {

    private DialogPassengerRideDetailsBinding binding;
    private PassengerRideDetailsDTO rideDetails;

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

        populateUI();
        setupButtons();
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
        binding = null;
    }
}
