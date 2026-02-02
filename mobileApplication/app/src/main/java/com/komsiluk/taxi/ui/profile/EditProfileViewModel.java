package com.komsiluk.taxi.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.komsiluk.taxi.data.remote.profile.UserService;
import com.komsiluk.taxi.data.remote.profile.UserProfileResponse;
import com.komsiluk.taxi.data.remote.profile.UserProfileUpdateRequest;
import com.komsiluk.taxi.data.session.SessionManager;
import com.komsiluk.taxi.util.Event;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class EditProfileViewModel extends ViewModel {

    private final UserService userService;
    private final SessionManager sessionManager;

    private final MutableLiveData<UserProfileResponse> currentProfile = new MutableLiveData<>();

    private final MutableLiveData<Event<UserProfileResponse>> updateSuccessEvent = new MutableLiveData<>();
    private final MutableLiveData<Event<String>> errorMessageEvent = new MutableLiveData<>();

    public LiveData<UserProfileResponse> getCurrentProfile() { return currentProfile; }
    public LiveData<Event<UserProfileResponse>> getUpdateSuccessEvent() { return updateSuccessEvent; }
    public LiveData<Event<String>> getErrorMessageEvent() { return errorMessageEvent; }

    @Inject
    public EditProfileViewModel(UserService userService, SessionManager sessionManager) {
        this.userService = userService;
        this.sessionManager = sessionManager;
    }

    public void loadCurrentProfile() {
        Long id = sessionManager.getUserId();
        if (id == null) {
            errorMessageEvent.setValue(new Event<>("Missing user session."));
            return;
        }

        userService.getProfile(id).enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentProfile.setValue(response.body());
                } else {
                    errorMessageEvent.setValue(new Event<>("Unable to load profile."));
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                errorMessageEvent.setValue(new Event<>("Unable to connect."));
            }
        });
    }

    public void updateProfile(UserProfileUpdateRequest request) {
        Long id = sessionManager.getUserId();
        if (id == null) {
            errorMessageEvent.setValue(new Event<>("Missing user session."));
            return;
        }

        userService.updateProfile(id, request).enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateSuccessEvent.setValue(new Event<>(response.body()));
                } else {
                    errorMessageEvent.setValue(new Event<>("Update failed."));
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                errorMessageEvent.setValue(new Event<>("Unable to connect."));
            }
        });
    }
}
