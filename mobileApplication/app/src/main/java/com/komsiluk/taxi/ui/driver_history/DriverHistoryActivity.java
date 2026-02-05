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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import javax.inject.Inject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class DriverHistoryActivity extends BaseNavDrawerActivity {

    @Inject
    DriverService driverService;
    @Inject
    SessionManager sessionManager;

    private DriverHistoryAdapter adapter;
    private List<DriverRide> rideList = new ArrayList<>();

    private String filterFrom = null;
    private String filterTo = null;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_driver_history;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View btnFilter = findViewById(R.id.btnFilter);

        if (btnFilter != null) {
            // Pokaži ga samo na ovom ekranu
            btnFilter.setVisibility(View.VISIBLE);

            // Postavi klik
            btnFilter.setOnClickListener(v -> showDateRangePicker());
        }

        // 1. Inicijalizacija liste (RecyclerView)
        RecyclerView rv = findViewById(R.id.rvHistory);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new DriverHistoryAdapter(rideList, ride -> {
            RideDetailsDialogFragment.newInstance(ride)
                    .show(getSupportFragmentManager(), "ride_details");
        });
        rv.setAdapter(adapter);

        // 2. Povezivanje filter dugmeta iz navbara
        if (btnFilter != null) {
            // Pozivamo već napisanu funkciju showDateRangePicker()
            btnFilter.setOnClickListener(v -> showDateRangePicker());
        }

        // 3. Prvo učitavanje istorije (bez filtera)
        loadHistory();
    }

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
                        .setNegativeButtonText("Resetuj") // Menjamo "Cancel" u "Resetuj"
                        .build();

        picker.show(getSupportFragmentManager(), "DATE_PICKER");

        // KLIK NA "SAČUVAJ" (OK)
        picker.addOnPositiveButtonClickListener(selection -> {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
            filterFrom = sdf.format(new java.util.Date(selection.first));
            filterTo = sdf.format(new java.util.Date(selection.second));
            loadHistory();
        });

        // KLIK NA "RESETUJ" (Umesto Cancel)
        picker.addOnNegativeButtonClickListener(v -> {
            filterFrom = null;
            filterTo = null;
            loadHistory();
            Toast.makeText(this, "Filteri su resetovani", Toast.LENGTH_SHORT).show();
        });
    }
    private DriverRide mapDtoToModel(RideResponse dto) {
        // 1. Formatiranje vremena
        String startTime = dto.getStartTime() != null ? dto.getStartTime() : "";
        String datePart = startTime.contains("T") ? startTime.split("T")[0] : "N/A";
        String startPart = startTime.contains("T") ? startTime.split("T")[1].substring(0, 5) : "--:--";

        String endPart = "--:--";
        if (dto.getEndTime() != null && dto.getEndTime().contains("T")) {
            endPart = dto.getEndTime().split("T")[1].substring(0, 5);
        }

        List<String> emails = (dto.getPassengerEmails() != null) ? dto.getPassengerEmails() : new ArrayList<>();

        // 3. Cena
        String priceFormatted = dto.getPrice() != null ? dto.getPrice().toString() + " $" : "0 $";

        // 4. Kreiranje modela
        // PAŽNJA: Proveri redosled parametara u DriverRide klasi da se ne crveni.
        // Ako se crveni, proveri da li stopsList ide ovde ili se setuje naknadno.
        DriverRide ride = new DriverRide(
                dto.getId(),
                datePart,
                startPart,
                endPart,
                dto.getStartAddress(),
                dto.getStops(),
                dto.getEndAddress(),
                dto.getStatus() != null ? dto.getStatus() : "UNKNOWN",
                emails.size(), // Broj putnika
                dto.getDistanceKm() != null ? dto.getDistanceKm() : 0.0,
                dto.getEstimatedDurationMin() + " min",
                priceFormatted
        );
        ride.passengerEmails = emails;
        ride.isPanicPressed = dto.isPanicTriggered();

        return ride;
    }
}