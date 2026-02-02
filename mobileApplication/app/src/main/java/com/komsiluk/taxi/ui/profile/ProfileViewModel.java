package com.komsiluk.taxi.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.komsiluk.taxi.data.remote.profile.UserProfileResponse;
import com.komsiluk.taxi.data.remote.profile.UserService;
import com.komsiluk.taxi.data.session.SessionManager;
import com.komsiluk.taxi.util.Event;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class ProfileViewModel extends ViewModel {

    private final UserService userService;
    private final SessionManager sessionManager;

    private final MutableLiveData<UserProfileResponse> profileLiveData = new MutableLiveData<>();
    private final MutableLiveData<Event<String>> errorMessageEvent = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);

    @Inject
    public ProfileViewModel(UserService userService, SessionManager sessionManager) {
        this.userService = userService;
        this.sessionManager = sessionManager;
    }

    public LiveData<UserProfileResponse> getProfile() { return profileLiveData; }
    public LiveData<Event<String>> getErrorMessage() { return errorMessageEvent; }
    public LiveData<Boolean> getLoading() { return loading; }

    public void fetchProfile() {
        Long id = sessionManager.getUserId();
        if (id == null) {
            errorMessageEvent.setValue(new Event<>("No session. Please login again."));
            return;
        }

        loading.setValue(true);

        userService.getProfile(id).enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                loading.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    profileLiveData.setValue(response.body());
                } else {
                    errorMessageEvent.setValue(new Event<>("Failed to load profile."));
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                loading.setValue(false);
                errorMessageEvent.setValue(new Event<>("Network error while loading profile."));
            }
        });
    }
}
