package com.komsiluk.taxi.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.komsiluk.taxi.BuildConfig;
import com.komsiluk.taxi.data.remote.profile.UserProfileResponse;
import com.komsiluk.taxi.data.remote.profile.UserService;
import com.komsiluk.taxi.data.session.SessionManager;
import com.komsiluk.taxi.util.Event;

import java.io.File;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
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
    private final MutableLiveData<Event<String>> imageUploadError = new MutableLiveData<>();
    private final MutableLiveData<Event<String>> imageUploadSuccess = new MutableLiveData<>();

    private final MutableLiveData<Long> imageVersion = new MutableLiveData<>(0L);
    public LiveData<Long> getImageVersion() { return imageVersion; }


    @Inject
    public ProfileViewModel(UserService userService, SessionManager sessionManager) {
        this.userService = userService;
        this.sessionManager = sessionManager;
    }

    public LiveData<UserProfileResponse> getProfile() { return profileLiveData; }
    public LiveData<Event<String>> getErrorMessage() { return errorMessageEvent; }
    public LiveData<Boolean> getLoading() { return loading; }
    public LiveData<Event<String>> getImageUploadError() { return imageUploadError; }
    public LiveData<Event<String>> getImageUploadSuccess() { return imageUploadSuccess; }

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

    public String buildAbsoluteImageUrl(String relativeOrFull, boolean bustCache) {
        if (relativeOrFull == null || relativeOrFull.trim().isEmpty()) return null;

        String full;
        if (relativeOrFull.startsWith("http://") || relativeOrFull.startsWith("https://")) {
            full = relativeOrFull;
        } else {
            String base = "http://" + BuildConfig.IP_ADDR + ":8081";
            if (relativeOrFull.startsWith("/")) full = base + relativeOrFull;
            else full = base + "/" + relativeOrFull;
        }

        if (!bustCache) return full;

        String sep = full.contains("?") ? "&" : "?";
        return full + sep + "t=" + System.currentTimeMillis();
    }


    public void updateProfileImage(File imageFile) {
        Long userId = sessionManager.getUserId();
        if (userId == null) {
            imageUploadError.setValue(new Event<>("Not logged in."));
            return;
        }

        if (imageFile == null || !imageFile.exists()) {
            imageUploadError.setValue(new Event<>("Invalid image file."));
            return;
        }

        RequestBody rb = RequestBody.create(
                MediaType.parse("image/*"),
                imageFile
        );

        MultipartBody.Part part = MultipartBody.Part.createFormData(
                "image",
                imageFile.getName(),
                rb
        );

        userService.updateProfileImage(userId, part)
            .enqueue(new Callback<UserProfileResponse>() {
                @Override
                public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {

                        UserProfileResponse current = profileLiveData.getValue();
                        if (current != null) {
                            profileLiveData.setValue(response.body());
                        }

                        imageVersion.setValue(System.currentTimeMillis());
                        imageUploadSuccess.setValue(new Event<>("Profile image updated."));
                    } else {
                        imageUploadError.setValue(new Event<>("Upload failed (" + response.code() + ")"));
                    }
                }

                @Override
                public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                    imageUploadError.setValue(new Event<>("Network error. Check connection/server."));
                }
            });
    }
}
