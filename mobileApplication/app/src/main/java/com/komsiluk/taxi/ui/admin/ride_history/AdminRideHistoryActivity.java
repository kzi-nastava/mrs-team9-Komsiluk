package com.komsiluk.taxi.ui.admin.ride_history;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.chip.Chip;
import com.komsiluk.taxi.R;
import com.komsiluk.taxi.data.remote.admin_ride_history.AdminRideSortBy;
import com.komsiluk.taxi.databinding.ActivityAdminRideHistoryBinding;
import com.komsiluk.taxi.ui.menu.BaseNavDrawerActivity;
import com.komsiluk.taxi.ui.passenger.ride_history.FilterDialogFragment;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AdminRideHistoryActivity extends BaseNavDrawerActivity {

    private ActivityAdminRideHistoryBinding binding;
    private AdminRideHistoryViewModel viewModel;
    private AdminRideHistoryAdapter adapter;

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_admin_ride_history;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminRideHistoryBinding.bind(findViewById(R.id.main));

        viewModel = new ViewModelProvider(this).get(AdminRideHistoryViewModel.class);


        setupRecyclerView();
        setupSearchBar();
        setupSortChips();
        setupSortDirectionChips();
        setupFilterButton();
        observeViewModel();
    }

    @Override
    public boolean dispatchTouchEvent(android.view.MotionEvent ev) {
        if (ev.getAction() == android.view.MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof com.google.android.material.textfield.TextInputEditText) {
                android.graphics.Rect outRect = new android.graphics.Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    v.clearFocus();
                    hideKeyboard();
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private void setupRecyclerView() {
        adapter = new AdminRideHistoryAdapter(ride -> {
            viewModel.loadRideDetails(ride.getRideId());
        });

        binding.rvHistory.setLayoutManager(new LinearLayoutManager(this));
        binding.rvHistory.setAdapter(adapter);
    }

    private void setupSearchBar() {
        binding.btnSearch.setOnClickListener(v -> performSearch());

        binding.tilEmail.setEndIconVisible(false);

        binding.etEmail.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.tilEmail.setEndIconVisible(s.length() > 0);

                if (binding.tilEmail.getError() != null) {
                    binding.tilEmail.setError(null);
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        binding.tilEmail.setEndIconOnClickListener(v -> {
            binding.etEmail.setText("");
            binding.tilEmail.setError(null);
            viewModel.clearSearch();
            hideKeyboard();
        });

        binding.etEmail.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });
    }

    private void performSearch() {
        String email = binding.etEmail.getText().toString().trim();
        
        if (email.isEmpty()) {
            binding.tilEmail.setError("Please enter an email");
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.setError("Invalid email format");
            return;
        }

        binding.tilEmail.setError(null);
        hideKeyboard();
        viewModel.searchByEmail(email);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void setupSortChips() {
        binding.chipGroupSort.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            
            int checkedId = checkedIds.get(0);
            AdminRideSortBy sortBy = mapChipIdToSortBy(checkedId);
            viewModel.setSortBy(sortBy);
        });
    }

    private AdminRideSortBy mapChipIdToSortBy(int chipId) {
        if (chipId == R.id.chipStartTime) return AdminRideSortBy.START_TIME;
        if (chipId == R.id.chipEndTime) return AdminRideSortBy.END_TIME;
        if (chipId == R.id.chipRoute) return AdminRideSortBy.ROUTE;
        if (chipId == R.id.chipStartAddr) return AdminRideSortBy.START_ADDRESS;
        if (chipId == R.id.chipEndAddr) return AdminRideSortBy.END_ADDRESS;
        if (chipId == R.id.chipPanic) return AdminRideSortBy.PANIC;
        if (chipId == R.id.chipCanceled) return AdminRideSortBy.CANCELED;
        if (chipId == R.id.chipPrice) return AdminRideSortBy.PRICE;
        return AdminRideSortBy.DATE;
    }

    private void setupSortDirectionChips() {
        binding.chipGroupSortDirection.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int checkedId = checkedIds.get(0);
            boolean desc = (checkedId == R.id.chipDesc);
            viewModel.setSortDirection(desc);
        });
    }

    private void setupFilterButton() {
        binding.btnFilterByDate.setOnClickListener(v -> {
            FilterDialogFragment dialog = FilterDialogFragment.newInstance(new FilterDialogFragment.FilterListener() {
                @Override
                public void onFilterApplied(String fromDate, String toDate) {
                    viewModel.setDateFilter(fromDate, toDate);
                }

                @Override
                public void onFilterCleared() {
                    viewModel.clearDateFilter();
                }
            });

            dialog.show(getSupportFragmentManager(), "filter_dialog");
        });
    }

    private void observeViewModel() {
        viewModel.rides.observe(this, rides -> {
            adapter.setRides(rides);
            updateEmptyState();
        });

        viewModel.rideDetails.observe(this, details -> {
            if (details != null) {
                AdminRideDetailsDialogFragment dialog = AdminRideDetailsDialogFragment.newInstance(details);
                dialog.show(getSupportFragmentManager(), "ride_details");
                viewModel.clearRideDetails();
            }
        });

        viewModel.loading.observe(this, isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.error.observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.searchPerformed.observe(this, performed -> {
            updateEmptyState();
        });
    }

    private void updateEmptyState() {
        Boolean searchPerformed = viewModel.searchPerformed.getValue();
        int rideCount = adapter.getItemCount();

        if (searchPerformed == null || !searchPerformed) {
            binding.tvEmptyState.setVisibility(View.VISIBLE);
            binding.tvEmptyState.setText("Enter user email to view ride history");
            binding.rvHistory.setVisibility(View.GONE);
        } else if (rideCount == 0) {
            binding.tvEmptyState.setVisibility(View.VISIBLE);
            binding.tvEmptyState.setText("No rides found for this user");
            binding.rvHistory.setVisibility(View.GONE);
        } else {
            binding.tvEmptyState.setVisibility(View.GONE);
            binding.rvHistory.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
