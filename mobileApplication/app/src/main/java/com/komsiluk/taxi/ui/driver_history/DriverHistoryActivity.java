package com.komsiluk.taxi.ui.driver_history;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.komsiluk.taxi.R;
import com.komsiluk.taxi.data.remote.driver_history.DriverService;
import com.komsiluk.taxi.data.remote.ride.RideResponse;
import com.komsiluk.taxi.data.session.SessionManager;
import com.komsiluk.taxi.ui.menu.BaseNavDrawerActivity;
import com.komsiluk.taxi.ui.ride.map.GeoRepository; // DODATO

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import javax.inject.Inject;
import okhttp3.OkHttpClient; // DODATO
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class DriverHistoryActivity extends BaseNavDrawerActivity {

    @Inject
    DriverService driverService;
    @Inject
    SessionManager sessionManager;

    @Inject
    OkHttpClient okHttpClient; // DODATO za ručnu inicijalizaciju mape

    private DriverHistoryAdapter adapter;
    private List<DriverRide> rideList = new ArrayList<>();
    private GeoRepository geoRepository; // DODATO

    private String filterFrom = null;
    private String filterTo = null;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_driver_history;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 0. Inicijalizacija mape (GeoRepository)
        geoRepository = new GeoRepository(okHttpClient); // Inicijalizovano ovde

        View btnFilter = findViewById(R.id.btnFilter);

        if (btnFilter != null) {
            btnFilter.setVisibility(View.VISIBLE);
            btnFilter.setOnClickListener(v -> showDateRangePicker());
        }

        // 1. Inicijalizacija liste (RecyclerView)
        RecyclerView rv = findViewById(R.id.rvHistory);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new DriverHistoryAdapter(rideList, ride -> {
            // PROSLEĐUJEMO geoRepository u newInstance metodu
            RideDetailsDialogFragment.newInstance(ride, geoRepository)
                    .show(getSupportFragmentManager(), "ride_details");
        });
        rv.setAdapter(adapter);

        loadHistory();
    }

    // ... ostatak koda (loadHistory, showDateRangePicker, mapDtoToModel) ostaje nepromenjen ...

    private void loadHistory() {
        Long driverId = sessionManager.getUserId();
        if (driverId == null) return;

        driverService.getDriverRideHistory(driverId, filterFrom, filterTo)
                .enqueue(new Callback<Collection<RideResponse>>() {
                    @Override
                    public void onResponse(Call<Collection<RideResponse>> call, Response<Collection<RideResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            rideList.clear();
                            for (RideResponse dto : response.body()) {
                                rideList.add(mapDtoToModel(dto));
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<Collection<RideResponse>> call, Throwable t) {
                        Toast.makeText(DriverHistoryActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showDateRangePicker() {
        com.google.android.material.datepicker.MaterialDatePicker<androidx.core.util.Pair<Long, Long>> picker =
                com.google.android.material.datepicker.MaterialDatePicker.Builder.dateRangePicker()
                        .setTitleText("Izaberi period")
                        .setNegativeButtonText("Resetuj")
                        .build();

        picker.show(getSupportFragmentManager(), "DATE_PICKER");

        picker.addOnPositiveButtonClickListener(selection -> {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
            filterFrom = sdf.format(new java.util.Date(selection.first));
            filterTo = sdf.format(new java.util.Date(selection.second));
            loadHistory();
        });

        picker.addOnNegativeButtonClickListener(v -> {
            filterFrom = null;
            filterTo = null;
            loadHistory();
            Toast.makeText(this, "Filteri su resetovani", Toast.LENGTH_SHORT).show();
        });
    }

    private DriverRide mapDtoToModel(RideResponse dto) {
        String startTime = dto.getStartTime() != null ? dto.getStartTime() : "";
        String datePart = startTime.contains("T") ? startTime.split("T")[0] : "N/A";
        String startPart = startTime.contains("T") ? startTime.split("T")[1].substring(0, 5) : "--:--";

        String endPart = "--:--";
        if (dto.getEndTime() != null && dto.getEndTime().contains("T")) {
            endPart = dto.getEndTime().split("T")[1].substring(0, 5);
        }

        List<String> emails = (dto.getPassengerEmails() != null) ? dto.getPassengerEmails() : new ArrayList<>();
        String priceFormatted = dto.getPrice() != null ? dto.getPrice().toString() + " $" : "0 $";

        DriverRide ride = new DriverRide(
                dto.getId(),
                datePart,
                startPart,
                endPart,
                dto.getStartAddress(),
                dto.getStops(),
                dto.getEndAddress(),
                dto.getStatus() != null ? dto.getStatus() : "UNKNOWN",
                emails.size(),
                dto.getDistanceKm() != null ? dto.getDistanceKm() : 0.0,
                dto.getEstimatedDurationMin() + " min",
                priceFormatted
        );
        ride.passengerEmails = emails;
        ride.isPanicPressed = dto.isPanicTriggered();

        return ride;
    }
}