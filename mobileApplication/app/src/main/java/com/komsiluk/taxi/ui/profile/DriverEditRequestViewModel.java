package com.komsiluk.taxi.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.komsiluk.taxi.data.remote.edit_requests.ProfileChangeRequestCreate;
import com.komsiluk.taxi.data.remote.edit_requests.ProfileChangeRequestResponse;
import com.komsiluk.taxi.data.remote.profile.UserService;
import com.komsiluk.taxi.data.remote.profile.UserProfileResponse;
import com.komsiluk.taxi.data.session.SessionManager;
import com.komsiluk.taxi.util.Event;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class DriverEditRequestViewModel extends ViewModel {

    private final UserService userService;
    private final SessionManager sessionManager;

    private final MutableLiveData<UserProfileResponse> currentProfile = new MutableLiveData<>();
    private final MutableLiveData<Event<String>> errorMessageEvent = new MutableLiveData<>();
    private final MutableLiveData<Event<ProfileChangeRequestResponse>> submitSuccessEvent = new MutableLiveData<>();

    public LiveData<UserProfileResponse> getCurrentProfile() { return currentProfile; }
    public LiveData<Event<String>> getErrorMessageEvent() { return errorMessageEvent; }
    public LiveData<Event<ProfileChangeRequestResponse>> getSubmitSuccessEvent() { return submitSuccessEvent; }

    @Inject
    public DriverEditRequestViewModel(UserService profileService, SessionManager sessionManager) {
        this.userService = profileService;
        this.sessionManager = sessionManager;
    }

    public void fetchProfile() {
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

    public void submitRequest(ProfileChangeRequestCreate dto) {
        Long driverId = sessionManager.getUserId();
        if (driverId == null) {
            errorMessageEvent.setValue(new Event<>("Missing user session."));
            return;
        }

        userService.createDriverEditRequest(driverId, dto)
            .enqueue(new Callback<ProfileChangeRequestResponse>() {
                @Override
                public void onResponse(Call<ProfileChangeRequestResponse> call, Response<ProfileChangeRequestResponse> response) {
                    if (response.isSuccessful()) {
                        submitSuccessEvent.setValue(new Event<>(response.body()));
                    } else {
                        errorMessageEvent.setValue(new Event<>("Request rejected. Check your inputs."));
                    }
                }

                @Override
                public void onFailure(Call<ProfileChangeRequestResponse> call, Throwable t) {
                    errorMessageEvent.setValue(new Event<>("Unable to connect."));
                }
            });
    }
}
