package com.komsiluk.taxi.ui.auth.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.komsiluk.taxi.data.remote.auth.AuthService;
import com.komsiluk.taxi.data.remote.auth.ForgotPasswordRequest;
import com.komsiluk.taxi.data.remote.auth.ResetPasswordRequest;
import com.komsiluk.taxi.util.Event;

import org.json.JSONObject;

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
                if (response.isSuccessful()) {
                    successEvent.setValue(new Event<>(true));
                } else {
                    String backendMessage = parseErrorMessage(response);

                    if (response.code() == 404) {
                        errorMessageEvent.setValue(
                                new Event<>("Reset failed. Invalid or expired token.")
                        );
                    } else {
                        errorMessageEvent.setValue(new Event<>(backendMessage));
                    }
                }
            }


            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                errorMessageEvent.postValue(new Event<>("Unable to connect. Please check your internet connection."));
            }
        });
    }

    private String parseErrorMessage(Response<?> response) {
        try {
            if (response.errorBody() != null) {
                String body = response.errorBody().string();
                try {
                    JSONObject obj = new JSONObject(body);
                    return obj.optString("message", body); // koristi plain text ako nema message polja
                } catch (Exception e) {
                    return body; // plain text
                }
            }
        } catch (Exception e) {
            return "Unexpected error occurred";
        }
        return "Unknown error";
    }

}
