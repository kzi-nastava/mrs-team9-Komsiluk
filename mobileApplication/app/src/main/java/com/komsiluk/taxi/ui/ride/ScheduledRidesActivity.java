package com.komsiluk.taxi.ui.ride;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.komsiluk.taxi.R;
import com.komsiluk.taxi.data.remote.ride.RideService;
import com.komsiluk.taxi.data.remote.ride.RideResponse;
import com.komsiluk.taxi.data.session.SessionManager;
import com.komsiluk.taxi.ui.menu.BaseNavDrawerActivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class ScheduledRidesActivity extends BaseNavDrawerActivity {

    @Inject
    RideService rideService;
    @Inject
    SessionManager sessionManager;

    private RecyclerView rvScheduled;
    private ScheduledAdapter adapter;
    private TextView tvEmpty;
    private ArrayList<ScheduledRide> rideList = new ArrayList<>();

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_scheduled_ride;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rvScheduled = findViewById(R.id.rvScheduled);
        tvEmpty = findViewById(R.id.tvScheduledEmpty);

        rvScheduled.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ScheduledAdapter(rideList, ride -> {
            ScheduledDialogs.showScheduledDetails(this, ride, () -> {
                cancelScheduledRide(ride);
            });
        });
        rvScheduled.setAdapter(adapter);

        loadScheduledRides();
    }

    private void loadScheduledRides() {
        Long userId = sessionManager.getUserId();
        if (userId == null) return;

        rideService.getScheduledRidesForUser(userId).enqueue(new Callback<List<RideResponse>>() {
            @Override
            public void onResponse(Call<List<RideResponse>> call, Response<List<RideResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mapDtoToModel(response.body());
                } else {
                    tvEmpty.setVisibility(View.VISIBLE);
                    rvScheduled.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<RideResponse>> call, Throwable t) {
                Toast.makeText(ScheduledRidesActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        }); // OVDE JE FALILA ZAGRADA KOJA JE PRAVILA HAOS
    }

    private void mapDtoToModel(Collection<RideResponse> dtos) {
        rideList.clear();
        for (RideResponse dto : dtos) {
            // Bezbedno izvlačenje stops i emails jer mogu biti null
            ArrayList<String> stops = dto.getStops() != null ? new ArrayList<>(dto.getStops()) : new ArrayList<>();
            ArrayList<String> emails = dto.getPassengerEmails() != null ? new ArrayList<>(dto.getPassengerEmails()) : new ArrayList<>();

            ScheduledRide sr = new ScheduledRide(
                    dto.getId(),
                    "Ride #" + dto.getId(),
                    dto.getStartAddress(),
                    dto.getEndAddress(),
                    stops,
                    emails,
                    dto.getVehicleType(),
                    dto.isPetFriendly(),
                    dto.isBabyFriendly(),
                    String.valueOf(dto.getDistanceKm()),
                    String.valueOf(dto.getEstimatedDurationMin()),
                    dto.getScheduledAt()
            );
            rideList.add(sr);
        }

        if (rideList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvScheduled.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvScheduled.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        }
    }

    private void cancelScheduledRide(ScheduledRide ride) {
        // Ovde ćemo kasnije dodati pravi API poziv za otkazivanje
        Toast.makeText(this, "Cancelling " + ride.getName(), Toast.LENGTH_SHORT).show();
    }
}