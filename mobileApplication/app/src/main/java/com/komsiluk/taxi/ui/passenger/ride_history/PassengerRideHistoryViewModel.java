package com.komsiluk.taxi.ui.passenger.ride_history;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.komsiluk.taxi.data.remote.passenger_ride_history.PassengerRideDetailsDTO;
import com.komsiluk.taxi.data.remote.passenger_ride_history.PassengerRideHistoryDTO;
import com.komsiluk.taxi.data.remote.passenger_ride_history.PassengerRideHistoryService;
import com.komsiluk.taxi.data.remote.passenger_ride_history.PassengerRideSortBy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class PassengerRideHistoryViewModel extends ViewModel {

    private static final String TAG = "PassengerRideVM";

    private final PassengerRideHistoryService service;

    private final MutableLiveData<List<PassengerRideHistoryDTO>> _rides = new MutableLiveData<>();
    public final LiveData<List<PassengerRideHistoryDTO>> rides = _rides;

    private final MutableLiveData<PassengerRideDetailsDTO> _rideDetails = new MutableLiveData<>();
    public final LiveData<PassengerRideDetailsDTO> rideDetails = _rideDetails;

    private final MutableLiveData<Boolean> _loading = new MutableLiveData<>();
    public final LiveData<Boolean> loading = _loading;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public final LiveData<String> error = _error;

    private String currentFromDate = null;
    private String currentToDate = null;
    private PassengerRideSortBy currentSortBy = PassengerRideSortBy.DATE;
    private boolean sortAscending = false;

    @Inject
    public PassengerRideHistoryViewModel(PassengerRideHistoryService service) {
        this.service = service;
    }


    private List<PassengerRideHistoryDTO> cachedRides = new ArrayList<>(); // The master list

    public void loadRides(Long userId) {
        _loading.setValue(true);
        service.getPassengerRides(userId, null, null, currentSortBy)
                .enqueue(new Callback<List<PassengerRideHistoryDTO>>() {
                    @Override
                    public void onResponse(Call<List<PassengerRideHistoryDTO>> call, Response<List<PassengerRideHistoryDTO>> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            cachedRides = response.body();
                            applyLocalFilterAndSort();
                        } else {
                            cachedRides = new ArrayList<>();
                            _rides.setValue(new ArrayList<>());
                        }
                        _loading.setValue(false);
                    }
                    @Override
                    public void onFailure(Call<List<PassengerRideHistoryDTO>> call, Throwable t) {
                        _loading.setValue(false);
                        _error.setValue("Failed to fetch history");
                    }
                });
    }

    public void clearRideDetails() {
        _rideDetails.setValue(null);
    }

    private void applyLocalFilterAndSort() {
        if (cachedRides == null) return;

        List<PassengerRideHistoryDTO> sortedList = new ArrayList<>();

        for (PassengerRideHistoryDTO ride : cachedRides) {
            if (isRideInRange(ride)) {
                sortedList.add(ride);
            }
        }

        if (!sortedList.isEmpty()) {
            Collections.sort(sortedList, (r1, r2) -> {
                int result;
                switch (currentSortBy) {
                    case ROUTE:
                        result = compareStringsSafe(buildRouteString(r1), buildRouteString(r2));
                        break;
                    case DATE:
                    case START_TIME:
                        result = compareStringsSafe(r1.getStartTime(), r2.getStartTime());
                        break;
                    case END_TIME:
                        result = compareStringsSafe(r1.getEndTime(), r2.getEndTime());
                        break;
                    default:
                        result = 0;
                }
                return sortAscending ? result : -result;
            });
        }

        _rides.setValue(sortedList);
    }

    private int compareStringsSafe(String s1, String s2) {
        if (s1 == s2) return 0;
        if (s1 == null) return 1;
        if (s2 == null) return -1;
        return s1.compareTo(s2);
    }

    private String buildRouteString(PassengerRideHistoryDTO r) {
        String start = r.getStartAddress() != null ? r.getStartAddress() : "";
        String route = r.getRoute() != null ? r.getRoute() : "";
        String end = r.getEndAddress() != null ? r.getEndAddress() : "";
        return (start + route + end).toLowerCase();
    }

    private boolean isRideInRange(PassengerRideHistoryDTO ride) {
        if (currentFromDate == null && currentToDate == null) return true;

        String startTime = ride.getStartTime();
        if (startTime == null || startTime.length() < 10) return false;

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
                            Log.d(TAG, "Loaded ride details for ID: " + rideId);
                        } else {
                            _error.setValue("Failed to load ride details: " + response.code());
                            Log.e(TAG, "Error loading ride details: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<PassengerRideDetailsDTO> call, Throwable t) {
                        _loading.setValue(false);
                        _error.setValue("Network error: " + t.getMessage());
                        Log.e(TAG, "Network error loading ride details", t);
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


    public void setSortBy(PassengerRideSortBy sortBy) {
        this.currentSortBy = sortBy;
        applyLocalFilterAndSort();
    }


    public void toggleSortDirection() {
        this.sortAscending = !this.sortAscending;
        applyLocalFilterAndSort();
    }


    public void setSortDirection(boolean ascending) {
        this.sortAscending = ascending;
        applyLocalFilterAndSort();
    }


    public boolean isSortAscending() {
        return sortAscending;
    }

}
