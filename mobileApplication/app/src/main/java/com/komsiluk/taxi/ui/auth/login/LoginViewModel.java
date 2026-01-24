package com.komsiluk.taxi.ui.auth.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.komsiluk.taxi.auth.UserRole;
import com.komsiluk.taxi.data.remote.auth.AuthService;
import com.komsiluk.taxi.data.remote.auth.LoginRequest;
import com.komsiluk.taxi.data.remote.auth.LoginResponse;
import com.komsiluk.taxi.data.session.SessionManager;
import com.komsiluk.taxi.util.Event;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class LoginViewModel extends ViewModel {


    private final MutableLiveData<Event<UserRole>> loginResultEvent = new MutableLiveData<>();


    private final MutableLiveData<Event<String>> errorMessageEvent = new MutableLiveData<>();

    public LiveData<Event<UserRole>> getLoginSuccess() { return loginResultEvent; }

    public LiveData<Event<String>> getErrorMessage() {return errorMessageEvent;}


    private AuthService authService;

    private SessionManager sessionManager;

    @Inject
    public LoginViewModel(
            AuthService authService,
            SessionManager sessionManager
    ) {
        this.authService = authService;
        this.sessionManager = sessionManager;
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
                errorMessageEvent.postValue(new Event<>("Unable to connect. Please check your internet connection."));
            }
        });
    }

}
