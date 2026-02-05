package com.komsiluk.taxi.ui.passenger.ride_history;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.komsiluk.taxi.R;
import com.komsiluk.taxi.data.remote.passenger_ride_history.PassengerRideHistoryDTO;
import com.komsiluk.taxi.data.remote.passenger_ride_history.PassengerRideSortBy;
import com.komsiluk.taxi.data.session.SessionManager;
import com.komsiluk.taxi.databinding.ActivityPassengerRideHistoryBinding;
import com.komsiluk.taxi.ui.menu.BaseNavDrawerActivity;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PassengerRideHistoryActivity extends BaseNavDrawerActivity {

    private ActivityPassengerRideHistoryBinding binding;
    private PassengerRideHistoryViewModel viewModel;
    private PassengerRideHistoryAdapter adapter;
    private ShakeDetector shakeDetector;

    @Inject
    SessionManager sessionManager;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_passenger_ride_history;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPassengerRideHistoryBinding.bind(findViewById(R.id.main));

        viewModel = new ViewModelProvider(this).get(PassengerRideHistoryViewModel.class);

        setupRecyclerView();
        setupSortSpinners();
        setupFilterButton();
        setupShakeDetector();
        observeViewModel();

        loadRides();
    }

    private void setupRecyclerView() {
        adapter = new PassengerRideHistoryAdapter(ride -> {
            viewModel.loadRideDetails(ride.getRideId());
        });

        binding.rvHistory.setLayoutManager(new LinearLayoutManager(this));
        binding.rvHistory.setAdapter(adapter);
    }

    private void setupSortSpinners() {
        String[] sortFields = {
                "Route",
                "Start Time",
                "End Time"
        };

        ArrayAdapter<String> fieldAdapter = new ArrayAdapter<>(
                this,
                R.layout.item_spinner,
                sortFields
        );
        int marginInDp = 12;
        int offsetPx = (int) (marginInDp * getResources().getDisplayMetrics().density);
        fieldAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        binding.spinnerSortField.setAdapter(fieldAdapter);
        binding.spinnerSortField.post(() -> {
            int width = binding.spinnerSortField.getWidth();

            binding.spinnerSortField.setDropDownWidth(width);


            binding.spinnerSortField.setDropDownHorizontalOffset(-offsetPx);
        });

        binding.spinnerSortDirection.post(() -> {
            binding.spinnerSortDirection.setDropDownWidth(binding.spinnerSortDirection.getWidth());
            binding.spinnerSortDirection.setDropDownHorizontalOffset(-offsetPx);
        });
        binding.spinnerSortField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                PassengerRideSortBy sortBy = mapPositionToSortBy(position);
                viewModel.setSortBy(sortBy);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        String[] sortDirections = {"Newest First", "Oldest First"};

        ArrayAdapter<String> directionAdapter = new ArrayAdapter<>(
                this,
                R.layout.item_spinner,
                sortDirections
        );
        directionAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        binding.spinnerSortDirection.setAdapter(directionAdapter);

        binding.spinnerSortDirection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                boolean ascending = position == 1;
                viewModel.setSortDirection(ascending);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    private PassengerRideSortBy mapPositionToSortBy(int position) {
        switch (position) {
            case 0: return PassengerRideSortBy.ROUTE;
            case 1: return PassengerRideSortBy.START_TIME;
            case 2: return PassengerRideSortBy.END_TIME;
            default: return PassengerRideSortBy.DATE;
        }
    }

    private void setupFilterButton() {
        binding.btnFilterByDate.setOnClickListener(v -> {
            FilterDialogFragment dialog = FilterDialogFragment.newInstance(new FilterDialogFragment.FilterListener() {
                @Override
                public void onFilterApplied(String fromDate, String toDate) {
                    Long userId = sessionManager.getUserId();
                    if (userId != null) {
                        viewModel.setDateFilter(fromDate, toDate);
                    }
                }

                @Override
                public void onFilterCleared() {
                    Long userId = sessionManager.getUserId();
                    if (userId != null) {
                        viewModel.clearDateFilter();
                    }
                }
            });

            dialog.show(getSupportFragmentManager(), "filter_dialog");
        });
    }

    private void setupShakeDetector() {
        shakeDetector = new ShakeDetector(() -> {
            viewModel.setSortBy(PassengerRideSortBy.START_TIME);

            viewModel.toggleSortDirection();

            updateSortUI();

        });
    }

    private void updateSortUI() {
        binding.spinnerSortField.setSelection(1, false);

        int direction = viewModel.isSortAscending() ? 1 : 0;
        binding.spinnerSortDirection.setSelection(direction, false);
    }

    private void observeViewModel() {
        viewModel.rides.observe(this, rides -> {
            adapter.setRides(rides);
        });

        viewModel.rideDetails.observe(this, details -> {
            if (details != null) {
                PassengerRideDetailsDialogFragment.newInstance(details)
                        .show(getSupportFragmentManager(), "ride_details");

                viewModel.clearRideDetails();
            }
        });

        viewModel.loading.observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);

            if (Boolean.FALSE.equals(isLoading)) {
                List<PassengerRideHistoryDTO> currentRides = viewModel.rides.getValue();
                if (currentRides == null || currentRides.isEmpty()) {
                    Toast.makeText(this, "No rides found", Toast.LENGTH_SHORT).show();
                }
            }
        });

        viewModel.error.observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadRides() {
        Long userId = sessionManager.getUserId();
        if (userId != null) {
            viewModel.loadRides(userId);
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        shakeDetector.start(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        shakeDetector.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
