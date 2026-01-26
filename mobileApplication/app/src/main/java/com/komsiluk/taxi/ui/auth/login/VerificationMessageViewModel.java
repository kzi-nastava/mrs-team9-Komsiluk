package com.komsiluk.taxi.ui.auth.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.komsiluk.taxi.data.remote.auth.AuthService;
import com.komsiluk.taxi.data.remote.auth.ResendEmailRequest;
import com.komsiluk.taxi.data.remote.auth.ResetPasswordRequest;
import com.komsiluk.taxi.util.Event;

import org.json.JSONObject;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class VerificationMessageViewModel extends ViewModel {

    private MutableLiveData<Event<Boolean>> successEvent = new MutableLiveData<>();

    private MutableLiveData<Event<String>> errorMessageEvent = new MutableLiveData<>();

    public LiveData<Event<Boolean>> getSuccessEvent() { return this.successEvent;}

    public LiveData<Event<String>> getErrorMessageEvent() { return this.errorMessageEvent; }

    AuthService authService;

    @Inject
    public VerificationMessageViewModel(AuthService authService) {
        this.authService = authService;
    }




    public void resendEmail(String email) {
        ResendEmailRequest request = new ResendEmailRequest();
        request.setEmail(email);

        Call<Void> call = authService.resendEmail(request);
        call.enqueue(new Callback<Void>() {

            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    successEvent.setValue(new Event<>(true));
                } else {
                    String backendMessage = parseErrorMessage(response);
                    errorMessageEvent.setValue(
                            new Event<>("Resend email failed." + backendMessage));
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
