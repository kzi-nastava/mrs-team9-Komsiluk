package com.komsiluk.taxi.ui.auth.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.komsiluk.taxi.data.remote.auth.AuthService;
import com.komsiluk.taxi.data.remote.auth.ForgotPasswordRequest;
import com.komsiluk.taxi.data.remote.auth.ResetPasswordRequest;
import com.komsiluk.taxi.util.Event;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class ResetPasswordViewModel extends ViewModel {

    private MutableLiveData<Event<Boolean>> successEvent = new MutableLiveData<>();

    private MutableLiveData<Event<String>> errorMessageEvent = new MutableLiveData<>();

    public LiveData<Event<Boolean>> getSuccessEvent() { return this.successEvent;}

    public LiveData<Event<String>> getErrorMessageEvent() { return this.errorMessageEvent; }

    private AuthService authService;

    @Inject
    public ResetPasswordViewModel(AuthService authService) { this.authService = authService;}

    public void resetPassword(String token, String newPassword, String confirmPassword) {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken(token);
        request.setNewPassword(newPassword);
        request.setConfirmPassword(confirmPassword);

        Call<Void> call = authService.resetPassword(request);
        call.enqueue(new Callback<Void>() {

            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    successEvent.setValue(new Event<>(true));
                } else {
                    errorMessageEvent.setValue(new Event<>("Problem with connection occured. Code: " + response.code()
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
