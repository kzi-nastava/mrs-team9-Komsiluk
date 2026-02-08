package com.komsiluk.taxi.ui.admin.ride_history;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.komsiluk.taxi.data.remote.admin_ride_history.AdminRideHistoryDTO;
import com.komsiluk.taxi.data.remote.admin_ride_history.AdminRideHistoryService;
import com.komsiluk.taxi.data.remote.admin_ride_history.AdminRideSortBy;
import com.komsiluk.taxi.data.remote.passenger_ride_history.PassengerRideDetailsDTO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class AdminRideHistoryViewModel extends ViewModel {


    private final AdminRideHistoryService service;

    private final MutableLiveData<List<AdminRideHistoryDTO>> _rides = new MutableLiveData<>();
    public final LiveData<List<AdminRideHistoryDTO>> rides = _rides;

    private final MutableLiveData<PassengerRideDetailsDTO> _rideDetails = new MutableLiveData<>();
    public final LiveData<PassengerRideDetailsDTO> rideDetails = _rideDetails;

    private final MutableLiveData<Boolean> _loading = new MutableLiveData<>();
    public final LiveData<Boolean> loading = _loading;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public final LiveData<String> error = _error;

    private final MutableLiveData<Boolean> _searchPerformed = new MutableLiveData<>(false);
    public final LiveData<Boolean> searchPerformed = _searchPerformed;

    private String currentFromDate = null;
    private String currentToDate = null;
    private AdminRideSortBy currentSortBy = AdminRideSortBy.START_ADDRESS;
    private boolean sortAscending = false;
    private String currentEmail = null;

    private List<AdminRideHistoryDTO> cachedRides = new ArrayList<>();

    @Inject
    public AdminRideHistoryViewModel(AdminRideHistoryService service) {
        this.service = service;
    }

    public void searchByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            _error.setValue("Please enter an email address");
            return;
        }

        currentEmail = email.trim();
        _loading.setValue(true);
        _error.setValue(null);
        _searchPerformed.setValue(true);

        service.getRidesByUserEmail(currentEmail, null, null, currentSortBy)
                .enqueue(new Callback<List<AdminRideHistoryDTO>>() {
                    @Override
                    public void onResponse(Call<List<AdminRideHistoryDTO>> call, 
                                           Response<List<AdminRideHistoryDTO>> response) {
                        _loading.setValue(false);

                        if (response.isSuccessful() && response.body() != null) {
                            cachedRides = response.body();
                            applyLocalFilterAndSort();
                        } else {
                            cachedRides = new ArrayList<>();
                            _rides.setValue(new ArrayList<>());
                            if (response.code() == 404) {
                                _error.setValue("User not found");
                            } else {
                                _error.setValue("Failed to load rides: " + response.code());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<AdminRideHistoryDTO>> call, Throwable t) {
                        _loading.setValue(false);
                        _error.setValue("Network error: " + t.getMessage());
                    }
                });
    }

    public void clearRideDetails() {
        _rideDetails.setValue(null);
    }

    private void applyLocalFilterAndSort() {
        if (cachedRides == null) {
            return;
        }

        List<AdminRideHistoryDTO> sortedList = new ArrayList<>();
        for (AdminRideHistoryDTO ride : cachedRides) {
            if (isRideInRange(ride)) {
                sortedList.add(ride);
            }
        }


        if (!sortedList.isEmpty()) {
            Collections.sort(sortedList, (r1, r2) -> {
                int result;
                switch (currentSortBy) {
                    case ROUTE:
                        String fullRoute1 = (safeString(r1.getStartAddress()) + safeString(r1.getRoute()) + safeString(r1.getEndAddress())).toLowerCase();
                        String fullRoute2 = (safeString(r2.getStartAddress()) + safeString(r2.getRoute()) + safeString(r2.getEndAddress())).toLowerCase();
                        result = fullRoute1.compareTo(fullRoute2);
                        break;
                    case START_ADDRESS:
                        result = compareStringsIgnoreCaseSafe(r1.getStartAddress(), r2.getStartAddress());
                        break;
                    case END_ADDRESS:
                        result = compareStringsIgnoreCaseSafe(r2.getEndAddress(), r2.getEndAddress());
                        break;
                    case DATE:
                    case START_TIME:
                        result = compareStringsSafe(r1.getStartTime(), r2.getStartTime());
                        break;
                    case END_TIME:
                        result = compareStringsSafe(r1.getEndTime(), r2.getEndTime());
                        break;
                    case PRICE:
                        BigDecimal p1 = r1.getPrice() != null ? r1.getPrice() : BigDecimal.ZERO;
                        BigDecimal p2 = r2.getPrice() != null ? r2.getPrice() : BigDecimal.ZERO;
                        result = p1.compareTo(p2);
                        break;
                    case PANIC:
                        result = Boolean.compare(r1.isPanicTriggered(), r2.isPanicTriggered());
                        break;
                    case CANCELED:
                        result = Boolean.compare(r1.isCanceled(), r2.isCanceled());
                        break;
                    default:
                        result = 0;
                }
                return sortAscending ? result : -result;
            });
        }

        _rides.setValue(sortedList);
    }

    private String safeString(String s) {
        return s != null ? s : "";
    }

    private int compareStringsSafe(String s1, String s2) {
        if (s1 == s2) return 0;
        if (s1 == null) return 1;
        if (s2 == null) return -1;
        return s1.compareTo(s2);
    }

    private int compareStringsIgnoreCaseSafe(String s1, String s2) {
        if (s1 == s2) return 0;
        if (s1 == null) return 1;
        if (s2 == null) return -1;
        return s1.compareToIgnoreCase(s2);
    }

    public void clearSearch() {
        _rides.setValue(new ArrayList<>());
        _searchPerformed.setValue(false);

        currentEmail = null;
        currentFromDate = null;
        currentToDate = null;
        cachedRides = new ArrayList<>();
        _error.setValue(null);
    }

    private boolean isRideInRange(AdminRideHistoryDTO ride) {
        if (currentFromDate == null && currentToDate == null) return true;

        String startTime = ride.getStartTime();
        if (startTime == null || startTime.trim().length() < 10) return false;

        String rideDate = startTime.substring(0, 10);

        boolean afterFrom = (currentFromDate == null) || (rideDate.compareTo(currentFromDate) >= 0);
        boolean beforeTo = (currentToDate == null) || (rideDate.compareTo(currentToDate) <= 0);

        return afterFrom && beforeTo;
    }

    public void loadRideDetails(Long rideId) {
        _loading.setValue(true);
        _error.setValue(null);

        service.getRideDetails(rideId)
                .enqueue(new Callback<PassengerRideDetailsDTO>() {
                    @Override
                    public void onResponse(Call<PassengerRideDetailsDTO> call,
                                           Response<PassengerRideDetailsDTO> response) {
                        _loading.setValue(false);

                        if (response.isSuccessful() && response.body() != null) {
                            _rideDetails.setValue(response.body());
                        } else {
                            _error.setValue("Failed to load ride details: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<PassengerRideDetailsDTO> call, Throwable t) {
                        _loading.setValue(false);
                        _error.setValue("Network error: " + t.getMessage());
                    }
                });
    }

    public void setDateFilter(String fromDate, String toDate) {
        this.currentFromDate = fromDate;
        this.currentToDate = toDate;
        applyLocalFilterAndSort();
    }

    public void clearDateFilter() {
        this.currentFromDate = null;
        this.currentToDate = null;
        applyLocalFilterAndSort();
    }

    public void setSortBy(AdminRideSortBy sortBy) {
        this.currentSortBy = sortBy;
        applyLocalFilterAndSort();
    }

    public void setSortDirection(boolean ascending) {
        this.sortAscending = ascending;
        applyLocalFilterAndSort();
    }

    public boolean isSortAscending() {
        return sortAscending;
    }

    public String getCurrentEmail() {
        return currentEmail;
    }
}
