package com.komsiluk.taxi.ui.auth.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.komsiluk.taxi.data.remote.auth.AuthService;
import com.komsiluk.taxi.data.remote.auth.ForgotPasswordRequest;
import com.komsiluk.taxi.util.Event;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class ForgotPasswordViewModel extends ViewModel {

    private MutableLiveData<Event<Boolean>> successEvent = new MutableLiveData<>();

    private MutableLiveData<Event<String>> errorMessageEvent = new MutableLiveData<>();

    public LiveData<Event<Boolean>> getSuccessEvent() { return this.successEvent;}

    public LiveData<Event<String>> getErrorMessageEvent() { return this.errorMessageEvent; }

    private AuthService authService;

    @Inject
    public ForgotPasswordViewModel(AuthService authService) { this.authService = authService;}

    public void forgotPassword(String email) {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail(email);

        Call<Void> call = authService.forgotPassword(request);
        call.enqueue(new Callback<Void>() {

            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    successEvent.setValue(new Event<>(true));
                } else {
                    errorMessageEvent.setValue(new Event<>("Failed to fetch product. Code: " + response.code()
                            + ", message: " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                errorMessageEvent.postValue(new Event<>(t.getMessage()));
            }
        });
    }
}
