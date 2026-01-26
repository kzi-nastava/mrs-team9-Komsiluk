package com.komsiluk.taxi.ui.auth;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.komsiluk.taxi.data.remote.auth.ActivatePassengerRequest;
import com.komsiluk.taxi.data.remote.auth.AuthService;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class AuthViewModel extends ViewModel {

    AuthService authService;

    @Inject
    public AuthViewModel(AuthService authService) {
        this.authService = authService;
    }


    public enum ActivationState {
        IDLE, LOADING, SUCCESS, ERROR
    }

    private MutableLiveData<ActivationState> activationState = new MutableLiveData<>(ActivationState.IDLE);

    public LiveData<ActivationState> getActivationState() {
        return activationState;
    }

    public void activatePassenger(String token) {
        ActivatePassengerRequest request = new ActivatePassengerRequest();
        request.setToken(token);

        activationState.setValue(ActivationState.LOADING);

        Call<Void> call = authService.activatePassenger(request);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    activationState.postValue(ActivationState.SUCCESS);
                } else {
                    activationState.postValue(ActivationState.ERROR);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                activationState.postValue(ActivationState.ERROR);
            }
        });
    }

}
