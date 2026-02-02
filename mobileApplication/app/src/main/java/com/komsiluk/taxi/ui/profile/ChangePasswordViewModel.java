package com.komsiluk.taxi.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.komsiluk.taxi.data.remote.profile.UserService;
import com.komsiluk.taxi.data.remote.profile.UserChangePasswordRequest;
import com.komsiluk.taxi.data.session.SessionManager;
import com.komsiluk.taxi.util.Event;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@HiltViewModel
public class ChangePasswordViewModel extends ViewModel {

    private final MutableLiveData<Event<String>> successEvent = new MutableLiveData<>();
    private final MutableLiveData<Event<String>> errorEvent = new MutableLiveData<>();

    public LiveData<Event<String>> getSuccessEvent() { return successEvent; }
    public LiveData<Event<String>> getErrorEvent() { return errorEvent; }

    private final UserService userService;
    private final SessionManager sessionManager;

    @Inject
    public ChangePasswordViewModel(UserService userService, SessionManager sessionManager) {
        this.userService = userService;
        this.sessionManager = sessionManager;
    }

    public void changePassword(String oldPassword, String newPassword) {
        Long userId = sessionManager.getUserId();
        if (userId == null) {
            errorEvent.setValue(new Event<>("Not logged in."));
            return;
        }

        UserChangePasswordRequest req = new UserChangePasswordRequest(oldPassword, newPassword);

        userService.changePassword(userId, req).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // backend vraca 204 NO_CONTENT => ovde je success
                    successEvent.setValue(new Event<>("Password changed successfully."));
                } else if (response.code() == 400 || response.code() == 401) {
                    // najcesce pogresan current password ili validation
                    errorEvent.setValue(new Event<>("Current password is incorrect."));
                } else {
                    errorEvent.setValue(new Event<>("Failed to change password (" + response.code() + ")."));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                errorEvent.setValue(new Event<>("Network error."));
            }
        });
    }
}
