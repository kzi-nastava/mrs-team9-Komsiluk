package com.komsiluk.taxi.ui.auth.login;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.komsiluk.taxi.auth.UserRole;
import com.komsiluk.taxi.data.remote.auth.AuthService;
import com.komsiluk.taxi.data.remote.auth.LoginRequest;
import com.komsiluk.taxi.data.remote.auth.LoginResponse;
import com.komsiluk.taxi.data.remote.location.DriverLocationUpdate;
import com.komsiluk.taxi.data.remote.location.LocationService;
import com.komsiluk.taxi.data.session.SessionManager;
import com.komsiluk.taxi.util.Event;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class LoginViewModel extends ViewModel {


    public final MutableLiveData<String> email = new MutableLiveData<>("");

    public final MutableLiveData<String> password = new MutableLiveData<>("");

    private final MutableLiveData<Event<UserRole>> loginResultEvent = new MutableLiveData<>();


    private final MutableLiveData<Event<String>> errorMessageEvent = new MutableLiveData<>();

    public LiveData<Event<UserRole>> getLoginSuccess() { return loginResultEvent; }

    public LiveData<Event<String>> getErrorMessage() {return errorMessageEvent;}


    private AuthService authService;

    private SessionManager sessionManager;

    private  LocationService locationService;

    @Inject
    public LoginViewModel(
            AuthService authService,
            SessionManager sessionManager,
            LocationService locationService
    ) {
        this.authService = authService;
        this.sessionManager = sessionManager;
        this.locationService = locationService;
    }

    public void login(String email, String password) {
        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword(password);

        Call<LoginResponse> call = authService.login(request);
        call.enqueue(new Callback<LoginResponse>() {

            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    sessionManager.saveSession(response.body().getToken(),response.body().getId(),response.body().getRole());
                    loginResultEvent.setValue(new Event<>(response.body().getRole()));
                } else {
                    errorMessageEvent.postValue(new Event<>("Login failed. Invalid credentials!"));
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("LOGIN", "Network fail", t);
                errorMessageEvent.postValue(new Event<>("FAIL: " + t.getClass().getSimpleName() + " - " + t.getMessage()));
            }

        });
    }

    public void sendInitialLocation(double lat, double lng) {
        Long driverId = sessionManager.getUserId();
        if (driverId == -1L) return;
        DriverLocationUpdate dto = new DriverLocationUpdate(lat,lng);

        locationService.updateLocation(driverId, dto).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d("START_LOCATION", "Initial location sent to Uspenska St.");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("START_LOCATION", "Failed to send initial location");
            }
        });
    }
}
