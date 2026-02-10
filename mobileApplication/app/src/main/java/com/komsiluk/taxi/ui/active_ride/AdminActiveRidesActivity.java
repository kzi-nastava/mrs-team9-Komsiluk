package com.komsiluk.taxi.ui.active_ride;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.komsiluk.taxi.R;
import com.komsiluk.taxi.data.remote.driver_history.DriverBasic;
import com.komsiluk.taxi.data.remote.driver_history.DriverService;
import com.komsiluk.taxi.data.remote.ride.AdminRideDetails;
import com.komsiluk.taxi.data.remote.ride.RideResponse;
import com.komsiluk.taxi.data.remote.ride.RideService;
import com.komsiluk.taxi.ui.menu.BaseNavDrawerActivity;
import com.komsiluk.taxi.ui.ride.map.GeoRepository;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@AndroidEntryPoint
public class AdminActiveRidesActivity extends BaseNavDrawerActivity {

    @Inject RideService rideService;
    @Inject DriverService driverService;
    @Inject OkHttpClient okHttpClient;

    private ActiveRidesAdapter adapter;
    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private EditText etSearch;
    private final HashMap<Long, String> driverMap = new HashMap<>();

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_admin_active_rides;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recyclerView = findViewById(R.id.rvActiveRides);
        tvEmpty = findViewById(R.id.tvActiveRidesEmpty);
        etSearch = findViewById(R.id.etDriverSearch); // ID iz tvog novog layouta

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        setupSearchListener();
        loadData();
    }

    private void setupSearchListener() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null) {
                    adapter.filter(s.toString(), driverMap);
                    updateEmptyState();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadData() {
        driverService.getDriversBasic().enqueue(new Callback<List<DriverBasic>>() {
            @Override
            public void onResponse(Call<List<DriverBasic>> call, Response<List<DriverBasic>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    driverMap.clear();
                    for (DriverBasic d : response.body()) {
                        driverMap.put(d.getId(), d.getFirstName() + " " + d.getLastName());
                    }
                }
                loadActiveRides();
            }
            @Override public void onFailure(Call<List<DriverBasic>> call, Throwable t) { loadActiveRides(); }
        });
    }

    private void loadActiveRides() {
        rideService.getAllActiveRides().enqueue(new Callback<List<RideResponse>>() {
            @Override
            public void onResponse(Call<List<RideResponse>> call, Response<List<RideResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter = new ActiveRidesAdapter(response.body(), driverMap, ride -> fetchAndOpenDetails(ride.getId()));
                    recyclerView.setAdapter(adapter);
                    updateEmptyState();
                }
            }
            @Override public void onFailure(Call<List<RideResponse>> call, Throwable t) {
                Toast.makeText(AdminActiveRidesActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateEmptyState() {
        if (adapter == null || adapter.getItemCount() == 0) {
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.GONE);
        }
    }

    private void fetchAndOpenDetails(Long rideId) {
        rideService.getRideDetails(rideId).enqueue(new Callback<AdminRideDetails>() {
            @Override
            public void onResponse(Call<AdminRideDetails> call, Response<AdminRideDetails> response) {
                if (response.isSuccessful() && response.body() != null) {
                    openRideDetailsDialog(response.body());
                } else {
                    Toast.makeText(AdminActiveRidesActivity.this, "Could not fetch details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AdminRideDetails> call, Throwable t) {
                Toast.makeText(AdminActiveRidesActivity.this, "API Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openRideDetailsDialog(AdminRideDetails details) {
        GeoRepository geoRepo = new GeoRepository(okHttpClient);
        RideDetailsDialogFragment dialog = RideDetailsDialogFragment.newInstance(details, geoRepo);
        dialog.show(getSupportFragmentManager(), "ride_details");
    }
}