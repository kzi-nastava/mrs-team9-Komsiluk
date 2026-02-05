package com.komsiluk.taxi.ui.add_driver;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.komsiluk.taxi.data.remote.add_driver.UserTokenActivationRequest;
import com.komsiluk.taxi.data.remote.add_driver.UserTokenService;
import com.komsiluk.taxi.util.Event;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class DriverActivationViewModel extends ViewModel {

    private final UserTokenService userTokenService;

    private final MutableLiveData<Event<String>> successEvent = new MutableLiveData<>();
    private final MutableLiveData<Event<String>> errorEvent = new MutableLiveData<>();

    @Inject
    public DriverActivationViewModel(UserTokenService userTokenService) {
        this.userTokenService = userTokenService;
    }

    public LiveData<Event<String>> getSuccessEvent() { return successEvent; }
    public LiveData<Event<String>> getErrorEvent() { return errorEvent; }

    public void activate(String token, String password) {
        UserTokenActivationRequest req = new UserTokenActivationRequest(token, password);

        userTokenService.activate(req).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    successEvent.setValue(new Event<>("OK"));
                } else {
                    errorEvent.setValue(new Event<>("Activation failed (" + response.code() + ")"));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                errorEvent.setValue(new Event<>("Unable to connect."));
            }
        });
    }
}
