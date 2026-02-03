package com.komsiluk.taxi.ui.driver_history;

import android.os.Bundle;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.komsiluk.taxi.R;
import com.komsiluk.taxi.data.remote.driver_history.DriverService;
import com.komsiluk.taxi.data.remote.ride.RideResponseDTO;
import com.komsiluk.taxi.data.session.SessionManager;
import com.komsiluk.taxi.ui.menu.BaseNavDrawerActivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import javax.inject.Inject; // Promenjeno sa jakarta na javax jer Hilt koristi standardni inject
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

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_driver_history;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RecyclerView rv = findViewById(R.id.rvHistory);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new DriverHistoryAdapter(rideList, ride -> {
            // Otvaranje dijaloga sa prosleđenim modelom
            RideDetailsDialogFragment.newInstance(ride)
                    .show(getSupportFragmentManager(), "ride_details");
        });
        rv.setAdapter(adapter);

        findViewById(R.id.btnFilter).setOnClickListener(v ->
                Toast.makeText(this, "Filter clicked", Toast.LENGTH_SHORT).show()
        );

        loadHistory();
    }

    private void loadHistory() {
        Long driverId = sessionManager.getUserId();

        if (driverId == null) {
            Toast.makeText(this, "Sesija nevažeća", Toast.LENGTH_SHORT).show();
            return;
        }

        driverService.getDriverRideHistory(driverId, null, null).enqueue(new Callback<Collection<RideResponseDTO>>() {
            @Override
            public void onResponse(Call<Collection<RideResponseDTO>> call, Response<Collection<RideResponseDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    rideList.clear();
                    for (RideResponseDTO dto : response.body()) {
                        rideList.add(mapDtoToModel(dto));
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(DriverHistoryActivity.this, "Greška servera: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Collection<RideResponseDTO>> call, Throwable t) {
                Toast.makeText(DriverHistoryActivity.this, "Mrežna greška: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private DriverRide mapDtoToModel(RideResponseDTO dto) {
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
        String priceFormatted = dto.getPrice() != null ? dto.getPrice().toString() + " RSD" : "0 RSD";

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